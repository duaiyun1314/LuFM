package com.andy.LuFM.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.app.AllInOneActivity;
import com.andy.LuFM.controller.ControllerManager;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.event.SwitchContentEvent;
import com.andy.LuFM.model.Attribute;
import com.andy.LuFM.model.Attributes;
import com.andy.LuFM.model.CategoryNode;

import java.util.List;

import roboguice.util.Ln;

/**
 * Created by Andy.Wang on 2016/3/2.
 */
public class RegionListFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView mListView;
    private ImageButton iv_back;
    private TextView title_label;
    private String type;
    private CategoryNode categoryNode;
    private MYAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_list_fragment, null, false);
        iv_back = (ImageButton) view.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        title_label = (TextView) view.findViewById(R.id.title_label);
        mListView = (ListView) view.findViewById(R.id.content_list);
        myAdapter = new MYAdapter(getActivity());
        mListView.setAdapter(myAdapter);
        title_label.setText("省市台");
        mListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Attributes> lstAttributes = InfoManager.getInstance().root().mLiveNode.getLstAttributes();
        List<Attribute> attributeList = null;
        for (Attributes attributes : lstAttributes) {
            if (attributes.name.equalsIgnoreCase("地区")) {
                attributeList = attributes.mLstAttribute;
            }
        }
        myAdapter.setData(attributeList);

    }

    public void update(String type, Object param) {
        this.type = type;
        this.categoryNode = (CategoryNode) param;
    }

    @Override
    public void onClick(View v) {
        AllInOneActivity allInOneActivity = (AllInOneActivity) getContext();
        allInOneActivity.onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Attribute attribute = (Attribute) myAdapter.getItem(position);
        categoryNode.setmAttributesPath(attribute.id + "");
        categoryNode.setCategoryId(5);
        ControllerManager.getInstance(getContext()).openChannelListView(SwitchContentEvent.SWITCH_TYPE_CHANNEL_LIST, categoryNode);

    }

    public class MYAdapter extends BaseAdapter {
        private List<Attribute> list;
        private Context context;

        public MYAdapter(Context context) {
            this.context = context;

        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list == null ? null : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.layout_regionlst_item, null);
                holder.regionName = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Attribute attribute = list.get(position);
            holder.regionName.setText(attribute.name);
            return convertView;
        }

        public void setData(List<Attribute> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        private class ViewHolder {
            TextView regionName;

        }
    }
}

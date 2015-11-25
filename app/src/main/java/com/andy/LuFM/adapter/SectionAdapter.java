package com.andy.LuFM.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.andy.LuFM.R;
import com.andy.LuFM.Utils.ViewFactory;
import com.andy.LuFM.model.SectionItem;
import com.andy.LuFM.view.LView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * Created by wanglu on 15/11/16.
 */
public class SectionAdapter extends BaseAdapter {
    private ViewFactory factory;
    private DisplayImageOptions options;

    public SectionAdapter(List<SectionItem> data, ViewFactory factory) {
        this.data = data;
        this.factory = factory;

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.recommend_defaultbg)
                .showImageOnFail(R.drawable.recommend_defaultbg)
                .build();
    }

    private List<SectionItem> data;

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SectionItem item = data.get(position);
        LView lView = (LView) this.factory.createView(item.type);
        lView.update(item, options);
        return lView;
    }

    public void setData(List<SectionItem> items) {
        this.data = items;
        notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(int position) {
        SectionItem item = data.get(position);
        if (item.type == SectionItem.TYPE_TAG) {
            return false;
        } else {
            return true;
        }
    }
}

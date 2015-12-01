package com.andy.LuFM.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.view.channeldetail.ChannelDetailView;

/**
 * Created by wanglu on 15/11/23.
 */
public class ChannelFragment extends Fragment implements View.OnClickListener {
    private ChannelDetailView detailView;
    private Object obj;
    private String type;
    private ImageView iv_back;
    private TextView title_label;

    public ChannelFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("Sync", "channeldetai fragment onAttach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_channel_detail, null);
        detailView = (ChannelDetailView) view.findViewById(R.id.detail_view);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        title_label = (TextView) view.findViewById(R.id.title_label);
        iv_back.setOnClickListener(this);
        if (obj != null) {
            // detailView.update(type, obj);
            setData(type, obj);
        }
        return view;
    }

    public void setData(String type, Object param) {
        obj = param;
        this.type = type;
        if (detailView != null) {
            detailView.update(type, param);
            updateTitle((ChannelNode) param);
        }
    }

    private void updateTitle(ChannelNode param) {

        title_label.setText(param.title);
    }

    @Override
    public void onClick(View v) {
        getActivity().onBackPressed();
    }
}

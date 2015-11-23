package com.andy.LuFM.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andy.LuFM.R;
import com.andy.LuFM.view.channeldetail.ChannelDetailView;

/**
 * Created by wanglu on 15/11/23.
 */
public class ChannelFragment extends Fragment {
    private ChannelDetailView detailView;
    private Object obj;
    private String type;

    public ChannelFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_channel_detail, null);
        detailView = (ChannelDetailView) view.findViewById(R.id.detail_view);
        if (obj != null) {
            detailView.update(type, obj);
        }
        return view;
    }

    public void setData(String type, Object param) {
        obj = param;
        this.type = type;
        if (detailView != null) {
            detailView.update(type, param);
        }
    }
}

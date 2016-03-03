package com.andy.LuFM.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andy.LuFM.view.channeldetail.ChannelDetailView;

/**
 * Created by Andy.Wang on 2016/3/2.
 */
public class ChannelDetailFragment extends Fragment {
    private ChannelDetailView channelDetailView;
    private String type;
    private Object param;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        channelDetailView = new ChannelDetailView(getActivity());
        return channelDetailView;
    }

    @Override
    public void onResume() {
        super.onResume();
        channelDetailView.update(type, param);
    }

    public void update(String type, Object param) {
        this.type = type;
        this.param = param;
        if (channelDetailView == null) return;
        channelDetailView.update(type, param);
    }
}

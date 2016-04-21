package com.andy.LuFM.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andy.LuFM.view.LiveChannelListView;
import com.andy.LuFM.view.topicview.SpecialTopicView;

/**
 * Created by Andy.Wang on 2016/3/2.
 */
public class ChannelListFragment extends Fragment {
    private LiveChannelListView liveChannelListView;
    private String type;
    private Object param;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        liveChannelListView = new LiveChannelListView(getActivity());
        return liveChannelListView;
    }

    @Override
    public void onResume() {
        super.onResume();
        liveChannelListView.update(type, param);
    }

    public void update(String type, Object param) {
        this.type = type;
        this.param = param;
        if (liveChannelListView == null) return;
        liveChannelListView.update(type, param);
    }
}

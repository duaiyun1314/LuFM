package com.andy.LuFM.controller;

import android.view.View;
import android.widget.FrameLayout;

import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.CategoryNode;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.SpecialTopicNode;
import com.andy.LuFM.providers.LiveChannelsProvider;
import com.andy.LuFM.providers.TopicChannelsProvider;
import com.andy.LuFM.view.LiveChannelListView;
import com.andy.LuFM.view.TopicHeaderView;
import com.andy.LuFM.view.topicview.SpecialTopicView;

import java.util.List;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class ChannelListController4Live<Prodiver extends LiveChannelsProvider> extends BaseListController<Prodiver, FrameLayout> {

    private CategoryNode categoryNode;

    public ChannelListController4Live(Prodiver provider) {
        super(provider);
    }

    @Override
    public void assumeView(View view) {
        super.assumeView(view);
        this.loader.setUserNead(false);
    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        mProvider.loadData(mProvider.getParam());
    }

    @Override
    public void onLoadSuccess(Object object) {
        super.onLoadSuccess(object);
        String type = (String) object;
        if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_LIVE_CHANNELS_BYATTR)) {
            CategoryNode categoryNode = (CategoryNode) mProvider.getParam();
            List<ChannelNode> list = ChannelHelper.getInstance().getLstChannelsByKey(ChannelHelper.buildKey(categoryNode.categoryId, categoryNode.getmAttributesPath()));
            if (list != null && list.size() > 0) {
                if (baseView.getParent() instanceof LiveChannelListView) {
                    LiveChannelListView liveChannelListView = (LiveChannelListView) baseView.getParent();
                    liveChannelListView.setData(list);
                }

            }

        }
        super.onLoadFinish(30);
    }

    @Override
    protected boolean setLoaderEnable() {
        return true;
    }

    @Override
    protected boolean setHeaderClickable() {
        return false;
    }
}

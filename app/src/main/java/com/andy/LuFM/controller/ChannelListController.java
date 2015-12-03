package com.andy.LuFM.controller;

import android.view.View;
import android.widget.FrameLayout;

import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.SpecialTopicNode;
import com.andy.LuFM.providers.RecommendListProvider;
import com.andy.LuFM.providers.TopicChannelsProvider;
import com.andy.LuFM.view.BaseRecommendView;
import com.andy.LuFM.view.SwitchView;
import com.andy.LuFM.view.TopicHeaderView;
import com.andy.LuFM.view.topicview.SpecialTopicView;

import java.util.List;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class ChannelListController<Prodiver extends TopicChannelsProvider> extends BaseListController<Prodiver, FrameLayout> {

    public TopicHeaderView mHeadView;
    private SpecialTopicNode topicNode;

    public ChannelListController(Prodiver provider) {
        super(provider);
    }

    @Override
    public void assumeView(View view) {
        super.assumeView(view);
        this.loader.setUserNead(false);
    }

    @Override
    protected View createHeadView() {
        mHeadView = new TopicHeaderView(mContext);
        mHeadView.setFocusable(false);
        mHeadView.setClickable(false);
        return mHeadView;
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
        if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_SPECIAL_TOPIC_CHANNELS)) {
            List<ChannelNode> list = ChannelHelper.getInstance().getLstChannelsByKey(((SpecialTopicNode) mProvider.getParam()).getKey());
            if (list != null && list.size() > 0) {
                if (baseView.getParent() instanceof SpecialTopicView) {
                    SpecialTopicView specialTopicView = (SpecialTopicView) baseView.getParent();
                    specialTopicView.setData(list);
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

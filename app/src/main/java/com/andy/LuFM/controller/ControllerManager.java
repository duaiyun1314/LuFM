package com.andy.LuFM.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.andy.LuFM.app.NowPlayingActivity;
import com.andy.LuFM.event.SwitchContentEvent;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.RecommendPlayingItemNode;
import com.andy.LuFM.model.SpecialTopicNode;

import de.greenrobot.event.EventBus;

/**
 * Created by wanglu on 15/11/20.
 */
public class ControllerManager {
    private static ControllerManager instance;
    private int mChannelSource;
    private Context context;

    private ControllerManager(Context context) {
        this.context = context;
    }

    public static ControllerManager getInstance(Context context) {
        if (instance == null) {
            instance = new ControllerManager(context);

        }
        return instance;
    }

    public void openControllerByRecommendNode(Node node) {
        if (node != null && node.nodeName.equalsIgnoreCase("recommenditem")) {
            RecommendItemNode p = (RecommendItemNode) node;
            if (p != null) {
            }
            p.mClickCnt++;
            if (p.mNode != null && !p.mNode.nodeName.equalsIgnoreCase("category")) {
                Log.i("Sync", "点击的类型：" + p.mNode.nodeName);
                if (p.mNode.nodeName.equalsIgnoreCase("channel")) {
                  /*  ChannelNode cn = p.mNode;
                    if (p.ratingStar != -1) {
                        cn.ratingStar = p.ratingStar;
                    }
                    if (cn.isNovelChannel()) {
                        getInstance().setChannelSource(1);
                        openNovelDetailView(p.mNode);
                    } else if (cn.channelType == 1) {
                        getInstance().setChannelSource(1);
                        openChannelDetailController(p.mNode);
                    } else {
                        redirectToPlayViewByNode(p.mNode, true);
                    }*/
                } else if (p.mNode.nodeName.equalsIgnoreCase("program")) {
                    openChannelDetailController((ProgramNode) p.mNode, true);
                } else if (p.mNode.nodeName.equalsIgnoreCase("activity")) {
                    /*MobclickAgent.onEvent(getContext(), "openActivityFromRecommend", p.name);
                    if (p.isAds && p.mAdNode != null) {
                        p.mAdNode.onClick();
                    }
                    redirectToActivityViewByNode(p.mNode);*/
                } else if (p.mNode.nodeName.equalsIgnoreCase("specialtopic")) {
                    openSpecialTopicController((SpecialTopicNode) p.mNode);
                }
            }
        }

    }

    /**
     * 打开直播的playing Activity
     *
     * @param playingItemNode
     */
    public void openPlayController(RecommendPlayingItemNode playingItemNode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(NowPlayingActivity.CHANNEL_MODE, playingItemNode);
        Intent intent = new Intent(context, NowPlayingActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void openSpecialTopicController(SpecialTopicNode mNode) {
        if (mNode != null) {
            String name = "specialtopic";
            redirect2View(name, mNode);
        }

    }

    public void openChannelDetailController(Node node, boolean openDamaku) {
        if (node != null) {
            String name = "channeldetail";
            String url;
            if (node.nodeName.equalsIgnoreCase("program")) {
                ChannelNode cNode = null;
                if (((ProgramNode) node).mLiveInVirtual) {
                    cNode = ChannelHelper.getInstance().getChannel(((ProgramNode) node).channelId, 1);
                } else {
                    cNode = ChannelHelper.getInstance().getChannel(((ProgramNode) node).channelId, ((ProgramNode) node).channelType);
                }
                if (cNode != null && cNode.ratingStar == -1) {
                    cNode.ratingStar = ((ProgramNode) node).channelRatingStar;
                }
                if (cNode == null) {
                    cNode = new ChannelNode();
                    cNode.channelId = ((ProgramNode) node).channelId;
                    cNode.channelType = ((ProgramNode) node).channelType;
                }
                redirect2View(name, cNode);
            } else if (node.nodeName.equalsIgnoreCase("channel")) {
                redirect2View(name, node);

            }
        }
    }

    public void redirect2View(String name, Object param) {
        SwitchContentEvent event = new SwitchContentEvent();
        event.type = name;
        event.params = param;
        EventBus.getDefault().post(event);
    }

    /**
     * 单独打开具体的类别视图
     *
     * @param item
     */
    public void openCatogoryView(Object item) {
        SwitchContentEvent event = new SwitchContentEvent();
        event.type = SwitchContentEvent.SWITCH_TYPE_CATEGORY_DETAIL;
        event.params = item;
        EventBus.getDefault().post(event);

    }
}

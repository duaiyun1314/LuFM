package com.andy.LuFM.controller;

import android.content.Context;
import android.util.Log;

import com.andy.LuFM.event.SwitchContentEvent;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.RecommendItemNode;
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
                    Node parent = p.parent;
                    boolean isFrontPage = false;
                    if (parent != null && parent.nodeName.equalsIgnoreCase("recommendcategory")) {
                        isFrontPage = ((RecommendCategoryNode) parent).isFrontpage();
                    }
                    if (isFrontPage) {
                        if (p.categoryPos == 0) {
                            PlayerAgent.getInstance().addPlaySource(21);
                        } else {
                            PlayerAgent.getInstance().addPlaySource(22);
                        }
                    } else if (p.categoryPos == 0) {
                        PlayerAgent.getInstance().addPlaySource(25);
                    } else {
                        PlayerAgent.getInstance().addPlaySource(36);
                    }
                    ((ProgramNode) p.mNode).setCategoryId(p.mCategoryId);
                    setChannelSource(1);
                    if (PlayerAgent.getInstance().isPlaying()) {
                        openChannelDetailController((ProgramNode) p.mNode, false, true);
                    } else {
                        openChannelDetailController((ProgramNode) p.mNode, true, true);
                    }
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

    private void openSpecialTopicController(SpecialTopicNode mNode) {
        if (mNode != null) {
            String name = "specialtopic";
            redirect2View(name, mNode);
        }

    }

    public void setChannelSource(int source) {
        this.mChannelSource = source;
    }

    public int getChannelSource() {
        return this.mChannelSource;
    }

    public void openChannelDetailController(Node node, boolean play, boolean openDamaku) {
        if (node != null) {
            String name = "channeldetail";
            String url;
            if (node.nodeName.equalsIgnoreCase("program")) {
                ChannelNode cNode = null;
                if (((ProgramNode) node).mLiveInVirtual) {
                    cNode = ChannelHelper.getInstance().getChannel(((ProgramNode) node).channelId, 1);
                    if (cNode == null) {
                        cNode = ChannelHelper.getInstance().getFakeVirtualChannel(((ProgramNode) node).channelId, ((ProgramNode) node).getCategoryId(), ((ProgramNode) node).title);
                    }
                } else {
                    cNode = ChannelHelper.getInstance().getChannel(((ProgramNode) node).channelId, ((ProgramNode) node).channelType);
                    if (cNode == null) {
                        cNode = ChannelHelper.getInstance().getFakeChannel(((ProgramNode) node).channelId, ((ProgramNode) node).getCategoryId(), ((ProgramNode) node).title, ((ProgramNode) node).channelType);
                    }
                }
                if (cNode.ratingStar == -1) {
                    cNode.ratingStar = ((ProgramNode) node).channelRatingStar;
                }
                redirect2View(name, cNode);
                /*if (openDamaku && InfoManager.getInstance().enableBarrage(((ProgramNode) node).channelId)) {
                    openDamakuPlayController();
                } else {
                    url = InfoManager.getInstance().h5Channel(cNode.channelId);
                    if (url == null || url.equalsIgnoreCase(bi.b)) {

                    } else {
                        redirectToActiviyByUrl(url, cNode.title, false);
                    }
                }
                if (play) {
                    if (!((ProgramNode) node).mLiveInVirtual) {
                        PlayerAgent.getInstance().play(node);
                    } else if (((ProgramNode) node).getCurrPlayStatus() != 2) {
                        PlayerAgent.getInstance().play(node);
                    }
                }*/
                // DoubleClick.getInstance().visitChannel(((ProgramNode) node).channelId, ((ProgramNode) node).getChannelName());
            } else if (node.nodeName.equalsIgnoreCase("channel")) {
                redirect2View(name, node);

            }
        }
    }

    public void redirect2View(String name, Object param) {
           /* ViewController controller = getController(name);
            controller.config(name, param);*/
        SwitchContentEvent event = new SwitchContentEvent();
        event.type = name;
        event.params = param;
        EventBus.getDefault().post(event);
        // pushControllerByProperAnimation(controller);
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

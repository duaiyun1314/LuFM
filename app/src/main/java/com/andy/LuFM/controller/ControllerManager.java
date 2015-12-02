package com.andy.LuFM.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.andy.LuFM.controller.viewcontroller.ChannelDetailController;
import com.andy.LuFM.controller.viewcontroller.ViewController;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.listener.ChannelDetailClickListener;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.RecommendItemNode;

/**
 * Created by wanglu on 15/11/20.
 */
public class ControllerManager {
    private static ControllerManager instance;
    private int mChannelSource;
    private Activity context;
    private ChannelDetailClickListener channelDetailClickListener;

    private ControllerManager(Activity context) {
        this.context = context;
    }

    public static ControllerManager getInstance(Activity context) {
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
                    //openSpecialTopicController((SpecialTopicNode) p.mNode);
                }
            }
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
               /* url = InfoManager.getInstance().h5Channel(((ChannelNode) node).channelId);
                if (url == null || url.equalsIgnoreCase(bi.b)) {
                    redirect2View(name, node);
                } else {
                    redirectToActiviyByUrl(url, ((ChannelNode) node).title, false);
                }
                if (play) {
                    ((ChannelNode) node).setAutoPlay(true);
                } else {
                    ((ChannelNode) node).setAutoPlay(false);
                }
                DoubleClick.getInstance().visitChannel(((ChannelNode) node).channelId, ((ChannelNode) node).title);
           */
            }
        }
    }

    public void redirect2View(String name, Object param) {
           /* ViewController controller = getController(name);
            controller.config(name, param);*/
        if (channelDetailClickListener != null) {
            channelDetailClickListener.onChannelSelected(name, param);
        }
        // pushControllerByProperAnimation(controller);
    }

    private ViewController getController(String type) {
        if (type.equalsIgnoreCase("channeldetail")) {
            return new ChannelDetailController(context);
        }
        return null;
    }

    public void unRegisterListener() {
        if (channelDetailClickListener != null) {
            channelDetailClickListener = null;
        }
    }

    /**
     * 通知activity switchFragment ，由于fragment的存在activity很有可能销毁了重建（eg. 按home键然后重新启动）这时候
     * listener指定的acitivity引用会改变导致回调时还是调用的指向的之前的acitivity，所以应该在activity创建时注册，销毁时解绑
     * 保证listener与当前运行的activity一致，避免出现activity has been destroyed
     *
     * @param context
     */
    public void RegisterListener(Activity context) {
        this.channelDetailClickListener = (ChannelDetailClickListener) context;

    }
}

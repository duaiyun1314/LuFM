package com.andy.LuFM.model;

import com.andy.LuFM.data.InfoManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanglu on 15/11/19.
 */
public class RootNode extends Node {
    public Map<Integer, RecommendCategoryNode> mapRecommendCategoryNode;
    private ChannelNode mPlayingChannelNode;


    public RootNode() {
        mapRecommendCategoryNode = new HashMap<>();
    }

    public RecommendCategoryNode getRecommendCategoryNode(int sectionId) {
        if (mapRecommendCategoryNode.containsKey(sectionId)) {
            return mapRecommendCategoryNode.get(sectionId);
        }
        return null;
    }

    public void setPlayingChannelNode(Node node) {
        if (node != null && node.nodeName.equalsIgnoreCase("channel") && this.mPlayingChannelNode != ((ChannelNode) node)) {
            this.mPlayingChannelNode = (ChannelNode) node;
            if (this.mPlayingChannelNode.hasEmptyProgramSchedule() && !this.mPlayingChannelNode.isDownloadChannel()) {
                InfoManager.getInstance().loadProgramsScheduleNode(this.mPlayingChannelNode, null);
            }
        }
    }

}

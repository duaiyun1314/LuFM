package com.andy.LuFM.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanglu on 15/11/19.
 */
public class RootNode extends Node {
    public Map<Integer, RecommendCategoryNode> mapRecommendCategoryNode;

    public RootNode() {
        mapRecommendCategoryNode = new HashMap<>();
    }

    public RecommendCategoryNode getRecommendCategoryNode(int sectionId) {
        if (mapRecommendCategoryNode.containsKey(sectionId)) {
            return mapRecommendCategoryNode.get(sectionId);
        }
        return null;
    }
}

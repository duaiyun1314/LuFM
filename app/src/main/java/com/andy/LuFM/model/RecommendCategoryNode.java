package com.andy.LuFM.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglu on 15/11/16.
 */
public class RecommendCategoryNode extends Node {
    public List<RecommendItemNode> lstBanner = new ArrayList<>();
    public List<List<RecommendItemNode>> lstRecMain = new ArrayList<>();

    public void insertItemNode(RecommendItemNode recommendItemNode, int pos) {
        if (pos == 1) {
            recommendItemNode.categoryPos = 1;
            for (int i = 0; i < lstRecMain.size(); i++) {
                if ((lstRecMain.get(i).get(0)).sectionId == recommendItemNode.sectionId) {
                    recommendItemNode.parent = this;
                    lstRecMain.get(i).add(recommendItemNode);
                    return;
                }
            }

            List<RecommendItemNode> lstNodes = new ArrayList<>();
            recommendItemNode.parent = this;
            lstNodes.add(recommendItemNode);
            lstRecMain.add(lstNodes);
        } else {
            if (lstBanner == null) {
                lstBanner = new ArrayList<>();

            }
            lstBanner.add(recommendItemNode);
        }
    }
}

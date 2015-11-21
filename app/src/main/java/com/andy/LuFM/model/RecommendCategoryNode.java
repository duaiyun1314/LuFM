package com.andy.LuFM.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglu on 15/11/16.
 */
public class RecommendCategoryNode extends Node {

    private transient boolean hasInsertedBannerAdvertisement;
    private transient boolean hasLoadAudioAdvertisement;
    private transient boolean hasRestored;
    private transient boolean hasRestoredSucc;
    public transient boolean hasUpdate;
    public List<RecommendItemNode> lstBanner;
    public List<List<RecommendItemNode>> lstRecMain;
    public String name;
    public int sectionId;

    public RecommendCategoryNode() {
        this.lstBanner = new ArrayList();
        this.lstRecMain = new ArrayList();
        this.sectionId = -1;
        this.name = "";
        this.hasRestoredSucc = false;
        this.hasRestored = false;
        this.hasUpdate = false;
        this.hasInsertedBannerAdvertisement = false;
        this.hasLoadAudioAdvertisement = false;
        this.nodeName = "recommendcategory";
    }

    public boolean isFrontpage() {
        if (this.sectionId == 0) {
            return true;
        }
        return false;
    }

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

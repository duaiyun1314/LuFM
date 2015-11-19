package com.andy.LuFM.controller;

import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.providers.RecommendListProvider;
import com.andy.LuFM.view.BaseRecommendView;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class RecommendListController<Prodiver extends RecommendListProvider> extends BaseRecommendListController<Prodiver, BaseRecommendView> {

    public RecommendListController(Prodiver provider) {
        super(provider);
    }

    @Override
    public void loadData(Object... aArray) {
        int sectionId = 0;
        for (Object obj : aArray) {
            sectionId = (int) obj;
        }
        RecommendCategoryNode recommendCategoryNode =
                InfoManager.getInstance().root().getRecommendCategoryNode(sectionId);
        if (recommendCategoryNode == null) {
            super.loadData(sectionId);
        } else {
            baseView.setDate(recommendCategoryNode);
        }

    }
}

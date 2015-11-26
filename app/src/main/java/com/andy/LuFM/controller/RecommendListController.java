package com.andy.LuFM.controller;

import android.view.View;

import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.providers.RecommendListProvider;
import com.andy.LuFM.view.BaseRecommendView;
import com.andy.LuFM.view.SwitchView;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class RecommendListController<Prodiver extends RecommendListProvider> extends BaseListController<Prodiver, BaseRecommendView> {

    public SwitchView mHeadView;

    public RecommendListController(Prodiver provider) {
        super(provider);
    }

    @Override
    protected View createHeadView() {
        mHeadView = new SwitchView(mContext,mRefreshLayout);
        return mHeadView;
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

    @Override
    public void onRefresh() {
        super.onRefresh();
        mProvider.loadData(baseView.getSection());
    }

    @Override
    public void onLoadSuccess(Object object) {
        super.onLoadSuccess(object);
        baseView.setDate(object);
    }
}

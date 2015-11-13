package com.andy.LuFM.view;

import android.content.Context;

import com.andy.LuFM.controller.BaseListController;
import com.andy.LuFM.controller.RecommendListController;
import com.andy.LuFM.providers.BaseProvider;
import com.andy.LuFM.providers.ListDataProvider;
import com.andy.LuFM.providers.RecommendListProvider;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class RecommendView extends BaseViewGroup<RecommendListController, RecommendListProvider> {
    public RecommendView(Context context) {
        super(context);
    }

    @Override
    protected RecommendListController createController() {
        return new RecommendListController(createProvider());
    }

    @Override
    protected RecommendListProvider createProvider() {
        return new RecommendListProvider(mContext);
    }

    @Override
    public void update() {
        controller.loadData();
    }
}

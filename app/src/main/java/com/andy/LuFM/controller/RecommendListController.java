package com.andy.LuFM.controller;

import android.view.View;

import com.andy.LuFM.providers.RecommendListProvider;
import com.andy.LuFM.view.RecommendView;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class RecommendListController<Prodiver extends RecommendListProvider> extends BaseListController<Prodiver, RecommendView> {
    public RecommendListController(Prodiver provider) {
        super(provider);
    }

    @Override
    public void assumeView(View view) {
        super.assumeView(view);
        mListView.setAdapter(mProvider.getAdapter());
    }
}

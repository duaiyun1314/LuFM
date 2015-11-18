package com.andy.LuFM.controller;

import android.util.Log;
import android.view.View;

import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.providers.RecommendListProvider;
import com.andy.LuFM.view.RecommendView;
import com.andy.LuFM.view.SwitchView;

import java.util.List;

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
        SwitchView switchView = new SwitchView(mContext);
        mListView.addHeaderView(switchView);
    }

    @Override
    public void onLoadSuccess(Object object) {
        super.onLoadSuccess(object);
        List<RecommendItemNode> recommendItemNodes = (List<RecommendItemNode>) object;
        View view = mListView.getChildAt(0);
        if (view instanceof SwitchView) {
            ((SwitchView) view).update(recommendItemNodes);
        }

    }
}

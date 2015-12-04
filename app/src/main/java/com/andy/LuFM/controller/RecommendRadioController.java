package com.andy.LuFM.controller;

import android.view.View;

import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.model.RecommendPlayingItemNode;
import com.andy.LuFM.providers.RecommendRadioProvider;
import com.andy.LuFM.view.BaseRecommendView;
import com.andy.LuFM.view.RadioHeadView;
import com.andy.LuFM.view.RecommendRadioView;
import com.andy.LuFM.view.SwitchView;

import java.util.List;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class RecommendRadioController<Prodiver extends RecommendRadioProvider> extends BaseListController<Prodiver, RecommendRadioView> {

    public RadioHeadView mHeadView;

    public RecommendRadioController(Prodiver provider) {
        super(provider);
    }

    @Override
    public void assumeView(View view) {
        super.assumeView(view);
        this.loader.setUserNead(false);
    }

    @Override
    protected View createHeadView() {
        mHeadView = new RadioHeadView(mContext);
        return mHeadView;
    }

    @Override
    public void loadData(Object... aArray) {
        int sectionId = 0;
        for (Object obj : aArray) {
            sectionId = (int) obj;
        }
        List<RecommendPlayingItemNode> lstNodes = InfoManager.getInstance().root().mRecommendPlayingInfo.getCurrPlayingForShow();

        if (lstNodes == null || lstNodes.size() <= 0) {
            super.loadData(sectionId);
        } else {
            baseView.setDate(lstNodes);
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

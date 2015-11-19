package com.andy.LuFM.controller;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.andy.LuFM.Utils.ToolKit;
import com.andy.LuFM.providers.ListDataProvider;
import com.andy.LuFM.view.BaseRecommendView;
import com.andy.LuFM.view.SwitchView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class BaseRecommendListController<Provider extends ListDataProvider, BaseView extends BaseRecommendView> extends BaseControllerImpl<Provider> implements SwipeRefreshLayout.OnRefreshListener {
    public SwipeRefreshLayout mRefreshLayout;
    public BaseView baseView;
    public ListView mListView;
    public SwitchView mHeadView;

    public BaseRecommendListController(Provider provider) {
        super(provider);
    }

    @Override
    public void assumeView(View view) {
        super.assumeView(view);
        baseView = (BaseView) view;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRefreshLayout = new SwipeRefreshLayout(mContext);
        baseView.removeAllViews();
        baseView.addView(mRefreshLayout, layoutParams);
        this.mRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        this.mRefreshLayout.setOnRefreshListener(this);
        this.mRefreshLayout.setColorSchemeColors(colorPrimary, colorPrimaryDark, colorAccent);
        //mRefreshLayout.removeAllViews();
        mListView = new ListView(mContext);
        mHeadView = new SwitchView(mContext);
        mListView.addHeaderView(mHeadView);
        mListView.setDivider(null);
        mListView.setAdapter(mProvider.getAdapter());
        mRefreshLayout.addView(mListView, layoutParams);
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
    }

    @Override
    public void onRefresh() {
        mProvider.loadData();
    }

    @Override
    public void loadData(final Object... aArray) {
        super.loadData(aArray);
        ToolKit.runInUIThread(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                //onRefresh();
                mProvider.loadData(aArray);
            }
        }, 0);
    }

    @Override
    public void onLoadFinish(int size) {
        super.onLoadFinish(size);
        mRefreshLayout.setRefreshing(false);
        mProvider.getAdapter().notifyDataSetChanged();
    }

    public ListAdapter getAdatper() {
        return mProvider.getAdapter();
    }

    @Override
    public void onLoadSuccess(Object object) {
        super.onLoadSuccess(object);
        baseView.setDate(object);
    }
}

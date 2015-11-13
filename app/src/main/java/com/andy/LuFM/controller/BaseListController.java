package com.andy.LuFM.controller;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.andy.LuFM.R;
import com.andy.LuFM.Utils.ToolKit;
import com.andy.LuFM.providers.ListDataProvider;
import com.andy.LuFM.view.BaseViewGroup;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class BaseListController<Provider extends ListDataProvider, BaseView extends BaseViewGroup> extends BaseControllerImpl<Provider> implements SwipeRefreshLayout.OnRefreshListener {
    protected SwipeRefreshLayout mRefreshLayout;
    protected BaseView baseView;
    protected ListView mListView;

    public BaseListController(Provider provider) {
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
        mRefreshLayout.removeAllViews();
        mListView = new ListView(mContext);
        mRefreshLayout.addView(mListView, layoutParams);
    }

    @Override
    public void onRefresh() {
        mProvider.loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        ToolKit.runInUIThread(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        }, 0);
    }

    @Override
    public void onLoadFinish(int size) {
        super.onLoadFinish(size);
        mRefreshLayout.setRefreshing(false);
        mProvider.getAdapter().notifyDataSetChanged();
    }
}

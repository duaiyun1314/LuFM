package com.andy.LuFM.controller;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
public class BaseListController<Provider extends ListDataProvider, BaseView extends FrameLayout> extends BaseControllerImpl<Provider> implements SwipeRefreshLayout.OnRefreshListener {
    public SwipeRefreshLayout mRefreshLayout;
    public BaseView baseView;
    public ListView mListView;
    public PageLoader loader;

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
        mListView = new ListView(mContext);
        mListView.setDivider(null);
        View headerview = createHeadView();
        if (headerview != null) {
            mListView.addHeaderView(headerview);
        }
        mRefreshLayout.addView(mListView, layoutParams);
        mRefreshLayout.setDistanceToTriggerSync(150);
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        PageLoader.OnLoadListener onLoadListener = new PageLoader.OnLoadListener() {
            @Override
            public void onLoading(PageLoader pagedLoader, boolean isAutoLoad) {
                mProvider.loadNextData();
            }
        };
        this.loader = PageLoader.from(mListView).setOnLoadListener(onLoadListener).build();
        this.loader.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        mListView.setAdapter(mProvider.getAdapter());
        this.loader.setAdatper(this.mProvider.getAdapter());
        //this.loader.setEnable(setLoaderEnable());
    }

    @Override
    public void onRefresh() {

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

    public void loadNextData(Object... aArray) {
        mProvider.loadNextData(aArray);
    }

    @Override
    public void onLoadFinish(int size) {
        super.onLoadFinish(size);
        mRefreshLayout.setRefreshing(false);
        //mProvider.getAdapter().notifyDataSetChanged();
        if (mProvider.getAdapter().getCount() < mProvider.getPageSize() || size == 0) {
            loader.setFinally();
        } else {
            loader.setLoading(false);
        }
    }

    @Override
    public void onLoadSuccess(Object object) {
        super.onLoadSuccess(object);
        mRefreshLayout.setRefreshing(false);
    }

    public ListAdapter getAdatper() {
        return mProvider.getAdapter();
    }

    protected View createHeadView() {
        return null;
    }

    protected boolean setLoaderEnable() {
        return false;
    }
}

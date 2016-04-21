package com.andy.LuFM.controller;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * Created by Andy.Wang on 2015/11/26.
 */
public class PageLoader extends DataSetObserver implements AbsListView.OnScrollListener, View.OnClickListener {
    private ListView listView;
    // ListView底部View
    private View moreView;
    private TextView normalTextView;
    private TextView finallyTextView;
    private ProgressWheel progressBar;
    private Mode mode = Mode.CLICK_TO_LOAD;
    private OnLoadListener mOnLoadListener;
    private AbsListView.OnScrollListener mOnScrollListener;
    private boolean enable;
    /**
     * 是否需要上拉加载更多
     */
    private boolean userNead = true;
    private ListAdapter mAdapter;
    /**
     * 正在加载
     */
    private boolean isLoading;

    /**
     * 最后一条可视条目的索引
     */
    private int lastVisibleIndex;

    public static Builder from(ListView listView) {
        return new Builder(listView);
    }

    /**
     * 暴露正在滚动的接口，方便知道滚动状态，控制图片的加载
     *
     * @param mOnScrollListener
     */
    public void setOnScrollListener(AbsListView.OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        setDisplay(enable);
    }

    public boolean isEnable() {
        return enable;
    }

    /**
     * 初始化状态
     *
     * @param adatper
     */
    public void setAdatper(ListAdapter adatper) {
        if (this.mAdapter == null && adatper == null) {
            throw new RuntimeException("adapter must be null");
        }
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this);
        }
        this.mAdapter = adatper;
        //监听adapter data 来控制footview的显隐
        this.mAdapter.registerDataSetObserver(this);
        bindEvent();
    }

    @Override
    public void onChanged() {
        if (mAdapter == null) {
            throw new RuntimeException("adapter must not be null");
        }
        bindEvent();
    }

    private void bindEvent() {
        if (mAdapter.getCount() == 0) {
            setEnable(false);
        } else {
            if (mOnLoadListener == null) {
                setEnable(false);
            } else {
                setEnable(true);
            }
        }
    }

    private void setDisplay(boolean show) {
        if (show && userNead) {
            moreView.setVisibility(View.VISIBLE);
        } else {
            moreView.setVisibility(View.GONE);
        }
    }

    public void setUserNead(boolean isNead) {
        this.userNead = isNead;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setFinally() {
        setFinally(true);
    }

    private void setFinally(boolean isFinall) {
        if (isFinall) {
            setLoading(false, true);
            enable = false;
        } else {
            setLoading(false, false);
            enable = true;
        }
    }

    private void setLoading(boolean isLoading, boolean isFinall) {
        setLoading(isLoading);
        if (isFinall) {
            normalTextView.setVisibility(View.GONE);
            finallyTextView.setVisibility(View.VISIBLE);
        } else {
            normalTextView.setVisibility(View.VISIBLE);
            finallyTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 暴露正在加载的接口，方便让加载器加载下一页数据
     *
     * @param mOnLoadListener
     */
    public void setOnLoadListener(OnLoadListener mOnLoadListener) {
        this.mOnLoadListener = mOnLoadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int i) {
        //到达最后一条 并且是自动加载模式  调用接口方法
        //如果有listview增加了headview 需要调用getAdapter().getCount();用自己的adapter得到的count是不正确的，headview占position 0 。
        if (isEnable()/**footview enable*/ && getMode() == Mode.AUTO_LOAD && !isLoading && lastVisibleIndex == listView.getAdapter().getCount() &&
                i == SCROLL_STATE_IDLE) {
            setLoading(true);
            mOnLoadListener.onLoading(this, true);
        }

        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, i);
        }

    }

    public void setLoading(boolean isloading) {
        if (isEnable()) {
            this.isLoading = isloading;
            if (isloading) {
                progressBar.spin();
                progressBar.setVisibility(View.VISIBLE);
                normalTextView.setVisibility(View.GONE);
            } else {
                progressBar.stopSpinning();
                progressBar.setVisibility(View.GONE);
                normalTextView.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastVisibleIndex = firstVisibleItem + visibleItemCount;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
                    totalItemCount);
        }
    }

    @Override
    public void onClick(View v) {
        if (enable && mode == Mode.CLICK_TO_LOAD) {
            setLoading(true);
            mOnLoadListener.onLoading(this, false);
        }
    }

    public enum Mode {
        CLICK_TO_LOAD, AUTO_LOAD
    }

    public interface OnLoadListener {
        void onLoading(PageLoader pagedLoader, boolean isAutoLoad);
    }


    public static class Builder {
        private PageLoader pageLoader;
        private Context context;

        private Builder(ListView listView) {
            this.context = listView.getContext();
            pageLoader = new PageLoader();
            pageLoader.listView = listView;
            //初始化底部布局
            pageLoader.moreView = LayoutInflater.from(context).inflate(R.layout.paged_foot,
                    pageLoader.listView, false);
            pageLoader.normalTextView = (TextView) pageLoader.moreView.findViewById(R.id.bt_load);
            pageLoader.finallyTextView = (TextView) pageLoader.moreView.findViewById(R.id.bt_finally);
            pageLoader.progressBar = (ProgressWheel) pageLoader.moreView.findViewById(R.id.pg);
        }

        public PageLoader build() {
            if (pageLoader.listView.getAdapter() != null) {
                throw new RuntimeException("must set footview before setAdapter()");
            }
            //设置footview
            pageLoader.listView.addFooterView(pageLoader.moreView, null, false);
            pageLoader.listView.setFooterDividersEnabled(false);
            pageLoader.normalTextView.setOnClickListener(pageLoader);
            //此时处于onCreateView   mode还是初始化值
            if (pageLoader.mode == Mode.AUTO_LOAD) {
                pageLoader.listView.setOnScrollListener(pageLoader);
            }
            //开始默认不显示footview
            pageLoader.setEnable(false);
            return pageLoader;
        }

        public Builder setOnLoadListener(OnLoadListener mOnLoadListener) {
            pageLoader.setOnLoadListener(mOnLoadListener);
            return this;
        }


    }
}

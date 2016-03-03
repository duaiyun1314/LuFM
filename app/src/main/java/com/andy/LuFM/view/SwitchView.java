package com.andy.LuFM.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.andy.LuFM.R;
import com.andy.LuFM.adapter.SwitchAdapter;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.view.autoSrcollViewpager.AutoScrollViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * Created by wanglu on 15/11/18.
 */
public class SwitchView extends LinearLayout {
    private AutoScrollViewPager viewPager;
    private Activity context;
    private SwipeRefreshLayout refreshLayout;
    private SlidingTabLineLayout slidingTabLayout;

    public SwitchView(Context context, SwipeRefreshLayout refreshLayout) {
        this(context, null, 0);
        this.refreshLayout = refreshLayout;
    }

    public SwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = (Activity) context;
        inflate(context, R.layout.layout_switchview, this);
        viewPager = (AutoScrollViewPager) findViewById(R.id.vp);
        slidingTabLayout = (SlidingTabLineLayout) findViewById(R.id.sliding_tabs);
        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        refreshLayout.setEnabled(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        refreshLayout.setEnabled(false);
                        break;
                }
                return false;
            }
        });
    }

    public void update(List<RecommendItemNode> lists) {
        viewPager.setAdapter(new SwitchAdapter(context, lists, viewPager));
        viewPager.setInterval(2000);
        viewPager.startAutoScroll();
        viewPager.setCurrentItem(0);
        slidingTabLayout.setViewPager(viewPager);
    }

}

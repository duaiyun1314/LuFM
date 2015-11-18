package com.andy.LuFM.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
    private Context context;

    public SwitchView(Context context) {
        this(context, null);
    }

    public SwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate(context, R.layout.layout_switchview, this);
        viewPager = (AutoScrollViewPager) findViewById(R.id.vp);
    }

    public void update(List<RecommendItemNode> lists) {
        Log.i("Sync", "switchview 加载adapter；" + lists.size());
        viewPager.setAdapter(new SwitchAdapter(context, lists));
        //viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        viewPager.setInterval(2000);
        viewPager.startAutoScroll();
        viewPager.setCurrentItem(0);
    }

}

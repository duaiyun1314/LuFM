/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andy.LuFM.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.andy.LuFM.adapter.SwitchAdapter;

public class SlidingTabLineLayout extends View {


    private int width;
    private int height = 2;
    private ViewPager mViewPager;
    //每条横线的宽度
    private float distance;
    //绘制每条横线的起始x
    private float startX;
    private Paint paint;
    private int count;

    public SlidingTabLineLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLineLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLineLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
    }


    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    private void populateTabStrip() {
        if (mViewPager == null) return;
        final SwitchAdapter adapter = (SwitchAdapter) mViewPager.getAdapter();
        count = adapter.getItemCount();
        distance = width / count;
        startX = mViewPager.getCurrentItem() % adapter.getItemCount() * distance;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(startX, 0, startX + distance, height, paint);
        //判断第一条线滑出屏幕了 滑出的部分从另一侧滑进
        if ((startX + distance) > width) {
            canvas.drawRect(0, 0, startX + distance - width, height, paint);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            startX = position % count * distance + distance * positionOffset;
            invalidate();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageSelected(int position) {
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        populateTabStrip();
    }
}

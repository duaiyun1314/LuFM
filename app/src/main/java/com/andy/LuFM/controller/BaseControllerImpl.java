package com.andy.LuFM.controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;

import com.andy.LuFM.R;
import com.andy.LuFM.providers.BaseProvider;
import com.andy.LuFM.providers.ProviderCallback;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class BaseControllerImpl<Provider extends BaseProvider> implements BaseController, ProviderCallback {
    protected Provider mProvider;
    protected Context mContext;
    protected int colorPrimary;
    protected int colorPrimaryDark;
    protected int titleColor;
    protected int windowBackground;
    protected int colorAccent;

    @Override
    public void assumeView(View view) {

    }

    public BaseControllerImpl(Provider provider) {
        this.mProvider = provider;
        this.mProvider.setCallback(this);
    }

    @Override
    public void setActivity(Context activity) {
        mContext = activity;
        TypedArray array = mContext.obtainStyledAttributes(new int[]{R.attr.colorPrimary,
                R.attr.colorPrimaryDark, R.attr.titleColor, android.R.attr.windowBackground,
                R.attr.colorAccent
        });
        colorPrimary = array.getColor(0, mContext.getResources().getColor(R.color.toolbarColor));
        colorPrimaryDark = array.getColor(1, mContext.getResources().getColor(R.color.statusColor));
        titleColor = array.getColor(2, mContext.getResources().getColor(R.color.toolbarColor));
        windowBackground = array.getColor(3, Color.WHITE);
        colorAccent = array.getColor(4, mContext.getResources().getColor(R.color.toolbarColor));
        array.recycle();
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onLoadStart() {

    }

    @Override
    public void onLoadSuccess(Object object) {

    }

    @Override
    public void onLoadFinish(int size) {

    }

    @Override
    public void onLoadFailure() {

    }
}

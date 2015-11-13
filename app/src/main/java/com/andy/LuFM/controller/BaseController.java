package com.andy.LuFM.controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.andy.LuFM.providers.BaseProvider;
import com.andy.LuFM.providers.ProviderCallback;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public interface BaseController<Provider extends BaseProvider> {

    void assumeView(View view);

    void setActivity(Context activity);

    abstract void loadData();
}

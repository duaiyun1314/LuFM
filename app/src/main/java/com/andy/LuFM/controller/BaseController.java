package com.andy.LuFM.controller;

import com.andy.LuFM.providers.BaseProvider;
import com.andy.LuFM.providers.ProviderCallback;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public abstract class BaseController<Provider extends BaseProvider> implements ProviderCallback {
    protected Provider provider;

    public BaseController(Provider provider) {
        this.provider = provider;
        this.provider.setCallback(this);
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
    abstract void loadData();
}

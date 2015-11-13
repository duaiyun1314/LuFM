package com.andy.LuFM.providers;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public abstract class BaseProvider<T> {
    protected ProviderCallback callback;

    public void setCallback(ProviderCallback callback) {
        this.callback = callback;
    }

    abstract void loadData();

}

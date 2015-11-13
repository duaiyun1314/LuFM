package com.andy.LuFM.providers;


import android.content.Context;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public abstract class BaseProvider<T> {
    protected Context mContext;

    public BaseProvider(Context context) {
        this.mContext = context;

    }

    protected ProviderCallback callback;

    public void setCallback(ProviderCallback callback) {
        this.callback = callback;
    }

    public abstract void loadData();

}

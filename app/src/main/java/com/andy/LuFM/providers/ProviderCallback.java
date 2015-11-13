package com.andy.LuFM.providers;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public interface ProviderCallback<T> {
    void onLoadStart();

    void onLoadSuccess(T object);

    void onLoadFinish(int size);

    void onLoadFailure();
}

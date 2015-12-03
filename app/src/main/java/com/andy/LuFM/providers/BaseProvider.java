package com.andy.LuFM.providers;


import android.content.Context;

import com.andy.LuFM.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public abstract class BaseProvider<T> {
    protected Context mContext;
    protected DisplayImageOptions options;

    public BaseProvider(Context context) {
        this.mContext = context;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.recommend_defaultbg)
                .showImageOnFail(R.drawable.recommend_defaultbg)
                .build();

    }

    protected ProviderCallback callback;

    public void setCallback(ProviderCallback callback) {
        this.callback = callback;
    }

    public abstract void loadData(Object... aArray);

    public DisplayImageOptions getOptions() {
        return options;
    }
}

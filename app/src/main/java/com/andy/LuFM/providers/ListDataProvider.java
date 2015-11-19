package com.andy.LuFM.providers;


import android.content.Context;
import android.widget.BaseAdapter;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public abstract class ListDataProvider extends BaseProvider {
    protected BaseAdapter adapter;

    public ListDataProvider(Context context) {
        super(context);
    }

    public BaseAdapter getAdapter() {
        if (adapter == null) {
            adapter = newAdapter();
        }
        return adapter;
    }

    @Override
    public void loadData(Object... aArray) {

    }

    public abstract BaseAdapter newAdapter();
}

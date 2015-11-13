package com.andy.LuFM.providers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class RecommendListProvider extends ListDataProvider {
    protected int Count = 0;

    public RecommendListProvider(Context context) {
        super(context);
    }

    @Override
    public BaseAdapter newAdapter() {
        return new TextAdapter();
    }

    @Override
    public void loadData() {
        super.loadData();
        Count = 10;
        callback.onLoadFinish(0);
    }

    public class TextAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(mContext);
            tv.setText("haha:" + position);
            return tv;
        }
    }
}

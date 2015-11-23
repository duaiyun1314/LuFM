package com.andy.LuFM.view.channeldetail;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by wanglu on 15/11/23.
 */
public class ChannelDetailView extends LinearLayout {
    public ChannelDetailView(Context context) {
        super(context);
    }

    public ChannelDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChannelDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void update(String type, Object param) {
        Log.i("Sync", "update");
    }

}

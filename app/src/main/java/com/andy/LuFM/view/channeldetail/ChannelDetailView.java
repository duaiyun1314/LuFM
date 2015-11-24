package com.andy.LuFM.view.channeldetail;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.ChannelNode;


/**
 * Created by wanglu on 15/11/23.
 */
public class ChannelDetailView extends LinearLayout implements ChannelHelper.IDataChangeObserver {
    private ChannelNode channelNode;
    private ChannelDetailCoverView coverView;

    public ChannelDetailView(Context context) {
        this(context, null);
    }

    public ChannelDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChannelDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        coverView = new ChannelDetailCoverView(context);
        addView(coverView, layoutParams);

    }

    public void update(String type, Object param) {
        Log.i("Sync", "update");
        ChannelNode temp = (ChannelNode) param;
        if (this.channelNode != temp) {
            this.channelNode = temp;
            this.coverView.update("setdata", param);
            ChannelHelper.getInstance().addObserver(this.channelNode.channelId, this);

        }
    }

    @Override
    public void onChannelNodeInfoUpdate(ChannelNode channelNode) {
        if (this.channelNode != null && this.channelNode.channelId == channelNode.channelId) {
            this.channelNode.updateAllInfo(channelNode);
            this.coverView.update("setData", this.channelNode);
            //  dispatchActionEvent("resetNavi", null);
        }
    }
}

package com.andy.LuFM.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.andy.LuFM.R;
import com.andy.LuFM.view.channeldetail.ChannelDetailView;
import com.andy.LuFM.view.topicview.SpecialTopicView;

/**
 * Created by Andy.Wang on 2015/12/3.
 */
public class SecondaryViewGroup extends LinearLayout {
    private FrameLayout contentViewContainer;
    private int childCount = 2;
    private View currentView;
    private Animation rightInAnim;
    private Animation rightOutAnim;
    private FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    public SecondaryViewGroup(Context context) {
        this(context, null);
    }

    public SecondaryViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecondaryViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rightInAnim = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right);
        rightOutAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_right);
        inflate(context, R.layout.layout_channel_detail, this);
        this.contentViewContainer = (FrameLayout) findViewById(R.id.view_container);

    }

    public void switchView(String type, Object param) {
        if (type.equalsIgnoreCase("channeldetail")) {
            ChannelDetailView contentView = new ChannelDetailView(getContext());
            contentView.update(type, param);
            currentView = contentView;
            this.contentViewContainer.addView(contentView, layoutParams);
            contentView.startAnimation(rightInAnim);
        } else if (type.equalsIgnoreCase("specialtopic")) {
            SpecialTopicView contentView = new SpecialTopicView(getContext());
            contentView.update(type, param);
            currentView = contentView;
            this.contentViewContainer.addView(contentView, layoutParams);
            contentView.startAnimation(rightInAnim);

        }
    }

    public boolean removeChild() {
        int count = contentViewContainer.getChildCount();
        if (count <= 0) {
            return true;
        } else {
            View childView = contentViewContainer.getChildAt(count - 1);
            //childView.startAnimation(rightOutAnim);
            contentViewContainer.removeView(childView);
            if (contentViewContainer.getChildCount() > 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    public int getContainerCount() {
        if (contentViewContainer != null) {
            return contentViewContainer.getChildCount();
        }
        return 0;
    }
}

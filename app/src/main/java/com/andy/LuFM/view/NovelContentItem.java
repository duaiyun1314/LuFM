package com.andy.LuFM.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.Utils.TimeKit;
import com.andy.LuFM.controller.ControllerManager;
import com.andy.LuFM.model.ActivityNode;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.SpecialTopicNode;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wanglu on 15/11/18.
 */
public class NovelContentItem extends LinearLayout implements View.OnClickListener {
    private RecommendItemNode mInfo;
    private ImageView content_iv;
    private TextView title;
    private TextView subtitle;
    private TextView time;
    private RatingBar ratingBar;
    private Activity context;

    public NovelContentItem(Context context) {
        this(context, null);
    }

    public NovelContentItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NovelContentItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = (Activity) context;
        inflate(context, R.layout.layout_novel_item, this);
        content_iv = (ImageView) findViewById(R.id.content_iv);
        title = (TextView) findViewById(R.id.title);
        subtitle = (TextView) findViewById(R.id.subtitle);
        time = (TextView) findViewById(R.id.time);
        ratingBar = (RatingBar) findViewById(R.id.rating);
        findViewById(R.id.ripple).setOnClickListener(this);
    }

    public void update(RecommendItemNode node, DisplayImageOptions options, LayoutParams layoutParams) {
        mInfo = node;
        title.setText(getTitle());
        subtitle.setText(getSubTitle());
        time.setText(getUpdateTime());
        if (mInfo.mNode instanceof ChannelNode && ((ChannelNode) mInfo.mNode).channelType == 0) {
            ratingBar.setStar(0);
        } else {
            ratingBar.setStar(mInfo.ratingStar / 10f * 5);
        }
        ImageLoader.getInstance().displayImage(node.getMediumThumb(), content_iv, options);
    }


    private String getTitle() {
        if (this.mInfo.mNode == null) {
            return null;
        }
        if (this.mInfo.mNode.nodeName.equalsIgnoreCase("program")) {
            String parentName = ((ProgramNode) this.mInfo.mNode).getChannelName();
            if (parentName == null) {
                return this.mInfo.belongName;
            }
            return parentName;
        } else if (this.mInfo.mNode.nodeName.equalsIgnoreCase("channel")) {
            return this.mInfo.name;
        } else {
            if (this.mInfo.mNode.nodeName.equalsIgnoreCase("specialtopic")) {
                return ((SpecialTopicNode) this.mInfo.mNode).title;
            }
            if (this.mInfo.mNode.nodeName.equalsIgnoreCase("activity")) {
                return ((ActivityNode) this.mInfo.mNode).name;
            }
            return this.mInfo.name;
        }
    }

    private String getSubTitle() {
        if (this.mInfo.mNode == null) {
            return null;
        }
        return this.mInfo.name;
    }

    private String getUpdateTime() {
        return standardizeTime(this.mInfo.getUpdateTime());
    }

    private String standardizeTime(long updateTime) {
        return TimeKit.getReadableTime(updateTime);
    }

    @Override
    public void onClick(View v) {
        ControllerManager.getInstance(context).openControllerByRecommendNode(mInfo);

    }
}

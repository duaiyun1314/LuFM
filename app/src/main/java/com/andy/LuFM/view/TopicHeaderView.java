package com.andy.LuFM.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.SpecialTopicNode;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wanglu on 15/11/18.
 */
public class TopicHeaderView extends LinearLayout {
    private ImageView switch_img;
    private TextView switch_name;

    public TopicHeaderView(Context context) {
        this(context, null);
    }

    public TopicHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopicHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_switch_item, this);
        setClickable(false);
        switch_img = (ImageView) findViewById(R.id.switch_img);
        switch_name = (TextView) findViewById(R.id.switch_name);
    }

    public void update(SpecialTopicNode node, DisplayImageOptions options) {
        switch_name.setText(node.title);
        ImageLoader.getInstance().displayImage(node.thumb, switch_img, options);
    }

}

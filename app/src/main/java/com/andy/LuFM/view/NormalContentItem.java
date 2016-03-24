package com.andy.LuFM.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.controller.ControllerManager;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.RecommendItemNode;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wanglu on 15/11/18.
 */
public class NormalContentItem extends LinearLayout implements View.OnClickListener {
    private ImageView content_iv;
    private TextView content_name;
    private Activity mContext;
    private Node node;

    public NormalContentItem(Context context) {
        this(context, null);
    }

    public NormalContentItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NormalContentItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = (Activity) context;
        inflate(context, R.layout.layout_content_item, this);
        content_iv = (ImageView) findViewById(R.id.content_iv);
        content_name = (TextView) findViewById(R.id.content_name);
        findViewById(R.id.ripple).setOnClickListener(this);
    }

    public void update(RecommendItemNode node, DisplayImageOptions options, LayoutParams layoutParams) {
        changeLayout(layoutParams);
        this.node = node;
        content_name.setText(node.name);
        ImageLoader.getInstance().displayImage(node.getMediumThumb(), content_iv, options);
    }

    private void changeLayout(LayoutParams layoutParams) {
        content_iv.getLayoutParams().height = layoutParams.width;
    }

    @Override
    public void onClick(View v) {
        ControllerManager.getInstance(mContext).openControllerByRecommendNode(this.node);

    }

}

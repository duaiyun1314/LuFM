package com.andy.LuFM.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.model.RecommendItemNode;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wanglu on 15/11/18.
 */
public class SwitchItemView extends LinearLayout {
    private ImageView switch_img;
    private TextView switch_name;

    public SwitchItemView(Context context) {
        this(context, null);
    }

    public SwitchItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_switch_item, this);
        switch_img = (ImageView) findViewById(R.id.switch_img);
        switch_name = (TextView) findViewById(R.id.switch_name);
    }

    public void update(RecommendItemNode node, DisplayImageOptions options) {
        switch_name.setText(node.name);
        ImageLoader.getInstance().displayImage(node.getSmallThumb(), switch_img, options);
    }

}

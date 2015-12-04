package com.andy.LuFM.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.model.CategoryNode;

/**
 * Created by Andy.Wang on 2015/12/4.
 */
public class RadioHeaderItemView extends LinearLayout {
    public TextView name;
    public ImageView iv_arrow;
    public Animation rotatoAnim;

    public RadioHeaderItemView(Context context) {
        this(context, null);
    }

    public RadioHeaderItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioHeaderItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_tag_button, this);
        name = (TextView) findViewById(R.id.tag_button_name);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
        rotatoAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_radio);
    }

    public void update(CategoryNode categoryNode, boolean isDrag) {
        String nameStr = categoryNode.getName();
        if (nameStr != null) {
            name.setText(categoryNode.getName());
            iv_arrow.setVisibility(View.GONE);
            name.setVisibility(View.VISIBLE);
        } else {
            iv_arrow.setVisibility(View.VISIBLE);
            name.setVisibility(View.GONE);
            if (isDrag) {
                iv_arrow.setBackgroundResource(R.drawable.ic_arrow_radiocollapse_up);
            } else {
                iv_arrow.setBackgroundResource(R.drawable.ic_arrow_radiocollapse_down);
            }
        }
    }
}

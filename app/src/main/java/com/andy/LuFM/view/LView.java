package com.andy.LuFM.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.andy.LuFM.model.SectionItem;

/**
 * Created by wanglu on 15/11/17.
 */
public abstract class LView extends LinearLayout {


    public LView(Context context) {
        super(context);
    }

    public LView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void update(SectionItem item, Object object);
}

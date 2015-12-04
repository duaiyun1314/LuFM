package com.andy.LuFM.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.andy.LuFM.R;
import com.andy.LuFM.model.CategoryNode;
import com.andy.LuFM.model.LiveNode;

import java.util.List;

/**
 * Created by Andy.Wang on 2015/12/4.
 */
public class RadioHeadView extends LinearLayout {
    public List<CategoryNode> regions;
    public List<CategoryNode> contents;
    RadioHeaderContainer regionContainer;
    RadioHeaderContainer contentContainer;

    public RadioHeadView(Context context) {
        this(context, null);
    }

    public RadioHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_radio_header, this);
        regionContainer = (RadioHeaderContainer) findViewById(R.id.radio_region);
        contentContainer = (RadioHeaderContainer) findViewById(R.id.radio_content);
    }

    public void update(LiveNode liveNode) {
        regions = liveNode.getRegionCategory();
        contents = liveNode.getContentCategory();
        regionContainer.update(true, regions);
        contentContainer.update(false, contents);
    }
}

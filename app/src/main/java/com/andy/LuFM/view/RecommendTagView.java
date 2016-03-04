package com.andy.LuFM.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.controller.ControllerManager;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.SectionItem;

/**
 * 类别tag视图
 */
public class RecommendTagView extends LView implements View.OnClickListener {
    private TextView mTagName;
    private LinearLayout mMore;
    private RecommendItemNode node;
    private boolean isMoreClickable = true;


    public RecommendTagView(Context context, boolean isMoreClickable) {
        this(context, null);
        if (isMoreClickable) {
            mMore.setOnClickListener(this);
        } else {
            mMore.setVisibility(View.GONE);
        }

    }

    public RecommendTagView(Context context) {
        this(context, null);
    }

    public RecommendTagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecommendTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_recommend_tag, this);
        mTagName = (TextView) findViewById(R.id.tag_name);
        mMore = (LinearLayout) findViewById(R.id.more_container);

    }

    public void update(SectionItem item, Object object) {
        if (item != null) {
            node = (RecommendItemNode) item.data;
            mTagName.setText(node.belongName);
        }
    }

    @Override
    public void onClick(View v) {
        ControllerManager.getInstance(getContext()).openCatogoryView(this.node);
    }
}

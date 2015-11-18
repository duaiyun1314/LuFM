package com.andy.LuFM.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.SectionItem;

/**
 * Created by wanglu on 15/11/17.
 */
public class RecommendTagView extends LView {
    private TextView mTagName;

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

    }

    public void update(SectionItem item, Object object) {
        if (item != null) {
            RecommendItemNode node = (RecommendItemNode) item.data;
            mTagName.setText(node.belongName);
        }
    }
}

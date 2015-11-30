package com.andy.LuFM.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.SectionItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * Created by wanglu on 15/11/17.
 */
public class RecommendItemView extends LView {
    private Activity mContext;
    private int contentPadding;
    private int width;

    public RecommendItemView(Context context) {
        this(context, null);
    }

    public RecommendItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecommendItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // inflate(context, R.layout.layout_recommend_item, this);
        this.mContext = (Activity) context;
        contentPadding = (int) getResources().getDimension(R.dimen.content_padding);
        setOrientation(HORIZONTAL);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;

    }

    public void update(SectionItem item, Object object) {
        DisplayImageOptions options = null;
        if (object != null) {
            options = (DisplayImageOptions) object;
        }
        if (item != null) {
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.width = (width - 4 * contentPadding) / 3;
            lp.leftMargin = contentPadding;
            List<RecommendItemNode> nodes = (List<RecommendItemNode>) item.data;
            for (int i = 0; i < nodes.size(); i++) {
                if (i > 2) return;
                NormalContentItem itemView = new NormalContentItem(mContext);
                itemView.update(nodes.get(i), options, lp);
                addView(itemView, lp);
            }
        }
    }

}

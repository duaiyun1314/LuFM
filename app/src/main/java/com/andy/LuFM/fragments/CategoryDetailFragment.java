package com.andy.LuFM.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.LuFM.AllInOneActivity;
import com.andy.LuFM.R;
import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.view.BaseRecommendView;
import com.andy.LuFM.view.ReCommendColumnView;
import com.andy.LuFM.view.RecommendNovelView;
import com.andy.LuFM.view.RecommendRadioView;
import com.andy.LuFM.view.RecommendView;
import com.andy.LuFM.view.topicview.SpecialTopicView;

/**
 * Created by Andy.Wang on 2016/3/2.
 */
public class CategoryDetailFragment extends Fragment implements View.OnClickListener {
    private String type;
    private Object param;
    private LinearLayout rootView;
    private ImageButton iv_back;
    private TextView title_label;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = new LinearLayout(getActivity());
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setBackgroundColor(getResources().getColor(R.color.list_item_color));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView(type, param);
    }

    public void update(String type, Object param) {
        this.type = type;
        this.param = param;
        if (rootView == null) return;
        updateView(type, param);
    }

    private void updateView(String type, Object param) {
        rootView.removeAllViews();
        RecommendItemNode node = (RecommendItemNode) param;
        //toolBarView
        View toolBarView = View.inflate(getActivity(), R.layout.layout_detail_toolbar, null);
        iv_back = (ImageButton) toolBarView.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        title_label = (TextView) toolBarView.findViewById(R.id.title_label);
        title_label.setText(node.belongName);
        //content view
        BaseRecommendView viewGroup = null;
        switch (node.sectionId) {
            case Constants.HOMEPAGE_SECTION:
                viewGroup = new RecommendView(getActivity());
                break;
            case Constants.NOVEL_SECTION:
                viewGroup = new RecommendNovelView(getActivity());
                break;
            case Constants.CAMPUS_SECTION:
                break;
            case Constants.RADIO_SECTION:
                viewGroup = new RecommendRadioView(getActivity());
                break;
            default:
                viewGroup = new ReCommendColumnView(getActivity());
                break;
        }
        if (viewGroup != null) {
            viewGroup.setMoreClickable(false);
            viewGroup.update(node.sectionId);

        }

        rootView.addView(toolBarView);
        rootView.addView(viewGroup);


    }

    @Override
    public void onClick(View v) {
        AllInOneActivity allInOneActivity = (AllInOneActivity) getContext();
        allInOneActivity.onBackPressed();
    }
}

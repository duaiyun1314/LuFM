package com.andy.LuFM.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.andy.LuFM.R;
import com.andy.LuFM.controller.ControllerManager;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.view.SwitchItemView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * Created by wanglu on 15/11/18.
 */
public class SwitchAdapter extends RecyclingPageAdapter {
    private List<RecommendItemNode> recommendItemNodes;
    private DisplayImageOptions options;
    private boolean isInfiniteLoop;
    private Activity context;
    private ViewPager viewPager;

    public SwitchAdapter(Activity context, List<RecommendItemNode> lists, ViewPager viewPager) {
        this.viewPager = viewPager;
        this.recommendItemNodes = lists;
        isInfiniteLoop = true;
        this.context = context;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.recommend_defaultbg)
                .showImageOnFail(R.drawable.recommend_defaultbg)
                .build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            holder.switchItemView = new SwitchItemView(context);
            convertView = holder.switchItemView;
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPoint = getPosition(viewPager.getCurrentItem());
                Log.i("Sync", "click:" + currentPoint);

                ControllerManager.getInstance(context).openControllerByRecommendNode(recommendItemNodes.get(currentPoint));
            }
        });
        holder.switchItemView.update(recommendItemNodes.get(getPosition(position)), options);
        return convertView;
    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : recommendItemNodes.size();
    }

    private int getPosition(int position) {
        return isInfiniteLoop ? position % recommendItemNodes.size() : position;
    }

    private static class ViewHolder {
        SwitchItemView switchItemView;
    }

}

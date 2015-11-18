package com.andy.LuFM.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.andy.LuFM.R;
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
    private Context context;

    public SwitchAdapter(Context context, List<RecommendItemNode> lists) {
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
    public View getView(int position, View convertView, ViewGroup container) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            holder.switchItemView = new SwitchItemView(context);
            convertView = holder.switchItemView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
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

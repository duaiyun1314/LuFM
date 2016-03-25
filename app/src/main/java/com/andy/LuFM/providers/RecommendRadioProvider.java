package com.andy.LuFM.providers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.Utils.TimeKit;
import com.andy.LuFM.Utils.ViewFactory;
import com.andy.LuFM.adapter.SectionAdapter;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.IResultRecvHandler;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.RecommendPlayingItemNode;
import com.andy.LuFM.model.SectionItem;
import com.andy.LuFM.view.RatingBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class RecommendRadioProvider extends ListDataProvider implements InfoManager.ISubscribeEventListener {


    public RecommendRadioProvider(Context context) {
        super(context);
    }

    @Override
    public BaseAdapter newAdapter() {
        return new MYAdapter(mContext);
    }

    public class MYAdapter extends BaseAdapter {
        private List<RecommendPlayingItemNode> list;
        private Context context;

        public MYAdapter(Context context) {
            this.context = context;

        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.layout_radiorecommend_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.updatetime = (TextView) convertView.findViewById(R.id.time);
                holder.thumb_iv = (ImageView) convertView.findViewById(R.id.content_iv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            RecommendPlayingItemNode recommendPlayingItemNode = list.get(position);
            holder.title.setText(recommendPlayingItemNode.channelName + " " + recommendPlayingItemNode.programName);
            holder.updatetime.setText(recommendPlayingItemNode.getUpdateTime());
            ImageLoader.getInstance().displayImage(recommendPlayingItemNode.thumb, holder.thumb_iv, options);
            return convertView;
        }

        public void setData(List<RecommendPlayingItemNode> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public List<RecommendPlayingItemNode> getData() {
            return list;
        }

        private class ViewHolder {
            TextView title;
            TextView updatetime;
            ImageView thumb_iv;
        }
    }

    @Override
    public void loadData(Object... aArray) {
        super.loadData(aArray);
        List<RecommendPlayingItemNode> lstNodes = InfoManager.getInstance().root().mRecommendPlayingInfo.getCurrPlayingForShow();
        if (lstNodes == null || lstNodes.size() <= 0) {
            InfoManager.getInstance().loadRecommendPlayingProgramsInfo(this);
        } else {
            callback.onLoadSuccess(lstNodes);
        }

    }

    @Override
    public void onNotification(String type) {
        if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_RECOMMEND_PLAYING_PROGRAMS_INFO)) {
            List<RecommendPlayingItemNode> lstNodes = InfoManager.getInstance().root().mRecommendPlayingInfo.getCurrPlayingForShow();
            callback.onLoadSuccess(lstNodes);
        }
    }
}

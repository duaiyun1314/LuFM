package com.andy.LuFM.providers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.Utils.TimeKit;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.model.SpecialTopicNode;
import com.andy.LuFM.view.RatingBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Andy.Wang on 2015/11/25.
 */
public class TopicChannelsProvider extends ListDataProvider implements InfoManager.ISubscribeEventListener {
    private SpecialTopicNode topicNode;

    public TopicChannelsProvider(Context context) {
        super(context);
    }

    @Override
    public BaseAdapter newAdapter() {
        return new MYAdapter(mContext);
    }

    @Override
    public void loadData(Object... aArray) {
        super.loadData(aArray);
        for (Object obj : aArray) {
            topicNode = (SpecialTopicNode) obj;
        }
        InfoManager.getInstance().loadSpecialTopicNode(topicNode, this);

    }

    @Override
    public void onNotification(String type) {
        if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_SPECIAL_TOPIC_CHANNELS)) {
            callback.onLoadSuccess(type);
        }

    }

    public class MYAdapter extends BaseAdapter {
        private List<ChannelNode> list;
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
                convertView = View.inflate(context, R.layout.layout_novel_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.updatetime = (TextView) convertView.findViewById(R.id.time);
                holder.subtitle = (TextView) convertView.findViewById(R.id.subtitle);
                holder.thumb_iv = (ImageView) convertView.findViewById(R.id.content_iv);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ChannelNode channelNode = list.get(position);
            holder.title.setText(channelNode.title);
            holder.updatetime.setText(TimeKit.getReadableTime(channelNode.getUpdateTime()));
            holder.subtitle.setText(channelNode.desc);
            ImageLoader.getInstance().displayImage(channelNode.getApproximativeThumb(65, 65, true), holder.thumb_iv, options);
            holder.ratingBar.setStar(channelNode.ratingStar / 10f * 5);
            return convertView;
        }

        public void setData(List<ChannelNode> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        private class ViewHolder {
            TextView title;
            TextView updatetime;
            TextView subtitle;
            ImageView thumb_iv;
            RatingBar ratingBar;
        }
    }


    @Override
    public int getPageSize() {
        return 300;
    }

    /**
     * provider 有时会需要view的一些参数
     *
     * @return
     */
    public Object getParam() {
        return null;
    }

}

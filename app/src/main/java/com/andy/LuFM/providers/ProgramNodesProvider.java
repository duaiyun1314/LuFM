package com.andy.LuFM.providers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.ProgramNode;

import java.util.List;

/**
 * Created by Andy.Wang on 2015/11/25.
 */
public class ProgramNodesProvider extends ListDataProvider implements InfoManager.ISubscribeEventListener {
    private ChannelNode channelNode;

    public ProgramNodesProvider(Context context) {
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
            channelNode = (ChannelNode) obj;
        }
        InfoManager.getInstance().reloadVirtualProgramsSchedule(channelNode, this);

    }

    @Override
    public void onNotification(String type) {
        if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_RELOAD_PROGRAMS_SCHEDULE)) {
            callback.onLoadSuccess(type);
        }

    }

    public class MYAdapter extends BaseAdapter {
        private List<ProgramNode> list;
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
                convertView = View.inflate(context, R.layout.channle_detail_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.title_label);
                holder.updatetime = (TextView) convertView.findViewById(R.id.updatetime_label);
                holder.duration = (TextView) convertView.findViewById(R.id.duration_label);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ProgramNode programNode = list.get(position);
            Log.i("Sync", "position:" + position + "   " + programNode.title + "   " + programNode.updateTime + "    " + programNode.duration);
            holder.title.setText(programNode.title);
            holder.updatetime.setText(programNode.updateTime);
            holder.duration.setText(getDurationTime((int) programNode.duration) + "");
            return convertView;
        }

        public void setData(List<ProgramNode> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        private String getDurationTime(int duration) {
            String result = "" + (duration / 60) + ":";
            int second = duration % 60;
            if (second < 10) {
                return new StringBuilder(String.valueOf(result)).append(0).append(second).toString();
            }
            return new StringBuilder(String.valueOf(result)).append(second).toString();
        }


        private class ViewHolder {
            TextView title;
            TextView updatetime;
            TextView duration;
        }
    }
}

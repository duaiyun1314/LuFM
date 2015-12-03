package com.andy.LuFM.data.ds;

import android.database.Cursor;

import com.andy.LuFM.LuFmApplication;
import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.IDataOperation;
import com.andy.LuFM.data.IResultRecvHandler;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.dbutil.DaoMaster;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.Node;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wanglu on 15/11/20.
 */
public class ChannelNodeDS implements IDataOperation {
    private static ChannelNodeDS instance;
    private static DaoMaster.DevOpenHelper mHelper;

    private ChannelNodeDS() {
        mHelper = LuFmApplication.getInstance().getHelper();
    }

    public static synchronized ChannelNodeDS getInstance() {
        if (instance == null) {
            instance = new ChannelNodeDS();
        }
        return instance;
    }

    @Override
    public String dataRequestName() {
        return RequestType.CHANNEL_INFO;
    }

    @Override
    public Result doCommand(DataCommand dataCommand, IResultRecvHandler iDataRecvHandler) {
        String type = dataCommand.getType();
        Result result = new Result();
        Map<String, Object> param = dataCommand.getParam();
        if (type.equalsIgnoreCase(RequestType.GETDB_CHANNEL_INFO)) {
            ChannelNode channelNode = getChannelInfo(param);
            if (channelNode != null) {
                result.setSuccess(true);
                result.setData(channelNode);
            } else {
                result.setSuccess(false);
            }
            return result;
        } else if (type.equalsIgnoreCase(RequestType.GETDB_CHANNEL_NODE)) {
            List<ChannelNode> channelNodes = acquireChannelNodes(param);
            if (channelNodes != null && channelNodes.size() > 0) {
                result.setSuccess(true);
                result.setData(channelNodes);
            } else {
                result.setSuccess(false);
            }
            return result;
        }
        return null;
    }

    private ChannelNode getChannelInfo(Map<String, Object> param) {
        ChannelNode node = null;
        try {
            int channelid = ((Integer) param.get(Constants.CHANNEL_ID)).intValue();
            Cursor cursor = mHelper.getReadableDatabase().rawQuery("select channelNode from channelNodesv6 where channelid = '" + channelid + "' and type = '" + ((Integer) param.get(Constants.MESSAGE_TYPE)).intValue() + "'", null);
            Gson gson = new Gson();
            while (cursor.moveToNext()) {
                try {
                    node = (ChannelNode) gson.fromJson(cursor.getString(cursor.getColumnIndex("channelNode")), ChannelNode.class);
                    if (!(node.title == null || node.title.equalsIgnoreCase("\u7535\u53f0\u6545\u969c"))) {
                        break;
                    }
                } catch (Exception e) {
                }
            }
            cursor.close();
            return node;
        } catch (Exception e2) {
            return null;
        }
    }

    private List<ChannelNode> acquireChannelNodes(Map<String, Object> param) {
        List<ChannelNode> lstChannels = null;
        try {
            String key = (String) param.get("key");
            if (key == null || key.equalsIgnoreCase("")) {
                return null;
            }
            String sql = "select channelNode from channelNodes where key = '" + key + "'";
            List<ChannelNode> lstChannels2 = new ArrayList();
            try {
                Cursor cursor = mHelper.getReadableDatabase().rawQuery("select channelNode from channelNodesv6 where key = '" + key + "'", null);
                ChannelNode channel = null;
                ChannelNode prevChannelNode = null;
                Gson gson = new Gson();
                while (cursor.moveToNext()) {
                    try {
                        channel = (ChannelNode) gson.fromJson(cursor.getString(cursor.getColumnIndex("channelNode")), ChannelNode.class);
                    } catch (Exception e) {
                    }
                    if (!(prevChannelNode == null || channel == null)) {
                        channel.prevSibling = prevChannelNode;
                        prevChannelNode.nextSibling = channel;
                    }
                    prevChannelNode = channel;
                    lstChannels2.add(channel);
                }
                cursor.close();
                lstChannels = lstChannels2;
                return lstChannels2;
            } catch (Exception e2) {
                lstChannels = lstChannels2;
                return lstChannels;
            }
        } catch (Exception e3) {
            return lstChannels;
        }
    }

}

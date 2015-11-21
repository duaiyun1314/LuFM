package com.andy.LuFM.helper;

import android.util.Log;
import android.util.SparseArray;

import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanglu on 15/11/20.
 */
public class ChannelHelper extends Node {
    private static ChannelHelper _instance = null;
    private final int ERROR_CHANNELS;
    private final int LIVE_CHANNELS_PAGE_SIZE;
    private final int VIRTUAL_CHANNELS_PAGE_SIZE;
    //private SparseArray<IDataChangeObserver> mObservers;
    public Map<String, List<ChannelNode>> mapChannelNodes;
    public Map<String, Integer> mapChannelPages;
    private SparseArray<ChannelNode> mapLiveChannels;
    private Map<String, Integer> mapTotalChannelNodes;
    public Map<String, Boolean> mapUpdateChannels;
    private SparseArray<ChannelNode> mapVirtualChannels;

    private ChannelHelper() {
        this.mapLiveChannels = new SparseArray();
        this.mapVirtualChannels = new SparseArray();
        this.mapChannelNodes = new HashMap();
        this.mapUpdateChannels = new HashMap();
        this.mapTotalChannelNodes = new HashMap();
        this.mapChannelPages = new HashMap();
        this.VIRTUAL_CHANNELS_PAGE_SIZE = 30;
        this.LIVE_CHANNELS_PAGE_SIZE = 1000;
        this.ERROR_CHANNELS = 1;
        this.nodeName = "channelhelper";
    }

    public static ChannelHelper getInstance() {
        if (_instance == null) {
            _instance = new ChannelHelper();
        }
        return _instance;
    }

    public ChannelNode getFakeChannel(int channelId, int catid, String name, int type) {
        if (type == 0) {
            return getFakeLiveChannel(channelId, catid, name);
        }
        return getFakeVirtualChannel(channelId, catid, name);
    }

    public ChannelNode getFakeLiveChannel(int channelId, int catid, String name) {
        if (this.mapLiveChannels.get(channelId) != null) {
            return (ChannelNode) this.mapLiveChannels.get(channelId);
        }
        ChannelNode node = new ChannelNode();
        node.channelId = channelId;
        node.title = name;
        node.channelType = 0;
        if (node.title == null) {
            node.title = "\\u7535\\u53f0";
        }
        node.categoryId = catid;
        this.mapLiveChannels.put(channelId, node);
        InfoManager.getInstance()._loadLiveChannelNode(channelId, this);
        return node;
    }

    public ChannelNode getFakeVirtualChannel(int channelId, int catid, String name) {
        if (this.mapVirtualChannels.get(channelId) != null) {
            return (ChannelNode) this.mapVirtualChannels.get(channelId);
        }
      /*  if (catid == DownLoadInfoNode.mDownloadId) {
            ChannelNode temp = InfoManager.getInstance().root().mDownLoadInfoNode.getChannelNode(channelId);
            if (temp != null) {
                this.mapVirtualChannels.put(channelId, temp);
                return temp;
            }
        }*/
        ChannelNode node = new ChannelNode();
        node.channelId = channelId;
        node.title = name;
        node.channelType = 1;
        if (node.title == null) {
            node.title = "\u873b\u8713fm";
        }
        node.categoryId = catid;
        this.mapVirtualChannels.put(channelId, node);
        //   InfoManager.getInstance().loadVirtualChannelNode(channelId, this);
        return node;
    }

    public ChannelNode getChannel(ProgramNode node) {
        if (node == null) {
            return null;
        }
       /* if (node.isDownloadProgram()) {
            return InfoManager.getInstance().root().mDownLoadInfoNode.getChannelNode(node.channelId);
        }*/
        ChannelNode cNode;
        if (node.channelType == 1 || node.mLiveInVirtual) {
            cNode = (ChannelNode) this.mapVirtualChannels.get(node.channelId);
        } else {
            cNode = (ChannelNode) this.mapLiveChannels.get(node.channelId);
        }
        if (cNode != null) {
            return cNode;
        }
        if (node.mLiveInVirtual) {
            return getChannelFromDB(node.channelId, 1);
        }
        return getChannelFromDB(node.channelId, node.channelType);
    }

    private ChannelNode getChannelFromDB(int channelId, int type) {
        Map<String, Object> param = new HashMap();
        param.put("channelid", Integer.valueOf(channelId));
        param.put("type", Integer.valueOf(type));
        Result result = DataManager.getInstance().getData(RequestType.CHANNEL_INFO, null, new DataCommand(RequestType.GETDB_CHANNEL_INFO, param));
        if (result.isSuccess()) {
            return (ChannelNode) result.getData();
        }
        return null;
    }

    public ChannelNode getChannel(int channelId, int type) {
        ChannelNode node;
        if (type == 0) {
            node = (ChannelNode) this.mapLiveChannels.get(channelId);
            if (node != null) {
                return node;
            }
        } else if (type == 1) {
            node = (ChannelNode) this.mapVirtualChannels.get(channelId);
            if (node != null) {
                return node;
            }
        }
        node = getChannelFromDB(channelId, type);
        if (node != null) {
            if (type == 1) {
                this.mapVirtualChannels.put(channelId, node);
            } else if (type == 0) {
                this.mapLiveChannels.put(channelId, node);
            }
        }
        Log.i("Sync:", "type：" + type);
        if (type == 0) {
            InfoManager.getInstance()._loadLiveChannelNode(channelId, this);
        }
        if (type == 1) {
            //  InfoManager.getInstance().loadVirtualChannelNode(channelId, this);
        }
        return node;
    }


}

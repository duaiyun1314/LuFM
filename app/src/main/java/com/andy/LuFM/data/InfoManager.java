package com.andy.LuFM.data;

import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.RootNode;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储必要信息
 */
public class InfoManager implements IResultRecvHandler {
    private static InfoManager instance;
    private RootNode mRootNode = new RootNode();

    private InfoManager() {

    }

    public static synchronized InfoManager getInstance() {
        if (instance == null) {
            instance = new InfoManager();
        }
        return instance;
    }

    public RootNode root() {
        return this.mRootNode;
    }

    public void _loadLiveChannelNode(int channelId, Node node) {
        if (node != null) {
            Map<String, Object> param = new HashMap();
            param.put("id", String.valueOf(channelId));
            DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(RequestType.GET_LIVE_CHANNEL_INFO, param));
            //registerNodeEventListener(node, INodeEventListener.ADD_LIVE_CHANNEL_INFO);
        }
    }

    @Override
    public void onRecvResult(Result result, String type) {

    }
}

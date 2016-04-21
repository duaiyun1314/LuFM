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

import roboguice.util.Ln;

/**
 * Created by wanglu on 15/11/20.
 */
public class ChannelHelper extends Node {
    private static ChannelHelper _instance = null;
    private final int ERROR_CHANNELS;
    private final int LIVE_CHANNELS_PAGE_SIZE;
    private final int VIRTUAL_CHANNELS_PAGE_SIZE;
    private SparseArray<IDataChangeObserver> mObservers;
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
        init();
    }

    public static ChannelHelper getInstance() {
        if (_instance == null) {
            _instance = new ChannelHelper();
        }
        return _instance;
    }

    public void init() {
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_CATEGORY_ALL_CHANNELS);
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_VIRTUAL_CHANNELS_BYATTR);
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_LIVE_CHANNELS_BYATTR);
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_SPECIAL_TOPIC_CHANNELS);
    }

    public List<ChannelNode> getLstChannelsByKey(String key) {
        if (key == null) {
            return null;
        }
        if (this.mapChannelNodes.get(key) != null) {
            return (List) this.mapChannelNodes.get(key);
        }
        /*if (!allowReadCache(key)) {
            return null;
        }*/
        List<ChannelNode> lstNodes = getLstChannelsFromDB(key);
        if (lstNodes == null || lstNodes.size() == 0) {
            return null;
        }
        this.mapChannelNodes.put(key, lstNodes);
        return lstNodes;
    }

    private List<ChannelNode> getLstChannelsFromDB(String key) {
        if (key == null || key.equalsIgnoreCase("")) {
            return null;
        }
        Map<String, Object> param = new HashMap();
        param.put("key", key);
        Result result = DataManager.getInstance().getData(RequestType.CHANNEL_INFO, null, new DataCommand(RequestType.GETDB_CHANNEL_NODE, param));
        if (result.isSuccess()) {
            return (List) result.getData();
        }
        return null;
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
        //   Log.i("Sync", "getChannel");
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
        //  Log.i("Sync:", "type：" + type);
        if (type == 0) {
            InfoManager.getInstance().loadLiveChannelNode(channelId, this);
        }
        if (type == 1) {
            //会调到onNodeUpdated（）
            InfoManager.getInstance().loadVirtualChannelNode(channelId, this);
        }
        return node;
    }

    @Override
    public void onNodeUpdated(Object obj, String type) {
        //super.onNodeUpdated(obj, str);
        if (obj != null) {
            Node node;
            ChannelNode temp;
            if (type.equalsIgnoreCase(InfoManager.INodeEventListener.ADD_VIRTUAL_CHANNEL_INFO)) {
                node = (Node) obj;
                if (node != null && node.nodeName.equalsIgnoreCase("channel")) {
                    temp = (ChannelNode) this.mapVirtualChannels.get(((ChannelNode) node).channelId);
                    if (temp != null) {
                        temp.updatePartialInfo((ChannelNode) node);
                    } else {
                        this.mapVirtualChannels.put(((ChannelNode) node).channelId, (ChannelNode) node);
                    }
                    dispatch2Observer((ChannelNode) node);
                    // updateChannel((ChannelNode) node);
                    /*ChannelNode currentNode = InfoManager.getInstance().root().getCurrentPlayingChannelNode();
                    if (currentNode != null && currentNode.channelId == ((ChannelNode) node).channelId) {
                        // PlayerAgent.getInstance().setPlayingChannelThumb(((ChannelNode) node).getApproximativeThumb());
                        currentNode.updateAllInfo((ChannelNode) node);
                    }*/
                }
            } else if (type.equalsIgnoreCase(InfoManager.INodeEventListener.ADD_LIVE_CHANNEL_INFO)) {
                node = (Node) obj;
                if (node != null && node.nodeName.equalsIgnoreCase("channel")) {
                    temp = (ChannelNode) this.mapLiveChannels.get(((ChannelNode) node).channelId);
                    if (temp != null) {
                        temp.updatePartialInfo((ChannelNode) node);
                    } else {
                        this.mapLiveChannels.put(((ChannelNode) node).channelId, (ChannelNode) node);
                    }
                    dispatch2Observer((ChannelNode) node);
                }
            }
        }
    }

    @Override
    public void onNodeUpdated(Object obj, Map<String, String> map, String type) {
        super.onNodeUpdated(obj, map, type);
        if (type.equalsIgnoreCase(InfoManager.INodeEventListener.ADD_SPECIAL_TOPIC_CHANNELS)) {
            List<ChannelNode> lstNodes = (List) obj;
            if (lstNodes.size() > 0 && map != null) {
                addChannels(lstNodes, 1, String.valueOf(1000001 + Integer.valueOf((String) map.get("id")).intValue()), 0);
            }

        } else if (type.equalsIgnoreCase(InfoManager.INodeEventListener.ADD_LIVE_CHANNELS_BYATTR)) {
            List<ChannelNode> lstNodes = (List) obj;
            Ln.e("通知存储" + (lstNodes == null ? "null" : lstNodes.size() + ""));
            if (lstNodes.size() > 0 && map != null) {
                String key = buildKey(((ChannelNode) lstNodes.get(0)).categoryId, (String) map.get("attr"));
                addChannels(lstNodes, 0, key, 0);
                return;
            }
            return;

        }
    }

    public static String buildKey(int catid, String attrPath) {
        String key = String.valueOf(catid);
        if (attrPath == null || attrPath.equalsIgnoreCase("") || attrPath.equalsIgnoreCase("0")) {
            return key;
        }
        return new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(key)).append("/").toString())).append(attrPath).toString();
    }

    private void addChannels(List<ChannelNode> lstChannels, int channelType, String key, int pos) {
        if (lstChannels != null) {
            int i;
            ChannelNode temp;
            if (channelType == 1) {
                for (i = 0; i < lstChannels.size(); i++) {
                    temp = (ChannelNode) this.mapVirtualChannels.get(((ChannelNode) lstChannels.get(i)).channelId);
                    if (temp != null) {
                        temp.updatePartialInfo((ChannelNode) lstChannels.get(i));
                    } else {
                        this.mapVirtualChannels.put(((ChannelNode) lstChannels.get(i)).channelId, (ChannelNode) lstChannels.get(i));
                    }
                }
            } else {
                for (i = 0; i < lstChannels.size(); i++) {
                    temp = (ChannelNode) this.mapLiveChannels.get(((ChannelNode) lstChannels.get(i)).channelId);
                    if (temp != null) {
                        temp.updatePartialInfo((ChannelNode) lstChannels.get(i));
                    } else {
                        this.mapLiveChannels.put(((ChannelNode) lstChannels.get(i)).channelId, (ChannelNode) lstChannels.get(i));
                    }
                }
            }
            if (key == null) {
                return;
            }
            if (this.mapChannelNodes.get(key) == null) {
                this.mapChannelNodes.put(key, lstChannels);
                return;
            }
            List<ChannelNode> lstnodes = (List) this.mapChannelNodes.get(key);
            int size = lstnodes.size();
            if (pos != size || size <= 0) {
                for (i = 0; i < lstChannels.size(); i++) {
                    boolean hasExisted = false;
                    for (int j = 0; j < lstnodes.size(); j++) {
                        if (((ChannelNode) lstnodes.get(j)).channelId == ((ChannelNode) lstChannels.get(i)).channelId) {
                            hasExisted = true;
                            ((ChannelNode) lstnodes.get(j)).updatePartialInfo((ChannelNode) lstChannels.get(i));
                            break;
                        }
                    }
                    if (!hasExisted) {
                        if (i < lstnodes.size()) {
                            lstnodes.add(i, (ChannelNode) lstChannels.get(i));
                        } else {
                            lstnodes.add((ChannelNode) lstChannels.get(i));
                        }
                    }
                }
                return;
            }
            ChannelNode node = (ChannelNode) lstnodes.get(lstnodes.size() - 1);
            node.nextSibling = (Node) lstChannels.get(0);
            ((ChannelNode) lstChannels.get(0)).prevSibling = node;
            lstnodes.addAll(lstChannels);
        }
    }

    private void dispatch2Observer(ChannelNode node) {
        if (this.mObservers != null) {
            IDataChangeObserver observer = (IDataChangeObserver) this.mObservers.get(node.channelId);
            if (observer != null) {
                observer.onChannelNodeInfoUpdate(node);
            }
        }
    }

    public void addObserver(int channelId, IDataChangeObserver observer) {
        if (this.mObservers == null) {
            this.mObservers = new SparseArray();
        }
        this.mObservers.put(channelId, observer);
    }

    public void removeObserver(int channelId) {
        if (this.mObservers != null) {
            this.mObservers.remove(channelId);
        }
    }

    public interface IDataChangeObserver {
        void onChannelNodeInfoUpdate(ChannelNode channelNode);
    }
}

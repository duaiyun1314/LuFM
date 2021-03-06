package com.andy.LuFM.data;

import android.util.Log;

import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.helper.ProgramHelper;
import com.andy.LuFM.model.CategoryNode;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramScheduleList;
import com.andy.LuFM.model.RecommendPlayingItemNode;
import com.andy.LuFM.model.RootNode;
import com.andy.LuFM.model.SpecialTopicNode;
import com.andy.LuFM.model.UserInfo;
import com.andy.LuFM.player.MediaCenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import roboguice.util.Ln;

/**
 * 存储必要信息
 */
public class InfoManager implements IResultRecvHandler {
    private static final String TAG = "InfoManager";
    private static InfoManager instance;
    private RootNode mRootNode = new RootNode();
    private Map<String, List<INodeEventListener>> mapNodeEventListeners = new HashMap();
    private Map<String, List<ISubscribeEventListener>> mapSubscribeEventListeners = new HashMap();
    private static int NOVEL_PAGE_SIZE = 50;
    private int ERROR_PROGRAM_CNT = 0;

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

    public void loadRecommendPlayingProgramsInfo(ISubscribeEventListener listener) {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        Map<String, Object> param = new HashMap();
        param.put("day", String.valueOf(day));
        if (listener != null) {
            registerSubscribeEventListener(listener, ISubscribeEventListener.RECV_RECOMMEND_PLAYING_PROGRAMS_INFO);
        }
        DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(RequestType.GET_RECOMMEND_PLAYING, param));
    }

    /**
     * 获取单个直播频道的详细信息
     *
     * @param channelId
     * @param listener
     */
    public void loadLiveChannelNode(int channelId, INodeEventListener listener) {
        if (listener != null) {
            Map<String, Object> param = new HashMap();
            param.put("id", String.valueOf(channelId));
            DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(RequestType.GET_LIVE_CHANNEL_INFO, param));
            registerNodeEventListener(listener, INodeEventListener.ADD_LIVE_CHANNEL_INFO);
        }
    }

    /**
     * 获取单个非直播频道的详细信息
     *
     * @param channelId
     * @param node
     */
    public void loadVirtualChannelNode(int channelId, Node node) {
        if (node != null) {
            registerNodeEventListener(node, INodeEventListener.ADD_VIRTUAL_CHANNEL_INFO);
            Map<String, Object> requestParam = new HashMap();
            requestParam.put("id", String.valueOf(channelId));
            DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(RequestType.GET_VIRTUAL_CHANNEL_INFO, requestParam));
        }
    }


    @Override
    public void onRecvResult(Result result, String type, Object param) {
        Map<String, String> mapParam;
        mapParam = (Map<String, String>) param;
        if (type.equalsIgnoreCase(RequestType.GET_VIRTUAL_CHANNEL_INFO)) {
            ChannelNode channelNode = (ChannelNode) result.getData();
            if (channelNode != null) {
                dispatchNodeEvent(channelNode, INodeEventListener.ADD_VIRTUAL_CHANNEL_INFO);
            }
        } else if (type.equalsIgnoreCase(RequestType.GET_LIVE_CHANNEL_INFO)) {
            ChannelNode node = (ChannelNode) result.getData();
            if (node != null) {
                dispatchNodeEvent(node, INodeEventListener.ADD_LIVE_CHANNEL_INFO);
            }
        } else if (type.equalsIgnoreCase(RequestType.GET_PODCASTER_BASEINFO)) {
            UserInfo user = (UserInfo) result.getData();
            if (user != null) {
                dispatchNodeEvent(user, INodeEventListener.ADD_PODCASTER_BASE);
                dispatchSubscribeEvent(ISubscribeEventListener.RECV_PODCASTER_BASEINFO);
            }
        } else if (type.equalsIgnoreCase(RequestType.RELOAD_VIRTUAL_PROGRAMS_SCHEDULE)) {
            ProgramScheduleList psl = (ProgramScheduleList) result.getData();
            if (psl != null) {
                dispatchNodeEvent(psl, mapParam, INodeEventListener.ADD_RELOAD_VIRTUAL_PROGRAMS_SCHEDULE);
                dispatchSubscribeEvent(ISubscribeEventListener.RECV_RELOAD_PROGRAMS_SCHEDULE);
            }

        } else if (type.equalsIgnoreCase(RequestType.GET_VIRTUAL_PROGRAM_SCHEDULE)) {
            ProgramScheduleList psl = (ProgramScheduleList) result.getData();
            if (psl != null) {
                dispatchNodeEvent(psl, (Map) param, INodeEventListener.ADD_VIRTUAL_PROGRAMS_SCHEDULE);
                dispatchSubscribeEvent(ISubscribeEventListener.RECV_PROGRAMS_SCHEDULE);
            }
        } else if (type.equalsIgnoreCase(RequestType.GET_LIVE_PROGRAM_SCHEDULE)) {
            ProgramScheduleList psl = (ProgramScheduleList) result.getData();
            if (psl != null) {
                dispatchNodeEvent(psl, (Map) param, INodeEventListener.ADD_LIVE_PROGRAMS_SCHEDULE);
                dispatchSubscribeEvent(ISubscribeEventListener.RECV_PROGRAMS_SCHEDULE);

            }
        } else if (type.equalsIgnoreCase(RequestType.GET_LIST_MEDIACENTER)) {
            MediaCenter center = (MediaCenter) result.getData();
            if (center != null) {
                MediaCenter.getInstance().setMediaCenter(center);
                // MediaCenter.getInstance().pkMediaCenter();
            }

        } else if (type.equalsIgnoreCase(RequestType.GET_SPECIAL_TOPIC_CHANNELS)) {
            SpecialTopicNode stNode = (SpecialTopicNode) result.getData();
            if (stNode != null) {
                List<ChannelNode> lstNodes = stNode.getlstChannels();
                if (lstNodes != null && lstNodes.size() > 0) {
                    dispatchNodeEvent(lstNodes, mapParam, INodeEventListener.ADD_SPECIAL_TOPIC_CHANNELS);
                }
                dispatchNodeEvent(stNode, mapParam, INodeEventListener.ADD_SPECIAL_TOPIC);
                dispatchSubscribeEvent(ISubscribeEventListener.RECV_SPECIAL_TOPIC_CHANNELS);
            }

        } else if (type.equalsIgnoreCase(RequestType.GET_RECOMMEND_PLAYING)) {
            List<RecommendPlayingItemNode> lstNodes3 = (List) result.getData();
            if (lstNodes3 != null) {
                root().mRecommendPlayingInfo.setRecommendList(lstNodes3);
                dispatchSubscribeEvent(ISubscribeEventListener.RECV_RECOMMEND_PLAYING_PROGRAMS_INFO);
            }
        } else if (type.equalsIgnoreCase(RequestType.GET_ADVERTISEMENT_ADDRESS)) {
            if (result.isSuccess()) {
                String url = (String) result.getData();
                EventBus.getDefault().post(url);
            } else {
                EventBus.getDefault().post("");
            }
        } else if (type.equalsIgnoreCase(RequestType.GET_LIST_LIVE_CHANNELS)) {
            List<ChannelNode> channelNodes = (List) result.getData();
            if (channelNodes != null) {
                dispatchNodeEvent(channelNodes, mapParam, INodeEventListener.ADD_LIVE_CHANNELS_BYATTR);
                dispatchSubscribeEvent(ISubscribeEventListener.RECV_LIVE_CHANNELS_BYATTR);

            }
        }

    }

    private void dispatchNodeEvent(Object node, String type) {
        if (node != null && this.mapNodeEventListeners.containsKey(type)) {
            List<INodeEventListener> lstListeners = (List) this.mapNodeEventListeners.get(type);
            for (int i = 0; i < lstListeners.size(); i++) {
                ((INodeEventListener) lstListeners.get(i)).onNodeUpdated(node, type);
            }
        }
    }

    /**
     * 通知存储node的类
     *
     * @param node
     * @param map
     * @param type
     */
    private void dispatchNodeEvent(Object node, Map<String, String> map, String type) {
        if (node != null && this.mapNodeEventListeners.containsKey(type)) {
            List<INodeEventListener> lstListeners = (List) this.mapNodeEventListeners.get(type);
            for (int i = 0; i < lstListeners.size(); i++) {
                ((INodeEventListener) lstListeners.get(i)).onNodeUpdated(node, map, type);
            }
        }
    }


    /**
     * 只是通知最初请求的类加载完成
     *
     * @param type
     */
    private void dispatchSubscribeEvent(String type) {
        if (this.mapSubscribeEventListeners.containsKey(type)) {
            int i;
            List<ISubscribeEventListener> lstListeners = (List) this.mapSubscribeEventListeners.get(type);
            int size = lstListeners.size();
            for (i = 0; i < size; i++) {
                ((ISubscribeEventListener) lstListeners.get(i)).onNotification(type);
            }
            if (false) {
                boolean delete = true;
                int count = 0;
                i = 0;
                while (i < size && count < size) {
                    count++;
                    int j = i + 1;
                    if (j < size) {
                        while (j < size) {
                            if (lstListeners.get(i) == lstListeners.get(j)) {
                                delete = false;
                                break;
                            }
                            j++;
                        }
                    } else {
                        delete = true;
                    }
                    if (delete) {
                        try {
                            lstListeners.remove(i);
                            i--;
                        } catch (Exception e) {
                            return;
                        }
                    }
                    i++;
                }
            }
        }
    }

    public void loadDataCenterList() {
        DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(RequestType.GET_LIST_MEDIACENTER, null));

    }

    public interface INodeEventListener {
        String ADD_CATEGORY_ALL_CHANNELS = "ADD_CATEGORY_ALL_CHANNELS";
        String ADD_LIVE_CHANNELS_BYATTR = "ADD_LIVE_CHANNELS_BYATTR";
        String ADD_LIVE_CHANNEL_INFO = "ADD_LIVE_CHANNEL_INFO";
        String ADD_LIVE_PROGRAMS_SCHEDULE = "ADD_LIVE_PROGRAMS_SCHEDULE";
        String ADD_MY_PODCASTER_LIST = "ADD_MY_PODCASTER_LIST";
        String ADD_PODCASTER_BASE = "ADD_PODCASTER_BASE";
        String ADD_PODCASTER_CHANNELS = "ADD_PODCASTER_CHANNELS";
        String ADD_PODCASTER_DETAIL = "ADD_PODCASTER_DETAIL";
        String ADD_PODCASTER_LATEST = "ADD_PODCASTER_LATEST";
        String ADD_RELOAD_VIRTUAL_PROGRAMS_SCHEDULE = "ADD_RELOAD_VIRTUAL_PROGRAMS_SCHEDULE";
        String ADD_SPECIAL_TOPIC = "ADD_SPECIAL_TOPIC";
        String ADD_SPECIAL_TOPIC_CHANNELS = "ADD_SPECIAL_TOPIC_CHANNELS";
        String ADD_VIRTUAL_CHANNELS_BYATTR = "ADD_VIRTUAL_CHANNELS_BYATTR";
        String ADD_VIRTUAL_CHANNEL_INFO = "ADD_VIRTUAL_CHANNEL_INFO";
        String ADD_VIRTUAL_PROGRAMS_SCHEDULE = "ADD_VIRTUAL_PROGRAMS_SCHEDULE";

        void onNodeUpdated(Object obj, String str);

        void onNodeUpdated(Object obj, Map<String, String> map, String str);
    }

    public void registerNodeEventListener(INodeEventListener listener, String type) {
        if (listener != null && type != null) {
            List<INodeEventListener> lstListeners;
            if (this.mapNodeEventListeners.containsKey(type)) {
                lstListeners = (List) this.mapNodeEventListeners.get(type);
                int i = 0;
                while (i < lstListeners.size()) {
                    if (lstListeners.get(i) != listener) {
                        Node newNode = (Node) listener;
                        Node existNode = (Node) lstListeners.get(i);
                        if (newNode.nodeName.equalsIgnoreCase("category") && existNode.nodeName.equalsIgnoreCase("category")) {
                            if (((CategoryNode) newNode).categoryId == ((CategoryNode) existNode).categoryId) {
                                lstListeners.remove(i);
                            }
                        } else if (newNode.nodeName.equalsIgnoreCase("channel") && existNode.nodeName.equalsIgnoreCase("channel") && ((ChannelNode) newNode).channelId == ((ChannelNode) existNode).channelId) {
                            lstListeners.remove(i);
                        }
                        i++;
                    } else {
                        return;
                    }
                }
                ((List) this.mapNodeEventListeners.get(type)).add(listener);
                return;
            }
            lstListeners = new ArrayList();
            lstListeners.add(listener);
            this.mapNodeEventListeners.put(type, lstListeners);
        }
    }

    public void loadPodcasterBaseInfo(int id, ISubscribeEventListener listener) {
        Map<String, Object> param = new HashMap();
        param.put("id", String.valueOf(id));
        if (listener != null) {
            registerSubscribeEventListener(listener, ISubscribeEventListener.RECV_PODCASTER_BASEINFO);
        }
        DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(RequestType.GET_PODCASTER_BASEINFO, param));
    }

    public void registerSubscribeEventListener(ISubscribeEventListener listener, String type) {
        if (listener != null && type != null) {
            List<ISubscribeEventListener> lstListeners;
            if (this.mapSubscribeEventListeners.containsKey(type)) {
                lstListeners = (List) this.mapSubscribeEventListeners.get(type);
                int i = 0;
                while (i < lstListeners.size()) {
                    if (lstListeners.get(i) != listener) {
                        i++;
                    } else {
                        return;
                    }
                }
                lstListeners.add(listener);
                return;
            }
            lstListeners = new ArrayList();
            lstListeners.add(listener);
            this.mapSubscribeEventListeners.put(type, lstListeners);
        }
    }

    public boolean unRegisterSubscribeEventListener(ISubscribeEventListener listener, String... types) {
        if (!(listener == null || types == null || types.length <= 0)) {
            for (String type : types) {
                if (this.mapSubscribeEventListeners.containsKey(type)) {
                    List<ISubscribeEventListener> lstListeners = (List) this.mapSubscribeEventListeners.get(type);
                    for (int j = 0; j < lstListeners.size(); j++) {
                        if (lstListeners.get(j) == listener) {
                            lstListeners.remove(j);
                            return true;
                        }
                    }
                    continue;
                }
            }
        }
        return false;
    }

    /**
     * 简单的消息通知，只是携带消息类型
     */
    public interface ISubscribeEventListener {
        public static final String RECV_ACTIVITY_LIST = "RACTL";
        public static final String RECV_ADVERTISEMENTS_INFO = "RADI";
        public static final String RECV_ALBUM_LIST = "RAL";
        public static final String RECV_ATTRIBUTES = "RECV_ATTRIBUTES";
        public static final String RECV_BILLBOARD_CHANNELS = "RBCS";
        public static final String RECV_BILLBOARD_PROGRAMS = "RBPS";
        public static final String RECV_CHECK_IN_STATUS = "RCIS";
        public static final String RECV_CONTENT_CATEGORY = "RCC";
        public static final String RECV_CURRENT_PLAYING_PROGRAMS_LIST = "RCPPL";
        public static final String RECV_CURRENT_PROGRAM_TOPICS = "RCPT";
        public static final String RECV_EMPTY_ADVERTISEMENTS = "RECV_EMPTY_AD";
        public static final String RECV_FRONTPAGE_BANNER_LOADED = "RFBL";
        public static final String RECV_FRONTPAGE_LOADED = "RFLO";
        public static final String RECV_GUIDE_CATEGORY_LIST = "RGCL";
        public static final String RECV_LINK_INFO = "RLI";
        public static final String RECV_LIVE_CATEGORY = "RLC";
        public static final String RECV_LIVE_CATEGORY_V2 = "RLCV2";
        public static final String RECV_LIVE_CHANNELS_BYATTR = "RECV_LIVE_CHANNELS_BYATTR";
        public static final String RECV_LIVE_CHANNEL_LIST = "RLCL";
        public static final String RECV_LOCAL_CATEGORY = "RECV_LOCAL_CATEGORY";
        public static final String RECV_LOCAL_RECOMMEND_INFO = "RECV_LOCAL_RECOMMEND_INFO";
        public static final String RECV_MY_PODCASTER_LIST = "RECV_MY_PODCASTER_LIST";
        public static final String RECV_NOVEL_CATEGORY = "RNC";
        public static final String RECV_ONDEMAND_PROGRAM_LIST = "ROPL";
        public static final String RECV_PODCASTER_BASEINFO = "RECV_PODCASTER_BASEINFO";
        public static final String RECV_PODCASTER_CHANNELS = "RECV_PODCASTER_CHANNELS";
        public static final String RECV_PODCASTER_DETAILINFO = "RECV_PODCASTER_DETAILINFO";
        public static final String RECV_PODCASTER_LATEST = "RECV_PODCASTER_LATEST";
        public static final String RECV_PODCAST_CATEGORY = "RPC";
        public static final String RECV_PROGRAMS_SCHEDULE = "RPS";
        public static final String RECV_RECOMMEND_CATEGORY_V2 = "RRCV";
        public static final String RECV_RECOMMEND_CATEGORY_V2_BANNER = "PRCVB";
        public static final String RECV_RECOMMEND_INFO = "RECV_RECOMMEND_INFO";
        public static final String RECV_RECOMMEND_PLAYING_PROGRAMS_INFO = "RRPPI";
        public static final String RECV_RELOAD_PROGRAMS_SCHEDULE = "RECV_RELOAD_PROGRAMS_SCHEDULE";
        public static final String RECV_RINGTONE_LIST = "RRTNL";
        public static final String RECV_SEARCH_HOT_KEYWORDS = "RSHK";
        public static final String RECV_SEARCH_LOADMORE = "RSLOADMORE";
        public static final String RECV_SEARCH_RESULT_LIST = "RSRL";
        public static final String RECV_SEARCH_SUGGESTIONS = "RSSUGG";
        public static final String RECV_SHARE_INFO_NODE = "RSIN";
        public static final String RECV_SPECIAL_TOPIC_CHANNELS = "RECV_SPECIAL_TOPIC_CHANNELS";
        public static final String RECV_SUB_CATEGORY = "RSC";
        public static final String RECV_USER_INFO_UPDATE = "RUIU";
        public static final String RECV_VIEWTIME_UPDATED = "viewTimeUpdated";
        public static final String RECV_VIRTUAL_CATEGORY_LIST = "RVCAL";
        public static final String RECV_VIRTUAL_CHANNELS_BYATTR = "RECV_VIRTUAL_CHANNELS_BYATTR";
        public static final String RECV_VIRTUAL_CHANNEL_LIST = "RVCL";
        public static final String RECV_VIRTUAL_DIMENTION_LIST = "RVDL";
        public static final String RECV_VIRTUAL_PROGRAM_INFO = "RVPI";
        public static final String RECV_WECHAT_TOKEN = "RECV_WECHAT_TOKEN";
        public static final String RECV_WECHAT_USER_INFO = "RECV_WECHAT_USER_INFO";
        public static final String RECV_WSQ_NEW = "WSQNEW";
        public static final String REFR_WECHAT_TOKEN = "REFR_WECHAT_TOKEN";
        public static final String SUBSCRIBE_ALL_EVENTS = "SAE";
        public static final String GET_AD = "GET_AD";

        void onNotification(String str);

        //void onRecvDataException(String str, DataExceptionStatus dataExceptionStatus);
    }

    public void loadProgramsScheduleNode(ChannelNode node, ISubscribeEventListener listener) {
        if (node != null) {
            if (node.channelType == 0) {
                loadLiveProgramSchedule(ProgramHelper.getInstance(), node.channelId, null, listener);
                return;
            }
            int page;
            int size = node.getLoadedProgramSize() + this.ERROR_PROGRAM_CNT;
            if (node.isNovelChannel()) {
                page = size / NOVEL_PAGE_SIZE;
            } else {
                page = size / getProgramPageSize();
            }
            loadVirtualProgramsScheduleNode(ProgramHelper.getInstance(), node.channelId, node.isNovelChannel(), page + 1, size, listener);
        }
    }

    public int getProgramPageSize() {
        return 30;
    }

    public void loadLiveProgramSchedule(Node node, int channelid, String days, ISubscribeEventListener listener) {
        if (node != null) {
            if (listener != null) {
                registerSubscribeEventListener(listener, ISubscribeEventListener.RECV_PROGRAMS_SCHEDULE);
            }
            String requestType = RequestType.GET_LIVE_PROGRAM_SCHEDULE;
            Map<String, Object> param = new HashMap();
            param.put("id", String.valueOf(channelid));
            param.put("day", days);
            DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(requestType, param));
            if (node != null) {
                registerNodeEventListener(node, INodeEventListener.ADD_LIVE_PROGRAMS_SCHEDULE);
            }
        }
    }


    public void loadVirtualProgramsScheduleNode(Node node, int channelid, boolean isnovel, int page, int curSize, ISubscribeEventListener listener) {
        int page_size = 30;
        //int order = root().getProgramListOrder(channelid);
        int order = 0;//正序
        if (isnovel) {
            page_size = NOVEL_PAGE_SIZE;
        }
        if (listener != null) {
            registerSubscribeEventListener(listener, ISubscribeEventListener.RECV_PROGRAMS_SCHEDULE);
        }
        String requestType = RequestType.GET_VIRTUAL_PROGRAM_SCHEDULE;
        Map<String, Object> param = new HashMap();
        param.put("id", String.valueOf(channelid));
        param.put("pagesize", String.valueOf(page_size));
        param.put("page", String.valueOf(page));
        param.put("order", String.valueOf(order));
        if (node != null) {
            registerNodeEventListener(node, INodeEventListener.ADD_VIRTUAL_PROGRAMS_SCHEDULE);
        }
        DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(requestType, param));
    }

    public void reloadVirtualProgramsSchedule(ChannelNode node, ISubscribeEventListener listener) {
        if (node != null) {
            Ln.d("sym:刷新节目单  id=" + node.channelId);
            //  int order = root().getProgramListOrder(node.channelId);
            int order = 0;//正序
            String requestType = RequestType.RELOAD_VIRTUAL_PROGRAMS_SCHEDULE;
            Map<String, Object> param = new HashMap();
            param.put("id", String.valueOf(node.channelId));
            if (node.isNovelChannel()) {
                param.put("pagesize", String.valueOf(NOVEL_PAGE_SIZE));
            } else {
                param.put("pagesize", String.valueOf(30));
            }
            param.put("page", String.valueOf(1));
            param.put("order", String.valueOf(order));
            registerNodeEventListener(ProgramHelper.getInstance(), INodeEventListener.ADD_RELOAD_VIRTUAL_PROGRAMS_SCHEDULE);
            if (listener != null) {
                registerSubscribeEventListener(listener, ISubscribeEventListener.RECV_RELOAD_PROGRAMS_SCHEDULE);
            }
            DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(requestType, param));
        }
    }

    List<Integer> liveChannel = null;

    public boolean isTestLiveChannel(int resid) {
        if (this.liveChannel != null) {
            return this.liveChannel.contains(Integer.valueOf(resid));
        }
        return false;
    }

    /**
     * 获取单个标签下直播列表
     *
     * @param listener
     */
    public void loadListLiveChannelsByAttr(CategoryNode categoryNode, ISubscribeEventListener listener) {
        String attrs = categoryNode.getmAttributesPath();
        if (attrs != null && !attrs.equalsIgnoreCase("")) {
            if (listener != null) {
                registerSubscribeEventListener(listener, ISubscribeEventListener.RECV_LIVE_CHANNELS_BYATTR);
            }
            Map<String, Object> requestParam = new HashMap();
            requestParam.put("id", String.valueOf(categoryNode.categoryId));
            requestParam.put("attr", attrs);
            requestParam.put("page", String.valueOf(1));
            requestParam.put("pagesize", String.valueOf(100));
            registerNodeEventListener(ChannelHelper.getInstance(), INodeEventListener.ADD_LIVE_CHANNELS_BYATTR);
            DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(RequestType.GET_LIST_LIVE_CHANNELS, requestParam));
        }
    }

    public void loadSpecialTopicNode(SpecialTopicNode node, ISubscribeEventListener listener) {
        if (node != null) {
            if (listener != null) {
                registerSubscribeEventListener(listener, ISubscribeEventListener.RECV_SPECIAL_TOPIC_CHANNELS);
            }
            if (node != null) {
                registerNodeEventListener(node, INodeEventListener.ADD_SPECIAL_TOPIC);
            }
            Map<String, Object> requestParam = new HashMap();
            requestParam.put("id", String.valueOf(node.getApiId()));
            DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(RequestType.GET_SPECIAL_TOPIC_CHANNELS, requestParam));
        }
    }

    public void loadAdvertisement() {
        DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(RequestType.GET_ADVERTISEMENT_ADDRESS, null));
    }


}

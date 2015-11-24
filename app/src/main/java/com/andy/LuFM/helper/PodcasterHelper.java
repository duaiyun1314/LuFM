package com.andy.LuFM.helper;

import android.text.TextUtils;

import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.UserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanglu on 15/11/24.
 */
public class PodcasterHelper extends Node {
    private static PodcasterHelper _instance = null;
    private Map<Integer, UserInfo> mapPodcasters;

    private PodcasterHelper() {
        this.mapPodcasters = new HashMap();
        this.nodeName = "podcasterhelper";
    }

    public static synchronized PodcasterHelper getInstance() {
        PodcasterHelper podcasterHelper;
        synchronized (PodcasterHelper.class) {
            if (_instance == null) {
                _instance = new PodcasterHelper();
                _instance.init();
            }
            podcasterHelper = _instance;
        }
        return podcasterHelper;
    }

    public void init() {
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_PODCASTER_BASE);
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_PODCASTER_CHANNELS);
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_PODCASTER_DETAIL);
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_PODCASTER_LATEST);
        InfoManager.getInstance().registerNodeEventListener(this, InfoManager.INodeEventListener.ADD_MY_PODCASTER_LIST);
    }

    public UserInfo getPodcaster(int id) {
        UserInfo user = (UserInfo) this.mapPodcasters.get(Integer.valueOf(id));
        if (user == null || TextUtils.isEmpty(user.podcasterName)) {
            UserInfo temp = null;
            if (user != null) {
                user.updateUserInfo(temp);
            }
        }
        if (user == null) {
            user = new UserInfo();
            user.podcasterId = id;
            user.podcasterName = "\u52a0\u8f7d\u4e2d";
            user.isPodcaster = true;
            InfoManager.getInstance().loadPodcasterBaseInfo(user.podcasterId, null);
        }
        this.mapPodcasters.put(Integer.valueOf(user.podcasterId), user);
        return user;
    }


    @Override
    public void onNodeUpdated(Object obj, String type) {
        if (type.equalsIgnoreCase(InfoManager.INodeEventListener.ADD_PODCASTER_BASE)) {
            addPodcaster((UserInfo) obj);
        }
    }

    private void addPodcaster(UserInfo user) {
        if (user != null) {
            UserInfo temp = (UserInfo) this.mapPodcasters.get(Integer.valueOf(user.podcasterId));
            if (temp == null) {
                this.mapPodcasters.put(Integer.valueOf(user.podcasterId), user);
            } else {
                temp.updateUserInfo(user);
            }
            Map<String, Object> param = new HashMap();
            param.put("userInfo", user);
            //  DataManager.getInstance().getData(RequestType.UPDATEDB_PODCASTER_INFO, null, param);
        }
    }
}

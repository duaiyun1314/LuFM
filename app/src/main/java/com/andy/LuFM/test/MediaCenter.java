package com.andy.LuFM.test;


import com.andy.LuFM.Utils.ToolKit;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.data.RequestType;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaCenter {
    public static final String LIVE_CHANNEL_DOWNLOAD = "radiodownload";
    public static final String LIVE_CHANNEL_PLAY = "radiohls";
    public static final String VIRUTAL_CHANNEL = "virutalchannel";
    public static MediaCenter _instance;
    private List<PingInfoV6> lstRes = new ArrayList();
    public String mDeviceId;
    public Map<String, List<PingInfoV6>> mapMediaCenters = new HashMap();
    public Map<String, List<PingInfoV6>> mapMediaCentersToDB;
    public String region = "CN";

    class PingInfoV6Comparator implements Comparator<PingInfoV6> {
        PingInfoV6Comparator() {
        }

        public int compare(PingInfoV6 key1, PingInfoV6 key2) {
            if (key1.getResult() > key2.getResult()) {
                return 1;
            }
            if (key1.getResult() < key2.getResult()) {
                return -1;
            }
            return 0;
        }
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public static MediaCenter getInstance() {
        if (_instance == null) {
            _instance = new MediaCenter();
        }
        return _instance;
    }

    public void init(String device) {
        this.mDeviceId = device;
    }

    /*public void restoreMediaCenter() {
        Result result = DataManager.getInstance().getData(RequestType.GETDB_MEDIA_CENTER, null, null).getResult();
        Map<String, List<PingInfoV6>> res = null;
        if (result.getSuccess()) {
            res = (Map) result.getData();
        }
        if (res != null && res.size() > 0) {
            this.mapMediaCenters = res;
        }
    }*/

 /*   public void updateMediaCenter() {
        if (this.mapMediaCentersToDB != null && this.mapMediaCentersToDB.size() != 0) {
            Map<String, Object> param = new HashMap();
            param.put("mediacenter", this.mapMediaCentersToDB);
            DataManager.getInstance().getData(RequestType.UPDATEDB_MEDIA_CENTER, null, param);
        }
    }*/

    public void setMediaCenter(MediaCenter mediacenter) {
        if (this.mapMediaCenters.size() == 0) {
            this.mapMediaCenters = mediacenter.mapMediaCenters;
        }
        this.mapMediaCentersToDB = mediacenter.mapMediaCenters;
    }

  /*  public void pkMediaCenter() {
        if (this.mapMediaCenters.size() != 0) {
            PickDataCenterV6 temp = new PickDataCenterV6();
            temp.setDataCenterInfo(this.mapMediaCenters);
            temp.start();
        }
    }*/

    public List<PingInfoV6> getPingInfo(String type) {
        if (type == null) {
            return null;
        }
        return (List) this.mapMediaCenters.get(type);
    }

    public String getReplayDownloadPath(String type, String resid, int bitrate, String start, String end) {
        if (type == null || resid == null || resid.equalsIgnoreCase("")) {
            return null;
        }
        List<PingInfoV6> lstPingInfo = getLstPingInfoAfterPing(type, resid);
        if (lstPingInfo == null || lstPingInfo.size() == 0 || 0 >= lstPingInfo.size()) {
            return null;
        }
        return ((PingInfoV6) lstPingInfo.get(0)).getReplayUrl(resid, this.mDeviceId, bitrate, start, end);
    }

    public String getVirtualProgramDownloadPath(String type, String resid, int bitrate) {
        if (type == null || resid == null || resid.equalsIgnoreCase("")) {
            return null;
        }
        List<PingInfoV6> lstPingInfo = getLstPingInfoAfterPing(type, resid);
        if (lstPingInfo == null || lstPingInfo.size() == 0 || 0 >= lstPingInfo.size()) {
            return null;
        }
        return ((PingInfoV6) lstPingInfo.get(0)).getAccessUrl(resid, this.mDeviceId, bitrate);
    }

    public String getShareUrl(String type, String resid, int bitrate) {
        if (type == null || resid == null || resid.equalsIgnoreCase("")) {
            return null;
        }
        List<PingInfoV6> lstPingInfo = getLstPingInfoAfterPing(type, resid);
        if (lstPingInfo == null || lstPingInfo.size() == 0) {
            return null;
        }
        String urls = "http://";
        PingInfoV6 ping = (PingInfoV6) lstPingInfo.get(0);
        if (type.equalsIgnoreCase(LIVE_CHANNEL_PLAY)) {
            urls = new StringBuilder(String.valueOf(urls)).append("hls.hz.qingting.fm").toString();
        } else if (type.equalsIgnoreCase(VIRUTAL_CHANNEL)) {
            urls = new StringBuilder(String.valueOf(urls)).append("od.qingting.fm").toString();
        }
        return new StringBuilder(String.valueOf(urls)).append(ping.getAccessUrl(resid, this.mDeviceId, bitrate)).toString();
    }

    public String getPlayUrls(String type, String resid, int bitrate, int channelId) {
        String str = null;
        if (!(type == null || resid == null || resid.equalsIgnoreCase("") || resid.equalsIgnoreCase("0"))) {
            List<PingInfoV6> lstPingInfo = getLstPingInfoAfterPing(type, resid);
            if (!(lstPingInfo == null || lstPingInfo.size() == 0)) {
                str = "";
                if (type == LIVE_CHANNEL_PLAY && InfoManager.getInstance().isTestLiveChannel(Integer.valueOf(resid).intValue())) {
                    str = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf("rtmp://rtmplive.qingting.fm/qtrtmp/" + resid)).append("?deviceid=").append("notification").toString())).append("&cid=").append(channelId).toString())).append("&phonetype=Android").toString())).append("&region=").append(this.region).toString())).append(";;").toString();
                }
                for (int i = 0; i < lstPingInfo.size(); i++) {
                    PingInfoV6 ping = (PingInfoV6) lstPingInfo.get(i);
                    str = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(str)).append("http://").toString())).append(ping.getDomainIP()).toString())).append(ping.getAccessUrl(resid, this.mDeviceId, bitrate)).toString();
                    if (type == LIVE_CHANNEL_PLAY) {
                        str = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(str)).append("&cid=").append(channelId).toString())).append("&phonetype=Android").toString())).append("&region=").append(this.region).toString();
                    }
                    str = new StringBuilder(String.valueOf(str)).append(";;").toString();
                }
            }
        }
        return str;
    }

    public String getShareReplayUrl(String resid, int bitrate, String start, String end) {
        if (resid == null || resid.equalsIgnoreCase("") || start == null || end == null || resid.equalsIgnoreCase("0")) {
            return null;
        }
        List<PingInfoV6> lstPingInfo = getLstPingInfoAfterPing(LIVE_CHANNEL_PLAY, resid);
        if (lstPingInfo == null || lstPingInfo.size() == 0) {
            return null;
        }
        PingInfoV6 ping = (PingInfoV6) lstPingInfo.get(0);
        return new StringBuilder(String.valueOf(new StringBuilder(String.valueOf("http://" + "hls.hz.qingting.fm")).append(ping.getReplayUrl(resid, this.mDeviceId, bitrate, start, end)).toString())).append(";;").toString();
    }

    public String getReplayUrls(String resid, int bitrate, String start, String end) {
        String str = null;
        if (!(resid == null || resid.equalsIgnoreCase("") || start == null || end == null)) {
            List<PingInfoV6> lstPingInfo = getLstPingInfoAfterPing(LIVE_CHANNEL_PLAY, resid);
            if (!(lstPingInfo == null || lstPingInfo.size() == 0)) {
                str = "";
                for (int i = 0; i < lstPingInfo.size(); i++) {
                    PingInfoV6 ping = (PingInfoV6) lstPingInfo.get(i);
                    str = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(str)).append("http://").toString())).append(ping.getDomainIP()).toString())).append(ping.getReplayUrl(resid, this.mDeviceId, bitrate, start, end)).toString())).append(";;").toString();
                }
            }
        }
        return str;
    }


    private List<PingInfoV6> getLstPingInfoAfterPing(String str, String str2) {
        int i = 0;
        if (str == null || str2 == null) {
            return null;
        }
        List<PingInfoV6> list = (List) this.mapMediaCenters.get(str);
        if (list == null || list.size() == 0) {
            return null;
        }
        int i2;
        if (str.equalsIgnoreCase(LIVE_CHANNEL_PLAY) || str.equalsIgnoreCase(LIVE_CHANNEL_DOWNLOAD)) {
            str2 = this.mDeviceId;
        }
        int i3 = 0;
        for (i2 = 0; i2 < list.size(); i2++) {
            i3 += ((PingInfoV6) list.get(i2)).weight;
        }
        if (i3 == 0) {
            return list;
        }
        long hashCode = hashCode(str2) % ((long) i3);
        i2 = 0;
        i3 = 0;
        int i4 = 0;
        while (i2 < list.size()) {
            i3 += ((PingInfoV6) list.get(i2)).weight;
            if (((long) i4) <= hashCode && hashCode <= ((long) i3)) {
                i3 = i2;
                break;
            }
            i2++;
            i4 = i3;
        }
        i3 = 0;
        this.lstRes.clear();
        for (i2 = 0; i2 < list.size(); i2++) {
            PingInfoV6 pingInfoV6 = new PingInfoV6();
            pingInfoV6.update((PingInfoV6) list.get(i2));
            this.lstRes.add(pingInfoV6);
        }
        while (i < this.lstRes.size()) {
            if (i != i3) {
                ((PingInfoV6) this.lstRes.get(i)).setResult(((PingInfoV6) list.get(i)).getReachTime());
            }
            i++;
        }
        ((PingInfoV6) this.lstRes.get(i3)).setResult(((PingInfoV6) this.lstRes.get(i3)).getReachTime() - ((PingInfoV6) this.lstRes.get(i3)).pcc);
        Collections.sort(this.lstRes, new PingInfoV6Comparator());
        return this.lstRes;
    }

    private long hashCode(String key) {
        if (key == null || key.equalsIgnoreCase("")) {
            return 0;
        }
        try {
            return ByteBuffer.wrap(ToolKit.md5(key).getBytes()).getLong();
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }
}

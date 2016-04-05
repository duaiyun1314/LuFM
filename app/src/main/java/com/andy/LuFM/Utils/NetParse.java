package com.andy.LuFM.Utils;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.andy.LuFM.app.PlayApplication;
import com.andy.LuFM.data.IResultRecvHandler;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.event.PlayActionEvent;
import com.andy.LuFM.model.ActivityNode;
import com.andy.LuFM.model.BroadcasterNode;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.model.ProgramSchedule;
import com.andy.LuFM.model.ProgramScheduleList;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.RecommendPlayingItemNode;
import com.andy.LuFM.model.SpecialTopicNode;
import com.andy.LuFM.model.UserInfo;
import com.andy.LuFM.player.MediaCenter;
import com.andy.LuFM.player.PingInfoV6;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wanglu on 15/11/20.
 */
public class NetParse {
    private static NetParse instance;

    private NetParse() {

    }

    public synchronized static NetParse getInstance() {
        if (instance == null) {
            instance = new NetParse();
        }
        return instance;
    }

    private class ParseAsyncTask extends AsyncTask<Void, Void, Result> {

        private String responseString;
        private String type;
        private IResultRecvHandler iResultRecvHandler;
        private Object param;

        private ParseAsyncTask(String responseString, String type, IResultRecvHandler iResultRecvHandler, Object param) {
            this.responseString = responseString;
            this.type = type;
            this.iResultRecvHandler = iResultRecvHandler;
            this.param = param;
        }

        @Override
        protected Result doInBackground(Void... params) {
            return parseMethod(responseString, type);
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            iResultRecvHandler.onRecvResult(result, type, param);
        }
    }

    public void parse(String responseString, String type, IResultRecvHandler iResultRecvHandler, Object param) {
        new ParseAsyncTask(responseString, type, iResultRecvHandler, param).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{});
    }

    public Result parseMethod(String responseString, String type) {
        Result result = new Result();
        if (type == RequestType.DATA_TYPE_GET_RECOMMEND) {
            RecommendCategoryNode recommendCategoryNode = parseRecommendInfo(responseString);
            if (recommendCategoryNode != null) {
                result.setSuccess(true);
                result.setData(recommendCategoryNode);
            } else {
                result.setSuccess(false);
            }
            return result;

        } else if (type == RequestType.GET_LIVE_CHANNEL_INFO || type == RequestType.GET_VIRTUAL_CHANNEL_INFO) {
            ChannelNode channelNode = parseChannelNode(responseString);
            if (channelNode != null) {
                result.setSuccess(true);
                result.setData(channelNode);
            } else {
                result.setSuccess(false);
            }
            return result;
        } else if (type.equalsIgnoreCase(RequestType.GET_PODCASTER_BASEINFO)) {
            try {
                JSONObject data = (new JSONObject(responseString)).getJSONObject("data");
                UserInfo userInfo = _parsePodcaster(data);
                if (userInfo != null) {
                    result.setSuccess(true);
                    result.setData(userInfo);
                } else {
                    result.setSuccess(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setSuccess(false);
            }
            return result;
        } else if (type.equalsIgnoreCase(RequestType.RELOAD_VIRTUAL_PROGRAMS_SCHEDULE) || type.equalsIgnoreCase(RequestType.GET_VIRTUAL_PROGRAM_SCHEDULE)) {
            ProgramScheduleList programScheduleList = parseVirtualProgramSchedule(responseString);
            if (programScheduleList != null) {
                result.setSuccess(true);
                result.setData(programScheduleList);

            }
            return result;
        } else if (type.equalsIgnoreCase(RequestType.GET_LIST_MEDIACENTER)) {
            MediaCenter center = parseMediaCenter(responseString);
            if (center != null) {
                result.setSuccess(true);
                result.setData(center);
            }
            return result;

        } else if (type.equalsIgnoreCase(RequestType.GET_SPECIAL_TOPIC_CHANNELS)) {
            SpecialTopicNode specialTopicNode = parseSpecialTopicChannels(responseString);
            if (specialTopicNode != null) {
                result.setSuccess(true);
                result.setData(specialTopicNode);
            }
            return result;
        } else if (type.equalsIgnoreCase(RequestType.GET_RECOMMEND_PLAYING)) {
            List<RecommendPlayingItemNode> recommendPlayingItemNodes = parseRecommendPlayingPrograms(responseString);
            if (recommendPlayingItemNodes != null && recommendPlayingItemNodes.size() > 0) {
                result.setSuccess(true);
                result.setData(recommendPlayingItemNodes);
            }
            return result;
        } else if (type.equalsIgnoreCase(RequestType.GET_ADVERTISEMENT_ADDRESS)) {
            String url = parseAd(responseString);
            if (url != null) {
                result.setSuccess(true);
                result.setData(url);
            } else {
                result.setSuccess(false);
            }
        } else if (type.equalsIgnoreCase(RequestType.GET_LIVE_PROGRAM_SCHEDULE)) {
            ProgramScheduleList programScheduleList = parseLiveProgramSchedule(responseString);
            if (programScheduleList != null) {
                result.setSuccess(true);
                result.setData(programScheduleList);

            }
            return result;
        }
        return result;

    }


    private String parseAd(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONObject dataObj = obj.getJSONObject("data");
            String url = dataObj.getString("image");
            String id = dataObj.getString("id");
            if (TextUtils.isEmpty(id)) {
                url = PrefKit.getString(PlayApplication.from(), Constants.PREF_AD_ADDRESS, Constants.PREF_AD_ADDRESS_DEFAULT);
            }
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return PrefKit.getString(PlayApplication.from(), Constants.PREF_AD_ADDRESS, Constants.PREF_AD_ADDRESS_DEFAULT);

        }
    }

    private List<RecommendPlayingItemNode> parseRecommendPlayingPrograms(String json) {
        if (!(json == null || json.equalsIgnoreCase(""))) {
            try {
                JSONObject dataObj = new JSONObject(json);
                JSONArray dataArray = dataObj.getJSONArray("data");
                List<RecommendPlayingItemNode> lstNodes = new ArrayList();
                for (int i = 0; i < dataArray.length(); i++) {
                    RecommendPlayingItemNode node = _parseRecommendPlayingProgram(dataArray.getJSONObject(i));
                    if (node != null) {
                        lstNodes.add(node);
                    }
                }
                return lstNodes;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private RecommendPlayingItemNode _parseRecommendPlayingProgram(JSONObject dataObj) {
        if (dataObj != null) {
            try {
                JSONObject detail = dataObj.getJSONObject("detail");
                if (detail == null) {
                    return null;
                }
                RecommendPlayingItemNode node = new RecommendPlayingItemNode();
                node.channelName = dataObj.getString("sub_title");
                node.programName = dataObj.getString("title");
                node.thumb = dataObj.getString("thumb");
                if (dataObj.has("channel_star")) {
                    node.ratingStar = dataObj.getInt("channel_star");
                }
                if (detail.has("channel_star")) {
                    node.ratingStar = detail.getInt("channel_star");
                }
                JSONObject media = detail.getJSONObject("mediainfo");
                if (media != null) {
                    node.resId = media.getInt("id");
                }
                node.channelId = detail.getInt("channel_id");
                node.programid = detail.getInt("id");
                node.startTime = detail.getString("start_time");
                if (node.startTime != null && node.startTime.equalsIgnoreCase("00:00:00")) {
                    node.startTime = "00:00:01";
                }
                node.endTime = detail.getString("end_time");
                if (node.endTime == null || !node.endTime.equalsIgnoreCase("00:00:00")) {
                    return node;
                }
                node.endTime = "23:59:59";
                return node;
            } catch (Exception e) {
            }
        }
        return null;
    }

    private MediaCenter parseMediaCenter(String json) {
        if (!(json == null || json.equalsIgnoreCase(""))) {
            try {
                JSONObject dataObj1 = new JSONObject(json);
                JSONObject dataObj = dataObj1.getJSONObject("data");
                JSONObject radioHls = dataObj.getJSONObject("radiostations_hls");
                JSONObject radioDownload = dataObj.getJSONObject("radiostations_download");
                JSONObject storeAudio = dataObj.getJSONObject("storedaudio_m4a");
                MediaCenter mediaCenter = new MediaCenter();
                mediaCenter.mapMediaCenters = new HashMap();
                List<PingInfoV6> lstHls = _parseMediaCenter(radioHls, 0);
                if (lstHls != null && lstHls.size() > 0) {
                    mediaCenter.mapMediaCenters.put(MediaCenter.LIVE_CHANNEL_PLAY, lstHls);
                }
                List<PingInfoV6> lstDownload = _parseMediaCenter(radioDownload, 0);
                if (lstDownload != null && lstDownload.size() > 0) {
                    mediaCenter.mapMediaCenters.put(MediaCenter.LIVE_CHANNEL_DOWNLOAD, lstDownload);
                }
                List<PingInfoV6> lstStorage = _parseMediaCenter(storeAudio, 1);
                if (lstStorage != null && lstStorage.size() > 0) {
                    mediaCenter.mapMediaCenters.put(MediaCenter.VIRUTAL_CHANNEL, lstStorage);
                }
                return mediaCenter;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<PingInfoV6> _parseMediaCenter(JSONObject dataObj, int type) {
        if (dataObj != null) {
            try {
                JSONArray mediaCenters = dataObj.getJSONArray("mediacenters");
                double pcc = 0.0;
                try {
                    pcc = dataObj.getDouble("preference_change_cost");
                } catch (Exception e) {

                }
                if (mediaCenters != null) {
                    List<PingInfoV6> lstPingInfo = new ArrayList();
                    for (int i = 0; i < mediaCenters.length(); i++) {
                        JSONObject mediaObj = mediaCenters.getJSONObject(i);
                        PingInfoV6 info = new PingInfoV6();
                        info.domain = mediaObj.getString("domain");
                        info.backupIP = mediaObj.getString("backup_ips");
                        info.weight = mediaObj.getInt("weight");
                        info.testpath = mediaObj.getString("test_path");
                        info.accessExp = mediaObj.getString("access");
                        info.replayExp = mediaObj.getString("replay");
                        info.res = mediaObj.getString("result");
                        info.codename = mediaObj.getString("codename");
                        info.channelType = type;
                        info.pcc = pcc;
                        String net = mediaObj.getString("type");
                        if (net == null || !net.equalsIgnoreCase("cdn")) {
                            info.isCDN = false;
                        } else {
                            info.isCDN = true;
                        }
                        lstPingInfo.add(info);
                    }
                    return lstPingInfo;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private RecommendCategoryNode parseRecommendInfo(String responseString) {
        if (!(responseString == null || responseString.equalsIgnoreCase(""))) {
            try {
                JSONObject dataObj = new JSONObject(responseString);
                JSONArray dataArray = dataObj.getJSONArray("data");
                if (dataArray != null) {
                    RecommendCategoryNode categoryNode = new RecommendCategoryNode();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject itemObj = dataArray.getJSONObject(i);
                        if (itemObj != null) {
                            String name = itemObj.getString("name");
                            String briefName = itemObj.getString("brief_name");
                            if (name != null) {
                                RecommendItemNode itemNode;
                                if (name.equalsIgnoreCase("banner")) {
                                    JSONArray recommendArray = itemObj.getJSONArray("recommends");
                                    if (recommendArray != null) {
                                        for (int j = 0; j < recommendArray.length(); j++) {
                                            itemNode = parseRecommendItemInfo(recommendArray.getJSONObject(j), true);
                                            if (itemNode != null) {
                                                categoryNode.insertItemNode(itemNode, 0);
                                            }
                                        }
                                    }
                                } else {
                                    int sectionId = itemObj.getInt("section_id");
                                    JSONObject redirect = itemObj.getJSONObject("redirect");
                                    if (redirect != null) {
                                        String redirectType = redirect.getString("redirect_type");
                                        if (redirectType != null && redirectType.equalsIgnoreCase("section")) {
                                            sectionId = redirect.getInt("section_id");
                                        }
                                    }
                                    JSONArray recommendArray = itemObj.getJSONArray("recommends");
                                    if (recommendArray != null) {
                                        for (int j = 0; j < recommendArray.length(); j++) {
                                            itemNode = parseRecommendItemInfo(recommendArray.getJSONObject(j), false);
                                            if (itemNode != null) {
                                                itemNode.sectionId = sectionId;
                                                itemNode.belongName = name;
                                                itemNode.briefName = briefName;
                                                categoryNode.insertItemNode(itemNode, 1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return categoryNode;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
        return null;
    }

    private RecommendItemNode parseRecommendItemInfo(JSONObject obj, boolean banner) {
        if (obj != null) {
            try {
                RecommendItemNode node = new RecommendItemNode();
                boolean hasStar = false;
                node.name = obj.getString("title");
                if (banner) {
                    node.setSmallThumb(obj.getString("thumb"));
                } else {
                    JSONObject thumbObj = obj.getJSONObject("thumbs");
                    if (thumbObj != null) {
                        node.setSmallThumb(thumbObj.getString("200_thumb"));
                        node.setMediumThumb(thumbObj.getString("400_thumb"));
                        node.setLargeThumb(thumbObj.getString("800_thumb"));
                    }
                    if (node.noThumb()) {
                        node.setSmallThumb(obj.getString("thumb"));
                    }
                }
                node.update_time = obj.getString("update_time");
                JSONObject detail = obj.getJSONObject("detail");
               /* if (true) {
                    return node;
                }*/
                if (detail == null) {
                    return node;
                }
                if (detail.has("channel_star")) {
                    node.ratingStar = detail.getInt("channel_star");
                    hasStar = true;
                } else if (detail.has("star")) {
                    node.ratingStar = detail.getInt("star");
                    hasStar = true;
                }
                int channelid = 0;
                int catid = 0;
                String channelname = null;
                JSONObject pInfo;
                try {
                    pInfo = obj.getJSONObject("parent_info");
                } catch (Exception e) {
                    pInfo = null;
                }
                if (pInfo != null) {
                    String ptype = pInfo.getString("parent_type");
                    if (ptype != null) {
                        if (ptype.equalsIgnoreCase("channel") || ptype.equalsIgnoreCase("channel_ondemand")) {
                            channelid = pInfo.getInt("parent_id");
                            channelname = pInfo.getString("parent_name");
                        } else if (ptype.equalsIgnoreCase("category")) {
                            catid = pInfo.getInt("parent_id");
                        }
                    }
                    JSONObject parentExtra = pInfo.getJSONObject("parent_extra");
                    if (parentExtra != null) {
                        catid = parentExtra.getInt("category_id");
                        node.mCategoryId = catid;
                    }
                }
                String type = detail.getString("type");
                ProgramNode pNode;
                ChannelNode cNode;
                if (type.equalsIgnoreCase("program_ondemand")) {
                    pNode = _parseVirtualProgramNode(detail, 0);
                    pNode.channelId = channelid;
                    if (channelname != null) {
                        pNode.setChannelName(channelname);
                    }
                    if (hasStar) {
                        pNode.channelRatingStar = node.ratingStar;
                    }
                    node.setDetail(pNode);
                    return node;
                } else if (type.equalsIgnoreCase("channel_ondemand")) {
                    cNode = _parseChannelNode(detail);
                    cNode.categoryId = catid;
                    if (hasStar) {
                        cNode.ratingStar = node.ratingStar;
                    }
                    node.setDetail(cNode);
                    return node;
                } else if (type.equalsIgnoreCase("channel_live")) {
                    cNode = _parseChannelNode(detail);
                    cNode.categoryId = catid;
                    if (hasStar) {
                        cNode.ratingStar = node.ratingStar;
                    }
                    node.setDetail(cNode);
                    return node;
                } else if (type.equalsIgnoreCase("topic")) {
                    SpecialTopicNode sNode = _parseSpecialTopicNode(detail);
                    if (sNode != null) {
                        node.mCategoryId = sNode.categoryId;
                        if (hasStar) {
                            sNode.channelStar = node.ratingStar;
                        }
                        node.setDetail(sNode);
                    }
                    if (!hasStar) {
                        return node;
                    }
                    sNode.channelStar = node.ratingStar;
                    return node;
                } else if (type.equalsIgnoreCase("activity")) {
                    node.setDetail(_parseActivity(detail));
                    return node;
                } else if (!type.equalsIgnoreCase("program_temp")) {
                    return null;
                } else {
                    pNode = _parseProgramTempNode(detail);
                    if (pNode != null) {
                        pNode.mLiveInVirtual = true;
                        pNode.channelId = channelid;
                        if (channelname != null) {
                            pNode.setChannelName(channelname);
                        }
                        node.setDetail(pNode);
                    }
                    if (!hasStar) {
                        return node;
                    }
                    pNode.channelRatingStar = node.ratingStar;
                    return node;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;

    }

    private ProgramNode _parseVirtualProgramNode(JSONObject obj, int dayofweek) throws Exception {
        if (obj == null) {
            return null;
        }
        String type = obj.getString("type");
        if (type == null || type.equalsIgnoreCase("program_ondemand")) {
            ProgramNode node = new ProgramNode();
            node.id = obj.getInt("id");
            node.uniqueId = node.id;
            node.channelType = 1;
            node.title = obj.getString("title");
            node.duration = obj.getDouble("duration");
            node.sequence = obj.getInt("sequence");
            try {
                node.updateTime = obj.getString("update_time");
            } catch (Exception e) {
                e.printStackTrace();
            }
            node.sequence = obj.getInt("sequence");
            node.groupId = obj.getInt("chatgroup_id");
            JSONObject media = obj.getJSONObject("mediainfo");
            if (media == null) {
                return node;
            }
            node.resId = media.getInt("id");
            JSONArray bitArray = media.getJSONArray("bitrates_url");
            if (bitArray == null) {
                return node;
            }
            node.lstAudioPath = new ArrayList();
            node.lstBitrate = new ArrayList();
            for (int i = 0; i < bitArray.length(); i++) {
                JSONObject bitobj = bitArray.getJSONObject(i);
                if (bitobj != null) {
                    String path = bitobj.getString("file_path");
                    if (path != null) {
                        node.lstAudioPath.add(path);
                    }
                    Integer bitrate = Integer.valueOf(bitobj.getInt("bitrate"));
                    if (bitrate != null) {
                        node.lstBitrate.add(bitrate);
                    }
                }
            }
            return node;
        } else if (type.equalsIgnoreCase("program_temp")) {
            return _parseProgramTempNode(obj);
        } else {
            return null;
        }
    }

    private ProgramNode _parseProgramTempNode(JSONObject obj) {
        if (obj != null) {
            try {
                ProgramNode node = new ProgramNode();
                node.mLiveInVirtual = true;
                node.dayOfWeek = 0;
                node.id = obj.getInt("id");
                node.uniqueId = node.id;
                node.channelType = 0;
                node.title = obj.getString("title");
                node.duration = obj.getDouble("duration");
                node.sequence = obj.getInt("sequence");
                node.startTime = obj.getString("start_time");
                long time = TimeKit.dateToMS(node.startTime);
                node.startTime = TimeKit.msToDate3(time);
                node.setAbsoluteStartTime(time / 1000);
                node.endTime = obj.getString("end_time");
                time = TimeKit.dateToMS(node.endTime);
                node.endTime = TimeKit.msToDate3(time);
                node.setAbsoluteEndTime(time / 1000);
                node.sequence = obj.getInt("sequence");
                node.groupId = obj.getInt("chatgroup_id");
                JSONObject media = obj.getJSONObject("mediainfo");
                if (media == null) {
                    return node;
                }
                node.resId = media.getInt("id");
                return node;
            } catch (Exception e) {
            }
        }
        return null;
    }

    private ChannelNode _parseChannelNode(JSONObject json) {
        if (json != null) {
            try {
                ChannelNode node = new ChannelNode();
                if (json.has("channel_star")) {
                    node.ratingStar = json.getInt("channel_star");
                } else if (json.has("star")) {
                    node.ratingStar = json.getInt("star");
                }
                node.channelId = json.getInt("id");
                node.title = json.getString("title");
                node.desc = json.getString("description");
                node.groupId = json.getInt("chatgroup_id");
                node.categoryId = json.getInt("category_id");
                node.update_time = json.getString("update_time");
                JSONObject thumbObj = json.getJSONObject("thumbs");
                if (thumbObj != null) {
                    node.setSmallThumb(thumbObj.getString("200_thumb"));
                    node.setMediumThumb(thumbObj.getString("400_thumb"));
                    node.setLargeThumb(thumbObj.getString("800_thumb"));
                    if (node.noThumb()) {
                        node.setSmallThumb(thumbObj.getString("small_thumb"));
                        node.setMediumThumb(thumbObj.getString("medium_thumb"));
                        node.setLargeThumb(thumbObj.getString("large_thumb"));
                    }
                }
                String type = json.getString("type");
                if (type == null || !type.equalsIgnoreCase("channel_ondemand")) {
                    node.channelType = 0;
                } else {
                    node.channelType = 1;
                }
                node.autoPlay = false;
                if (json.has("record_enabled") && json.getInt("record_enabled") == 0) {
                    node.recordEnable = false;
                } else {
                    node.recordEnable = true;
                }
                if (node.isLiveChannel()) {
                    node.audienceCnt = json.getInt("audience_count");
                    JSONObject media = json.getJSONObject("mediainfo");
                    if (media != null) {
                        node.resId = media.getInt("id");
                    }
                } else {
                    node.latest_program = json.getString("latest_program");
                }
                JSONObject detailObject;
                if (json.has("detail")) {
                    detailObject = json.getJSONObject("detail");
                    if (detailObject == null) {
                        return node;
                    }
                } else {
                    return node;
                }

                int i;
                node.programCnt = detailObject.getInt("program_count");
               /* BroadcasterNode broadcasterNode;

                JSONArray authors = detailObject.getJSONArray("authors");
                if (authors != null) {
                    for (i = 0; i < authors.length(); i++) {
                        broadcasterNode = parseBroadcasterNode(authors.getJSONObject(i));
                        if (node.lstAuthors == null) {
                            node.lstAuthors = new ArrayList();
                        }
                        node.lstAuthors.add(broadcasterNode);
                    }
                }
                JSONArray broadcastersObject = detailObject.getJSONArray("broadcasters");
                if (broadcastersObject != null) {
                    for (i = 0; i < broadcastersObject.length(); i++) {
                        broadcasterNode = parseBroadcasterNode(broadcastersObject.getJSONObject(i));
                        if (node.lstBroadcaster == null) {
                            node.lstBroadcaster = new ArrayList();
                        }
                        node.lstBroadcaster.add(broadcasterNode);
                    }
                }
                JSONArray podcastersObject = detailObject.getJSONArray("podcasters");
                if (podcastersObject == null) {
                    return node;
                }*/
                JSONArray podcastersObject;
                try {
                    podcastersObject = detailObject.getJSONArray("podcasters");
                } catch (Exception e) {
                    podcastersObject = null;
                }
                if (podcastersObject == null) {
                    return node;
                }
                for (i = 0; i < podcastersObject.length(); i++) {
                    UserInfo user = _parsePodcaster(podcastersObject.getJSONObject(i));
                    if (node.lstPodcasters == null) {
                        node.lstPodcasters = new ArrayList();
                    }
                    if (user != null) {
                        node.lstPodcasters.add(user);
                    }
                }
                return node;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private SpecialTopicNode _parseSpecialTopicNode(JSONObject detail) {
        if (detail != null) {
            try {
                SpecialTopicNode node = new SpecialTopicNode();
                node.id = detail.getInt("id") + 1000001;
                node.title = detail.getString("title");
                node.thumb = detail.getString("thumb");
                node.desc = detail.getString("description");
                node.categoryId = detail.getInt("category_id");
                node.create_time = detail.getString("create_time");
                node.update_time = detail.getString("update_time");
                return node;
            } catch (Exception e) {
            }
        }
        return null;
    }

    private ActivityNode _parseActivity(JSONObject obj) {
        if (obj == null) {
            return null;
        }
        try {
            ActivityNode aNode = new ActivityNode();
            aNode.id = obj.getInt("id");
            aNode.name = obj.getString("title");
            aNode.type = obj.getString("type");
            aNode.contentUrl = obj.getString("url");
            aNode.infoUrl = null;
            aNode.infoTitle = obj.getString("description");
            return aNode;
        } catch (Exception e) {
            return null;
        }
    }

    private ChannelNode parseChannelNode(String json) {
        if (!(json == null || json.equalsIgnoreCase(""))) {
            try {
                JSONObject obj = new JSONObject(json);
                if (obj != null) {
                    ChannelNode node = _parseChannelNode(obj.getJSONObject("data"));
                    if (node != null) {
                        return node;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private UserInfo _parsePodcaster(JSONObject data) throws Exception {
        if (data == null) {
            return null;
        }
        UserInfo user = new UserInfo();
        user.userId = data.getString("user_system_id");
        user.userKey = user.userId;
        user.isBlocked = false;
        user.isPodcaster = true;
        user.podcasterId = data.getInt("id");
        user.podcasterName = data.getString("nickname");
        user.fansNumber = data.getLong("fan_num");
        user.snsInfo.signature = data.getString("signature");
        user.snsInfo.sns_id = data.getString("weibo_id");
        user.snsInfo.sns_name = data.getString("weibo_name");
        if (user.snsInfo.sns_name == null) {
            user.snsInfo.sns_name = "\u873b\u8713\u4e3b\u64ad";
        }
        user.snsInfo.sns_avatar = data.getString("avatar");
        user.snsInfo.desc = data.getString("description");
        int sex = data.getInt("sex");
        if (sex == 0) {
            user.snsInfo.sns_gender = "n";
            return user;
        } else if (sex == 1) {
            user.snsInfo.sns_gender = "m";
            return user;
        } else if (sex != 2) {
            return user;
        } else {
            user.snsInfo.sns_gender = "f";
            return user;
        }
    }

    private ProgramScheduleList parseVirtualProgramSchedule(String json) {
        if (!(json == null || json.equalsIgnoreCase(""))) {
            try {
                // JSONArray dataArray = ((JSONObject) JSON.parse(json)).getJSONArray(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA);
                JSONObject dataObj = new JSONObject(json);
                JSONArray dataArray = dataObj.getJSONArray("data");
                if (dataArray != null) {
                    ProgramScheduleList pslist = new ProgramScheduleList(1);
                    ProgramSchedule programSchedule = new ProgramSchedule();
                    programSchedule.dayOfWeek = 0;
                    programSchedule.mLstProgramNodes = new ArrayList();
                    Node prev = null;
                    for (int i = 0; i < dataArray.length(); i++) {
                        ProgramNode program = _parseVirtualProgramNode(dataArray.getJSONObject(i), 0);
                        if (program != null) {
                            if (program.sequence == 0) {
                                program.sequence = i;
                            }
                            if (prev != null) {
                                prev.nextSibling = program;
                                program.prevSibling = prev;
                            }
                            programSchedule.mLstProgramNodes.add(program);
                            prev = program;
                        }
                    }
                    pslist.mLstProgramsScheduleNodes.add(programSchedule);
                    return pslist;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private ProgramScheduleList parseLiveProgramSchedule(String json) {
        if (!(json == null || json.equalsIgnoreCase(""))) {
            try {
                JSONObject obj = new JSONObject(json);
                JSONObject dataObj = obj.getJSONObject("data");
                ProgramScheduleList pslist = new ProgramScheduleList(0);
                Node prev = null;
                for (int i = 1; i <= 7; i++) {
                    if (!dataObj.has(String.valueOf(i))) continue;
                    JSONArray dataArray = dataObj.getJSONArray(String.valueOf(i));
                    if (dataArray != null) {
                        int dayofweek = i;
                        if (dataArray.length() > 0) {
                            ProgramSchedule programSchedule = new ProgramSchedule();
                            programSchedule.dayOfWeek = dayofweek;
                            programSchedule.mLstProgramNodes = new ArrayList();
                            for (int j = 0; j < dataArray.length(); j++) {
                                ProgramNode program = _parseLiveProgramNode(dataArray.getJSONObject(j), dayofweek);
                                if (program != null) {
                                    if (prev != null) {
                                        prev.nextSibling = program;
                                        program.prevSibling = prev;
                                    }
                                    programSchedule.mLstProgramNodes.add(program);
                                    prev = program;
                                }
                            }
                            pslist.mLstProgramsScheduleNodes.add(programSchedule);
                        }
                    }
                }
                if (pslist.mLstProgramsScheduleNodes.size() > 0) {
                    return pslist;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private ProgramNode _parseLiveProgramNode(JSONObject obj, int dayofweek) throws Exception {
        if (obj == null) {
            return null;
        }
        ProgramNode node = new ProgramNode();
        node.id = obj.getInt("id");
        node.startTime = obj.getString("start_time");
        node.endTime = obj.getString("end_time");
        if (node.endTime != null && node.endTime.equalsIgnoreCase("00:00:00")) {
            node.endTime = "23:59:00";
        }
        node.title = obj.getString("title");
        node.channelId = obj.getInt("channel_id");
        node.uniqueId = obj.getInt("program_id");
        node.groupId = obj.getInt("chatgroup_id");
        node.dayOfWeek = dayofweek;
        node.channelType = 0;
        JSONObject media = obj.getJSONObject("mediainfo");
        if (media != null) {
            node.resId = media.getInt("id");
        }
       /* ProgramABTestBean bean = InfoManager.getInstance().getProgramABTest(node.channelId, node.uniqueId);
        if (bean != null) {
            node.resId = bean.resId;
            node.title = bean.title;
        }*/
        JSONObject detail = obj.getJSONObject("detail");
        if (detail == null) {
            return node;
        }
        JSONArray bArray = detail.getJSONArray("broadcasters");
        if (bArray == null) {
            return node;
        }
        node.lstBroadcaster = new ArrayList();
        for (int i = 0; i < bArray.length(); i++) {
            BroadcasterNode bNode = new BroadcasterNode();
            JSONObject bObj = bArray.getJSONObject(i);
            bNode.id = bObj.getInt("id");
            bNode.nick = bObj.getString("username");
            bNode.avatar = bObj.getString("thumb");
            bNode.weiboId = bObj.getString("weibo_id");
            bNode.weiboName = bObj.getString("weibo_name");
            bNode.qqId = bObj.getString("qq_id");
            bNode.qqName = bObj.getString("qq_name");
            node.lstBroadcaster.add(bNode);
        }
        return node;
    }


    public SpecialTopicNode parseSpecialTopicChannels(String json) {
        if (!(json == null || json.equalsIgnoreCase(""))) {
            try {
                JSONObject obj = new JSONObject(json);
                JSONObject dataObj = obj.getJSONObject("data");
                if (dataObj != null) {
                    SpecialTopicNode stNode = _parseSpecialTopicNode(dataObj);
                    JSONArray channelsArray = dataObj.getJSONArray("channels");
                    if (channelsArray != null) {
                        List<ChannelNode> lstNodes = new ArrayList();
                        for (int i = 0; i < channelsArray.length(); i++) {
                            ChannelNode node = _parseChannelNode(channelsArray.getJSONObject(i));
                            if (node != null) {
                                lstNodes.add(node);
                            }
                        }
                        if (stNode != null) {
                            stNode.setChannels(lstNodes);
                            return stNode;
                        }
                    }
                    if (stNode != null) {
                        return stNode;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}

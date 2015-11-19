package com.andy.LuFM.providers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andy.LuFM.Utils.TimeKit;
import com.andy.LuFM.Utils.ViewFactory;
import com.andy.LuFM.adapter.SectionAdapter;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.IDataRecvHandler;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.handler.GsonHttpHandler;
import com.andy.LuFM.model.ActivityNode;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.RecommendDataModel;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.SectionItem;
import com.andy.LuFM.model.SpecialTopicNode;
import com.andy.LuFM.view.RecommendItemView;
import com.andy.LuFM.view.RecommendTagView;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class RecommendListProvider extends ListDataProvider implements IDataRecvHandler {
    int sectionId = 0;
    private ViewFactory mFactory = null;


    public RecommendListProvider(Context context, ViewFactory factory) {
        super(context);
        this.mFactory = factory;
    }

    @Override
    public BaseAdapter newAdapter() {
        return new SectionAdapter(new ArrayList<SectionItem>(), mFactory);
    }

    private GsonHttpHandler gsonHttpHandler = new GsonHttpHandler(null) {
        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        }

        @Override
        protected void onError(int statusCode, Header[] headers, String responseString, Throwable cause) {


        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString, Object object) {
            RecommendCategoryNode categoryNode = parseRecommendInfo(responseString);
            if (categoryNode != null) {
                InfoManager.getInstance().root().mapRecommendCategoryNode.put(sectionId, categoryNode);
                callback.onLoadSuccess(categoryNode);
                callback.onLoadFinish(0);
            }

        }
    };


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
                                            Log.i("Sync", "banner:" + (itemNode == null));
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
                JSONObject pInfo = obj.getJSONObject("parent_info");
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

    @Override
    public void loadData(Object... aArray) {
        super.loadData(aArray);
        for (Object obj : aArray) {
            sectionId = (int) obj;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("sectionId", sectionId);
        DataManager.getInstance().getData(RequestType.NET_REQUEST, gsonHttpHandler, new DataCommand(RequestType.DATA_TYPE_GET_RECOMMEND, param));

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
           // node.updateTime = obj.getString("update_time");
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
                if (json.getInt("auto_play") == 0) {
                    node.autoPlay = false;
                } else {
                    node.autoPlay = true;
                }
                if (json.getInt("record_enabled") == 0) {
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
                JSONObject detailObject = json.getJSONObject("detail");
                if (detailObject == null) {
                    return node;
                }
                int i;
               /* BroadcasterNode broadcasterNode;
                node.programCnt = detailObject.getInt("program_count");
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
             /*   for (i = 0; i < podcastersObject.length(); i++) {
                    UserInfo user = _parsePodcaster(podcastersObject.getJSONObject(i));
                    if (node.lstPodcasters == null) {
                        node.lstPodcasters = new ArrayList();
                    }
                    if (user != null) {
                        node.lstPodcasters.add(user);
                    }
                }*/
                return node;
            } catch (Exception e) {
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

}

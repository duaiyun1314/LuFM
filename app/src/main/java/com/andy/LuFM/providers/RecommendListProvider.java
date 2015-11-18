package com.andy.LuFM.providers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.andy.LuFM.Utils.ViewFactory;
import com.andy.LuFM.adapter.SectionAdapter;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.IDataRecvHandler;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.handler.GsonHttpHandler;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.RecommendDataModel;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.SectionItem;
import com.andy.LuFM.view.RecommendItemView;
import com.andy.LuFM.view.RecommendTagView;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class RecommendListProvider extends ListDataProvider implements IDataRecvHandler {
    private ViewFactory mFactory = new ViewFactory() {
        @Override
        public View createView(int sectionType) {
            View view = null;
            switch (sectionType) {
                case 2://tag
                    return new RecommendTagView(mContext);
                case 3://item
                    return new RecommendItemView(mContext);
            }
            return view;
        }
    };

    public RecommendListProvider(Context context) {
        super(context);
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
                List<SectionItem> sectionItemList = parseSection(categoryNode);
                ((SectionAdapter) getAdapter()).setData(sectionItemList);
                Log.i("Sync", "加载完成：" + categoryNode.lstBanner.size());
                callback.onLoadSuccess(categoryNode.lstBanner);
                callback.onLoadFinish(0);
            }

        }
    };

    private List<SectionItem> parseSection(RecommendCategoryNode categoryNode) {
        List<List<RecommendItemNode>> defaultRecList = categoryNode.lstRecMain;
        if (defaultRecList != null) {
            List<List<RecommendItemNode>> recList = defaultRecList;
            List<SectionItem> list = new ArrayList();
            for (int i = 0; i < recList.size(); i++) {
                List<RecommendItemNode> subList = (List) recList.get(i);
                if (subList.size() > 0) {
                    //list.add(new SectionItem(0, null));
                    list.add(new SectionItem(2, subList.get(0)));
                    list.add(new SectionItem(3, subList));
                }
            }
            // list.add(new SectionItem(0, null));

            return list;
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
                return node;
              /*  JSONObject detail = obj.getJSONObject("detail");
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
                } else if (type.equalsIgnoreCase(PushMessage.TOPIC)) {
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
                } else if (type.equalsIgnoreCase(DBManager.ACTIVITY)) {
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
                }*/
            } catch (Exception e) {
                return null;
            }
        }
        return null;

    }

    @Override
    public void loadData() {
        super.loadData();
        DataManager.getInstance().getData(RequestType.NET_REQUEST, gsonHttpHandler, new DataCommand(RequestType.DATA_TYPE_GET_RECOMMEND, null));

    }


}

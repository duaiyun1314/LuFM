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
import com.andy.LuFM.data.IResultRecvHandler;
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
public class RecommendListProvider extends ListDataProvider implements IResultRecvHandler {
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


    @Override
    public void loadData(Object... aArray) {
        super.loadData(aArray);
        for (Object obj : aArray) {
            sectionId = (int) obj;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("sectionId", sectionId);
        DataManager.getInstance().getData(RequestType.NET_REQUEST, this, new DataCommand(RequestType.DATA_TYPE_GET_RECOMMEND, param));

    }


    @Override
    public void onRecvResult(Result result, String type) {
        if (result.isSuccess()) {
            RecommendCategoryNode recommendCategoryNode = (RecommendCategoryNode) result.getData();
            InfoManager.getInstance().root().mapRecommendCategoryNode.put(sectionId, recommendCategoryNode);
            callback.onLoadSuccess(recommendCategoryNode);
            callback.onLoadFinish(0);
        }

    }
}

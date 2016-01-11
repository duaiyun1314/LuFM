package com.andy.LuFM.providers;

import android.content.Context;
import android.widget.BaseAdapter;
import com.andy.LuFM.Utils.ViewFactory;
import com.andy.LuFM.adapter.SectionAdapter;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.DataManager;
import com.andy.LuFM.data.IResultRecvHandler;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.SectionItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
    public void onRecvResult(Result result, String type, Object param) {
        if (result.isSuccess()) {
            RecommendCategoryNode recommendCategoryNode = (RecommendCategoryNode) result.getData();
            InfoManager.getInstance().root().mapRecommendCategoryNode.put(sectionId, recommendCategoryNode);
            callback.onLoadSuccess(recommendCategoryNode);
            callback.onLoadFinish(0);
        }

    }


}

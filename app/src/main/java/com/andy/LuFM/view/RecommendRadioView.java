package com.andy.LuFM.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.andy.LuFM.Utils.ViewFactory;
import com.andy.LuFM.adapter.SectionAdapter;
import com.andy.LuFM.controller.RecommendListController;
import com.andy.LuFM.controller.RecommendRadioController;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.RecommendPlayingItemNode;
import com.andy.LuFM.model.SectionItem;
import com.andy.LuFM.providers.ProgramNodesProvider;
import com.andy.LuFM.providers.RecommendListProvider;
import com.andy.LuFM.providers.RecommendRadioProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglu on 15/11/19.
 */
public class RecommendRadioView extends BaseRecommendView<RecommendRadioController, RecommendRadioProvider> implements AdapterView.OnItemClickListener {

    private ListView mListView;

    public RecommendRadioView(Context context) {
        super(context);
    }

    @Override
    protected RecommendRadioController createController() {
        return new RecommendRadioController(createProvider());
    }

    @Override
    protected RecommendRadioProvider createProvider() {
        return new RecommendRadioProvider(mContext);
    }


    @Override
    public void update(int sectionId) {
        controller.loadData(sectionId);
        this.mListView = controller.mListView;
        this.mListView.setOnItemClickListener(this);
        this.mListView.setDivider(new ColorDrawable(Color.parseColor("#DDDDDD")));
        this.mListView.setDividerHeight(1);
        controller.mHeadView.update(InfoManager.getInstance().root().mLiveNode);

    }

    @Override
    public void setDate(Object object) {
        List<RecommendPlayingItemNode> lstNodes = (List<RecommendPlayingItemNode>) object;
        ((RecommendRadioProvider.MYAdapter) controller.getAdatper()).setData(lstNodes);
    }

    @Override
    public int getSection() {
        return 9999;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

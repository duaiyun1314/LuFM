package com.andy.LuFM.view;

import android.content.Context;
import android.view.View;

import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.Utils.ViewFactory;
import com.andy.LuFM.adapter.SectionAdapter;
import com.andy.LuFM.controller.RecommendListController;
import com.andy.LuFM.model.RecommendCategoryNode;
import com.andy.LuFM.model.RecommendItemNode;
import com.andy.LuFM.model.SectionItem;
import com.andy.LuFM.providers.RecommendListProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglu on 15/11/19.
 */
public class RecommendNovelView extends BaseRecommendView<RecommendListController, RecommendListProvider> {
    private RecommendCategoryNode recommendCategoryNode;

    public RecommendNovelView(Context context) {
        super(context);
    }

    @Override
    protected RecommendListController createController() {
        return new RecommendListController(createProvider());
    }

    @Override
    protected RecommendListProvider createProvider() {
        return new RecommendListProvider(mContext, createViewFactory());
    }

    @Override
    protected ViewFactory createViewFactory() {
        return new ViewFactory() {
            @Override
            public View createView(int sectionType) {
                View view = null;
                switch (sectionType) {
                    case 2://tag
                        return new RecommendTagView(mContext);
                    case 3://item
                        return new RecommendItemNovelView(mContext);
                }
                return view;
            }
        };
    }

    @Override
    public void update() {
        controller.loadData(Constants.NOVEL_SECTION);

    }

    @Override
    public void setDate(Object object) {

        this.recommendCategoryNode = (RecommendCategoryNode) object;
        List<SectionItem> sectionItemList = parseSection(recommendCategoryNode);
        controller.mHeadView.update(recommendCategoryNode.lstBanner);
        ((SectionAdapter) controller.getAdatper()).setData(sectionItemList);

    }

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
}

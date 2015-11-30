package com.andy.LuFM.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.andy.LuFM.Utils.ViewFactory;
import com.andy.LuFM.controller.BaseListController;
import com.andy.LuFM.providers.BaseProvider;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public abstract class BaseRecommendView<Controller extends BaseListController, Provider extends BaseProvider> extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {
    protected Controller controller;


    protected Activity mContext;

    public BaseRecommendView(Context context) {
        super(context);
        init(context);
    }

    public BaseRecommendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseRecommendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = (Activity) context;
        this.controller = createController();
        this.controller.setActivity(context);
        this.controller.assumeView(this);


    }


    protected abstract Controller createController();

    protected abstract Provider createProvider();

    protected abstract ViewFactory createViewFactory();

    /**
     * 此方法中view可能要加载数据了
     */
    public abstract void update(int sectionId);

    @Override
    public void onRefresh() {

    }

    /**
     * 设置view所需要的数据
     *
     * @param object
     */
    public abstract void setDate(Object object);

    public abstract int getSection();
}

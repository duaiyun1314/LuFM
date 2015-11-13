package com.andy.LuFM.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.andy.LuFM.controller.BaseListController;
import com.andy.LuFM.providers.BaseProvider;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public abstract class BaseViewGroup<Controller extends BaseListController, Provider extends BaseProvider> extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {
    protected Controller controller;


    protected Context mContext;

    public BaseViewGroup(Context context) {
        super(context);
        init(context);
    }

    public BaseViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.controller = createController();
        this.controller.setActivity(context);
        this.controller.assumeView(this);


    }


    protected abstract Controller createController();

    protected abstract Provider createProvider();
    public abstract void update();

    @Override
    public void onRefresh() {

    }
}

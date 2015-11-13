package com.andy.LuFM.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.andy.LuFM.controller.BaseController;
import com.andy.LuFM.providers.BaseProvider;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public abstract class BaseViewGroup<Controller extends BaseController, Provider extends BaseProvider> extends FrameLayout {
    protected Controller controller;
    protected Provider provider;

    public BaseViewGroup(Context context) {
        super(context);
        init();
    }

    public BaseViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.controller = createController();
    }

    abstract Controller createController();

    abstract Provider createProvider();

}

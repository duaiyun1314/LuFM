package com.andy.LuFM.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;

/**
 * Created by Andy.Wang on 2016/3/3.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销eventbus
        EventBus.getDefault().unregister(this);
    }
}

package com.andy.LuFM.controller.viewcontroller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import java.io.Serializable;

/**
 * Created by wanglu on 15/11/23.
 */
public class ChannelDetailController extends ViewController {
    private Context mContext;

    public ChannelDetailController(Context context) {
        this.mContext = context;
    }

    @Override
    public void config(String type, Object param) {
        super.config(type, param);
        if (type.equalsIgnoreCase("channeldetail")) {


        }
    }
}

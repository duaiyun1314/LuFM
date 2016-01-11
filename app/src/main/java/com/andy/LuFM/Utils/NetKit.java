package com.andy.LuFM.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.andy.LuFM.LuFmApplication;

/**
 * Created by wanglu on 15/11/16.
 */
public class NetKit {
    private static NetKit mInstance;

    public static boolean isNetworkConnected() {
        Context context = LuFmApplication.getInstance();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable()) {
            return true;
        }
        return false;
    }

    private NetKit() {

    }

    public static NetKit getInstance() {
        if (mInstance == null) {
            mInstance = new NetKit();
        }
        return mInstance;
    }


}

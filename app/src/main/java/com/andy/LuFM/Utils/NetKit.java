package com.andy.LuFM.Utils;

import android.preference.PreferenceActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.util.Map;

/**
 * Created by wanglu on 15/11/16.
 */
public class NetKit {
    private static NetKit mInstance;
    private AsyncHttpClient mClient;

    private NetKit() {
        mClient = new AsyncHttpClient();
        mClient.setConnectTimeout(3000);
        mClient.setResponseTimeout(6000);
        mClient.setMaxRetriesAndTimeout(3, 200);

    }

    public static NetKit getInstance() {
        if (mInstance == null) {
            mInstance = new NetKit();
        }
        return mInstance;
    }

    public void getRecommendInfo(Map<String, Object> param, ResponseHandlerInterface handlerInterface) {
        String url = Constants.RECOMMEND_INFO_URL + "" + (Integer) param.get("sectionId");
        mClient.get(url, handlerInterface);

    }

}

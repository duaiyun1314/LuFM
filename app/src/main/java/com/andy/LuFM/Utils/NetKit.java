package com.andy.LuFM.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.andy.LuFM.LuFmApplication;
import com.andy.LuFM.data.IDataRecvHandler;
import com.andy.LuFM.data.IResultRecvHandler;
import com.andy.LuFM.handler.GsonHttpHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wanglu on 15/11/16.
 */
public class NetKit {
    private static NetKit mInstance;
    private AsyncHttpClient mClient;
    private SyncHttpClient syncClient;
    private NetParse netParser;

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
        mClient = new AsyncHttpClient();
        syncClient = new SyncHttpClient();
        syncClient.setConnectTimeout(3000);
        syncClient.setResponseTimeout(6000);
        mClient.setConnectTimeout(3000);
        mClient.setResponseTimeout(6000);
        mClient.setMaxRetriesAndTimeout(3, 200);
        netParser = NetParse.getInstance();

    }

    public static NetKit getInstance() {
        if (mInstance == null) {
            mInstance = new NetKit();
        }
        return mInstance;
    }

   /* public void getRecommendInfo(String type, Map<String, Object> param, IDataRecvHandler handlerInterface) {
        String url = Constants.RECOMMEND_INFO_URL + "" + (Integer) param.get("sectionId");
        mClient.get(url, handlerInterface);

    }

    public void getLiveChannleInfo(String type, Map<String, Object> param, IDataRecvHandler handlerInterface) {
        String url = Constants.LIVE_CHANNLE_INFO_URL + "" + (Integer) param.get("id");
        mClient.get(url, handlerInterface);

    }*/

    public void getNormalNetInfo(final String url, final String requesttype, final IResultRecvHandler iResultRecvHandler, final Object param) {
        Log.i("Sync", "url:" + url);
        mClient.get(url, new GsonHttpHandler(null) {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            protected void onError(int statusCode, Header[] headers, String responseString, Throwable cause) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString, Object object) {
                if (netParser != null) {
                    netParser.parse(responseString, requesttype, iResultRecvHandler, param);
                }
            }
        });
    }

}

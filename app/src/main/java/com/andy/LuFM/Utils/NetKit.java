package com.andy.LuFM.Utils;

import android.preference.PreferenceActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by wanglu on 15/11/16.
 */
public class NetKit {
    private static NetKit mInstance;
    private AsyncHttpClient mClient;

    private NetKit() {
        mClient = new AsyncHttpClient();
        // mClient.setCookieStore(new BasicCookieStore());
        mClient.setConnectTimeout(3000);
        mClient.setResponseTimeout(6000);
        mClient.setMaxRetriesAndTimeout(3, 200);
        // mClient.setUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.45 Safari/537.36");

    }

    public static NetKit getInstance() {
        if (mInstance == null) {
            mInstance = new NetKit();
        }
        return mInstance;
    }

    /*  public void getNewslistByPage(int page, String type, ResponseHandlerInterface handlerInterface) {
          RequestParams params = new RequestParams();
          params.add("type", type);
          params.add("page", page + "");
          params.add("_", System.currentTimeMillis() + "");
          mClient.get(null, Configure.NEWS_LIST_URL, getAuthHeader(), params, handlerInterface);
      }*/
    public void getRecommendInfo(ResponseHandlerInterface handlerInterface) {
        mClient.get(Constants.RECOMMEND_INFO_URL, handlerInterface);

    }

   /* public static PreferenceActivity.Header[] getAuthHeader() {
        return new PreferenceActivity.Header[]{
                new BasicHeader("Referer", "http://www.cnbeta.com/"),
                new BasicHeader("Origin", "http://www.cnbeta.com"),
                new BasicHeader("X-Requested-With", "XMLHttpRequest")
        };
    }*/
}

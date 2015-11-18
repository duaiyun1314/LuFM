package com.andy.LuFM.handler;

import com.andy.LuFM.Utils.ToolKit;
import com.andy.LuFM.data.IDataRecvHandler;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by wanglu on 15/11/16.
 */
public abstract class GsonHttpHandler<T> extends TextHttpResponseHandler implements IDataRecvHandler {
    protected Type type;
    protected boolean parseString = false;

    public GsonHttpHandler(TypeToken<T> tTypeToken) {
        if (tTypeToken == null) {
            parseString = true;
        } else {
            type = tTypeToken.getType();
        }
    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {

        if (statusCode == 200) {
            if (parseString) {
                onSuccess(statusCode, headers, responseString, null);
                return;
            }
            try {
                T t = ToolKit.getGson().fromJson(responseString, type);
                if (t != null) {
                    onSuccess(statusCode, headers, responseString, t);
                } else {
                    onFailure(statusCode, headers, responseString, new RuntimeException("response emyty"));

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                onError(statusCode, headers, responseString, ex);
            }

        }
    }

    protected abstract void onError(int statusCode, Header[] headers, String responseString, Throwable cause);

    public abstract void onSuccess(int statusCode, Header[] headers, String responseString, T object);
}

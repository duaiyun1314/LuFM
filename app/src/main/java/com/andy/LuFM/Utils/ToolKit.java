package com.andy.LuFM.Utils;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.security.MessageDigest;

/**
 * Created by Andy.Wang on 2015/11/13.
 */
public class ToolKit {
    public static Gson gson;
    public static Handler uiHandler;

    public static void runInUIThread(Runnable runnable, int delay) {
        if (uiHandler == null) {
            uiHandler = new Handler(Looper.getMainLooper());
        }
        uiHandler.postDelayed(runnable, delay);

    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static String md5(String text) {
        String cypher = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (byte b : messageDigest) {
                hexString.append(Integer.toHexString(b & PlayStatus.DETAIL_MASK));
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return text;
        }
    }

}

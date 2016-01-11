package com.andy.LuFM.network;

/**
 * Created by Andy.Wang on 2016/1/8.
 */
public class NormalFactory {

    private static NormalGetAPI sInstance = null;
    private static final Object WATCH_DOG = new Object();

    private NormalFactory() {
    }

    public static NormalGetAPI getNbaplus() {
        synchronized (WATCH_DOG) {
            if (sInstance == null) {
                NormalClient nbaplusCilent = new NormalClient();
                sInstance = nbaplusCilent.getCilent();
            }
            return sInstance;
        }
    }

}

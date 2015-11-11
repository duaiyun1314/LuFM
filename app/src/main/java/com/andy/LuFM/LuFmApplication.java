package com.andy.LuFM;

import android.app.Application;

import com.andy.LuFM.dbutil.DaoMaster;

/**
 * Created by Andy.Wang on 2015/11/11.
 */
public class LuFmApplication extends Application {
    private static LuFmApplication mInstance;
    private DaoMaster.DevOpenHelper mHelper;

    public static LuFmApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initDb();
    }

    private void initDb() {
        mHelper = new DaoMaster.DevOpenHelper(this, "categoryNodes", null);
    }

    public DaoMaster.DevOpenHelper getHelper() {
        return mHelper;
    }
}

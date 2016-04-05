package com.andy.LuFM.app;

import android.app.Application;

import com.andy.LuFM.dbutil.DaoMaster;

/**
 * Created by Andy.Wang on 2015/11/11.
 */
public class LuFmApplication extends Application {
    private static LuFmApplication mInstance;
    private DaoMaster.DevOpenHelper mHelper;
    private DaoMaster.DevOpenHelper mProgramNodesHelper;

    public static LuFmApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initDb();
        //CrashHandler.getInstance().init(this);

    }

    private void initDb() {
        mHelper = new DaoMaster.DevOpenHelper(this, "categoryNodes", null);
        mProgramNodesHelper = new DaoMaster.DevOpenHelper(this, "programNodes", null);
    }

    public DaoMaster.DevOpenHelper getHelper() {
        return mHelper;
    }

    public DaoMaster.DevOpenHelper getProgramNodesHelperHelper() {
        return mProgramNodesHelper;
    }
}

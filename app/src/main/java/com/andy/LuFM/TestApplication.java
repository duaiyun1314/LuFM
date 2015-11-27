package com.andy.LuFM;

import android.app.Application;

import com.andy.LuFM.dbutil.DaoMaster;
import com.andy.LuFM.test.AudioPlaybackService;
import com.andy.LuFM.test.PlaybackKickstarter;

/**
 * Created by Andy.Wang on 2015/11/11.
 */
public class TestApplication extends LuFmApplication {
    private static TestApplication mInstance;
    private PlaybackKickstarter mPlaybackKickstarter;
    private boolean mIsServiceRunning;
    private AudioPlaybackService mService;

    public static TestApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Playback kickstarter.
        mPlaybackKickstarter = new PlaybackKickstarter(this.getApplicationContext());
    }

    public PlaybackKickstarter getPlaybackKickstarter() {
        return mPlaybackKickstarter;
    }

    public boolean isServiceRunning() {
        return mIsServiceRunning;
    }

    public void setIsServiceRunning(boolean running) {
        mIsServiceRunning = running;
    }

    public AudioPlaybackService getService() {
        return mService;
    }
    public void setService(AudioPlaybackService service) {
        mService = service;
    }
}

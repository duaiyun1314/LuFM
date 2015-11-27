package com.andy.LuFM.test;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.andy.LuFM.AllInOneActivity;
import com.andy.LuFM.TestApplication;

/**
 * Created by Andy.Wang on 2015/11/27.
 */
public class PlaybackKickstarter implements AudioPlaybackService.PrepareServiceListener {
    private Context mContext;
    private TestApplication mApp;
    private boolean mPlayAll;

    public PlaybackKickstarter(Context context) {
        mContext = context;
    }

    /**
     * Helper method that calls all the required method(s)
     * that initialize music playback. This method should
     * always be called when the cursor for the service
     * needs to be changed.
     */
    public void initPlayback(Context context,
                             String querySelection,
                             boolean showNowPlayingActivity,
                             boolean playAll) {

        mApp = (TestApplication) mContext.getApplicationContext();
        mPlayAll = playAll;

        if (showNowPlayingActivity) {
            //Launch NowPlayingActivity.
            Intent intent = new Intent(mContext, AllInOneActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // intent.putExtra(NowPlayingActivity.START_SERVICE, true);
            mContext.startActivity(intent);

        } else {
            //Start the playback service if it isn't running.
            if (!mApp.isServiceRunning()) {
                startService();
            } else {
                //Call the callback method that will start building the new cursor.
                mApp.getService()
                        .getPrepareServiceListener()
                        .onServiceRunning(mApp.getService());
            }

        }

    }

    /**
     * Starts AudioPlaybackService. Once the service is running, we get a
     * callback to onServiceRunning() (see below). That's where the method to
     * build the cursor is called.
     */
    private void startService() {
        Intent intent = new Intent(mContext, AudioPlaybackService.class);
        mContext.startService(intent);
    }

    @Override
    public void onServiceRunning(AudioPlaybackService service) {
        //Build the cursor and pass it on to the service.
        mApp = (TestApplication) mContext.getApplicationContext();
        mApp.setIsServiceRunning(true);
        mApp.setService(service);
        mApp.getService().setPrepareServiceListener(this);
        // mApp.getService().setCurrentSongIndex(mCurrentSongIndex);
        //new AsyncBuildCursorTask(false).execute();
    }

    @Override
    public void onServiceFailed(Exception exception) {

    }
}

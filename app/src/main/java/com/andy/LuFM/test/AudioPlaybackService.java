package com.andy.LuFM.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.andy.LuFM.TestApplication;

/**
 * Created by Andy.Wang on 2015/11/27.
 */
public class AudioPlaybackService extends Service {
    private PrepareServiceListener mPrepareServiceListener;
    private TestApplication mApp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //The service has been successfully started.
        setPrepareServiceListener(mApp.getPlaybackKickstarter());
        getPrepareServiceListener().onServiceRunning(this);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Returns an instance of the PrepareServiceListener.
     */
    public PrepareServiceListener getPrepareServiceListener() {
        return mPrepareServiceListener;
    }

    /**
     * Sets the mPrepareServiceListener object.
     */
    public void setPrepareServiceListener(PrepareServiceListener listener) {
        mPrepareServiceListener = listener;
    }

    /**
     * Public interface that provides access to
     * major events during the service startup
     * process.
     *
     * @author Saravan Pantham
     */
    public interface PrepareServiceListener {

        /**
         * Called when the service is up and running.
         */
        public void onServiceRunning(AudioPlaybackService service);

        /**
         * Called when the service failed to start.
         * Also returns the failure reason via the exception
         * parameter.
         */
        public void onServiceFailed(Exception exception);

    }

}

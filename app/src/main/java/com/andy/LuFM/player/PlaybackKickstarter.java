/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.andy.LuFM.player;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.andy.LuFM.app.NowPlayingActivity;
import com.andy.LuFM.app.PlayApplication;
import com.andy.LuFM.fragments.NowPlayingFragment;
import com.andy.LuFM.model.ProgramNode;

import java.util.List;


/**
 * Initiates the playback sequence and
 * starts AudioPlaybackService.
 *
 * @author Saravan Pantham
 */
public class PlaybackKickstarter implements AudioPlaybackService.PrepareServiceListener, NowPlayingFragment.NowPlayingActivityListener {

    private Context mContext;
    private PlayApplication mApp;

    private List<ProgramNode> programNodes;
    private int mCurrentSongIndex;
    private boolean mPlayAll;

    public PlaybackKickstarter(Context context) {
        mContext = context;
    }

    private BuildCursorListener mBuildCursorListener;

    @Override
    public void onNowPlayingActivityReady() {
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

    /**
     * Public interface that provides access to
     * major events during the cursor building
     * process.
     *
     * @author Saravan Pantham
     */
    public interface BuildCursorListener {

        /**
         * Called when the service cursor has been prepared successfully.
         */
        public void onServiceCursorReady(List<ProgramNode> programNodes, int currentSongIndex, boolean playAll);

        /**
         * Called when the service cursor failed to be built.
         * Also returns the failure reason via the exception's
         * message parameter.
         */
        public void onServiceCursorFailed(String exceptionMessage);

        /**
         * Called when/if the service is already running and
         * should update its cursor. The service's cursor may
         * need to be updated if the user tapped on "Save
         * current position", etc.
         */
        public void onServiceCursorUpdated(List<ProgramNode> programNodes);

    }

    /**
     * Helper method that calls all the required method(s)
     * that initialize music playback. This method should
     * always be called when the cursor for the service
     * needs to be changed.
     */
    public void initPlayback(Context context,
                             List<ProgramNode> lists,
                             int currentSongIndex,
                             boolean showTextNowPlayingActivity,
                             boolean playAll) {

        mApp = (PlayApplication) mContext.getApplicationContext();
        mCurrentSongIndex = currentSongIndex;
        mPlayAll = playAll;
        programNodes = lists;

        if (showTextNowPlayingActivity) {
            //Launch NowPlayingActivity.
            Intent intent = new Intent(mContext, NowPlayingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(NowPlayingFragment.START_SERVICE, true);
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
        mApp = (PlayApplication) mContext.getApplicationContext();
        mApp.setIsServiceRunning(true);
        mApp.setService(service);
        mApp.getService().setPrepareServiceListener(this);
        mApp.getService().setCurrentSongIndex(mCurrentSongIndex);
        mBuildCursorListener.onServiceCursorReady(programNodes, mCurrentSongIndex, mPlayAll);

    }

    @Override
    public void onServiceFailed(Exception exception) {
        //Can't move forward from this point.
        exception.printStackTrace();
        Toast.makeText(mContext, "unable_to_start_playback", Toast.LENGTH_SHORT).show();

    }

   /* @Override
    public void onTextNowPlayingActivityReady() {
        //Start the playback service if it isn't running.
        if (!mApp.isServiceRunning()) {
            startService();
        } else {
            //Call the callback method that will start building the new cursor.
            mApp.getService()
                    .getPrepareServiceListener()
                    .onServiceRunning(mApp.getService());
        }

    }*/

    public BuildCursorListener getBuildCursorListener() {
        return mBuildCursorListener;
    }

    public void setBuildCursorListener(BuildCursorListener listener) {
        mBuildCursorListener = listener;
    }


    public int getPreviousCurrentSongIndex() {
        return mCurrentSongIndex;
    }

}

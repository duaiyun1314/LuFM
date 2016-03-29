package com.andy.LuFM.fragments;
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

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.LuFM.app.PlayApplication;
import com.andy.LuFM.R;
import com.andy.LuFM.event.PlayActionEvent;
import com.andy.LuFM.app.NowPlayingActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;


public class MiniPlayerFragment extends Fragment {

    private Context mContext;
    private PlayApplication mApp;

    private ImageView mMiniPlayerAlbumArt;
    private RelativeLayout mPlayPauseBackground;
    private LinearLayout mDescriptionContainer;
    private ImageButton mPlayPauseButton;
    private ImageButton mNextButton;
    private TextView mTitleText;
    private TextView mSubText;
    private ProgressBar mSeekBar;
    //Handler object.
    private Handler mHandler = new Handler();


    private boolean mDrawerOpen = false;
    private DisplayImageOptions options;
    /**
     * Create a new Runnable to update the seekbar and time every 100ms.
     */
    public Runnable seekbarUpdateRunnable = new Runnable() {

        public void run() {

            try {
                long currentPosition = mApp.getService().getCurrentPosition();
                int currentPositionInSecs = (int) currentPosition / 1000;
                smoothScrollSeekbar(currentPositionInSecs);

                //mSeekbar.setProgress(currentPositionInSecs);
                mHandler.postDelayed(seekbarUpdateRunnable, 100);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    /**
     * Smoothly scrolls the seekbar to the indicated position.
     */
    private void smoothScrollSeekbar(int progress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(mSeekBar, "progress", progress);
        animation.setDuration(100);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.recommend_defaultbg)
                .showImageOnFail(R.drawable.recommend_defaultbg)
                .build();
        EventBus.getDefault().register(this);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        mApp = PlayApplication.from();

        View rootView = inflater.inflate(R.layout.fragment_queue_drawer, null);

        mMiniPlayerAlbumArt = (ImageView) rootView.findViewById(R.id.queue_drawer_album_art);
        mPlayPauseBackground = (RelativeLayout) rootView.findViewById(R.id.playPauseButtonBackground);
        mPlayPauseButton = (ImageButton) rootView.findViewById(R.id.playPauseButton);
        mNextButton = (ImageButton) rootView.findViewById(R.id.nextButton);
        mTitleText = (TextView) rootView.findViewById(R.id.songName);
        mSubText = (TextView) rootView.findViewById(R.id.artistAlbumName);
        mSeekBar = (ProgressBar) rootView.findViewById(R.id.nowPlayingSeekBar);
        mDescriptionContainer = (LinearLayout) rootView.findViewById(R.id.description_container);

        // mPlayPauseBackground.setBackgroundResource(UIElementsHelper.getShadowedCircle(mContext));
        mPlayPauseButton.setTag("pause_light");

        //  mTitleText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
        //   mSubText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));

        //Set the click listeners.
        mPlayPauseBackground.setOnClickListener(playPauseClickListener);
        mPlayPauseButton.setOnClickListener(playPauseClickListener);
        mNextButton.setOnClickListener(mOnClickNextListener);
        mDescriptionContainer.setOnClickListener(mOnClickMiniPlayer);
        mMiniPlayerAlbumArt.setOnClickListener(mOnClickMiniPlayer);

        //Restrict all touch events to this fragment.
        rootView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }

        });

        return rootView;
    }

    public void onEventMainThread(PlayActionEvent actionEvent) {
        Intent intent = actionEvent.getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle.containsKey(PlayApplication.UPDATE_PAGER_POSTIION)) {
            //Update the queue fragment with the new song info.
            initMiniPlayer();

        }

        //Updates the playback control buttons.
        if (intent.hasExtra(PlayApplication.UPDATE_PLAYBACK_CONTROLS))
            setPlayPauseButton();

        if (bundle.containsKey(PlayApplication.SERVICE_STOPPING)) {
            //    showEmptyTextView();

        }
        //Updates the buffering progress on the seekbar.
        if (intent.hasExtra(PlayApplication.UPDATE_BUFFERING_PROGRESS))
            mSeekBar.setSecondaryProgress(Integer.parseInt(
                    bundle.getString(
                            PlayApplication.UPDATE_BUFFERING_PROGRESS)));
        //Updates the duration of the SeekBar.
        if (intent.hasExtra(PlayApplication.UPDATE_SEEKBAR_DURATION)) {
            Log.i("Sync", "收到duration");
            String duration = bundle.getString(PlayApplication.UPDATE_SEEKBAR_DURATION);
            if (duration == null) return;
            setSeekbarDuration(Integer.parseInt(
                    duration));
        }
    }

    /**
     * Helper method that checks whether the audio playback service
     * is running or not.
     */
    private void checkServiceRunning() {
        if (mApp.isServiceRunning() && mApp.getService().getData() != null) {
            initMiniPlayer();
            setPlayPauseButton();
        } else {
            //  showEmptyTextView();
        }

    }

    /**
     * Initializes the mini player above the current queue.
     */
    private void initMiniPlayer() {
        //  mMiniPlayerAlbumArt.setImageBitmap(mApp.getService().getCurrentSong().getAlbumArt());
        mTitleText.setText(mApp.getService().getCurrentSong().getTitle());
        mSubText.setText(mApp.getService().getCurrentSong().getAlbum());
        ImageLoader.getInstance().displayImage(mApp.getService().getCurrentSong().getmThumb(), mMiniPlayerAlbumArt, options);

    }


    /**
     * Sets the play/pause button states.
     */
    private void setPlayPauseButton() {
        if (mApp.isServiceRunning()) {
            if (mApp.getService().isPlayingMusic())
                mPlayPauseButton.setImageResource(R.drawable.pause_light);
            else
                mPlayPauseButton.setImageResource(R.drawable.play_light);

        }

    }

    /**
     * Click listener for the play/pause button.
     */
    private View.OnClickListener playPauseClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            //BZZZT! Give the user a brief haptic feedback touch response.
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            //判断是否有播放列表
            if (mApp.getService() == null || mApp.getService().getPlaybackIndecesList().size() <= 0) {
                Toast.makeText(getActivity(), "当前播放列表为空", Toast.LENGTH_SHORT).show();
                return;
            }

            //Update the playback UI elements.
            if (mApp.getService().isPlayingMusic())
                mPlayPauseButton.setImageResource(R.drawable.play_light);
            else
                mPlayPauseButton.setImageResource(R.drawable.pause_light);

            /*
             * Toggle the playback state in a separate thread. This
             * will allow the play/pause button animation to remain
             * buttery smooth.
             */
            new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] params) {
                    mApp.getService().togglePlaybackState();
                    return null;
                }

            }.execute();

        }

    };

    /**
     * Click listener for the next button.
     */
    private View.OnClickListener mOnClickNextListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            //判断是否有播放列表
            if (mApp.getService() == null || mApp.getService().getPlaybackIndecesList().size() <= 0) {
                Toast.makeText(getActivity(), "当前播放列表为空", Toast.LENGTH_SHORT).show();
                return;
            }
            mApp.getService().skipToNextTrack();

        }

    };


    /**
     * Click listener for the mini player.
     */
    private View.OnClickListener mOnClickMiniPlayer = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //判断是否有播放列表
            if (mApp.getService() == null || mApp.getService().getPlaybackIndecesList().size() <= 0) {
                Toast.makeText(getActivity(), "当前播放列表为空", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(mContext, NowPlayingActivity.class);
            startActivity(intent);
        }

    };


    @Override
    public void onResume() {
        super.onResume();
        checkServiceRunning();
        //  mPlayPauseBackground.setBackgroundResource(UIElementsHelper.getShadowedCircle(mContext));
        mPlayPauseButton.setTag("pause_light");

        //Update the seekbar.
        try {
            setSeekbarDuration(mApp.getService().getDuration() / 1000);
        } catch (Exception e) {
        }

    }

    /**
     * Sets the seekbar's duration. Also updates the
     * elapsed/remaining duration text.
     */
    private void setSeekbarDuration(long duration) {
        mSeekBar.setMax((int) duration);
        mHandler.postDelayed(seekbarUpdateRunnable, 100);
    }

    public boolean isDrawerOpen() {
        return mDrawerOpen;
    }

    public void setIsDrawerOpen(boolean isOpen) {
        mDrawerOpen = isOpen;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}


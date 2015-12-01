package com.andy.LuFM.test;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.TestApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class MiniPlayerFragment extends Fragment {

    private Context mContext;
    private TestApplication mApp;

    private ImageView mMiniPlayerAlbumArt;
    private RelativeLayout mPlayPauseBackground;
    private ImageButton mPlayPauseButton;
    private ImageButton mNextButton;
    private TextView mTitleText;
    private TextView mSubText;
    private SeekBar mSeekBar;
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
                long currentPosition = mApp.getService().getCurrentMediaPlayer().getCurrentPosition();
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
        Log.i("Sync", "progresss:" + progress);
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
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        mApp = TestApplication.from();

        View rootView = inflater.inflate(R.layout.fragment_queue_drawer, null);

        mMiniPlayerAlbumArt = (ImageView) rootView.findViewById(R.id.queue_drawer_album_art);
        mPlayPauseBackground = (RelativeLayout) rootView.findViewById(R.id.playPauseButtonBackground);
        mPlayPauseButton = (ImageButton) rootView.findViewById(R.id.playPauseButton);
        mNextButton = (ImageButton) rootView.findViewById(R.id.nextButton);
        mTitleText = (TextView) rootView.findViewById(R.id.songName);
        mSubText = (TextView) rootView.findViewById(R.id.artistAlbumName);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.nowPlayingSeekBar);

        // mPlayPauseBackground.setBackgroundResource(UIElementsHelper.getShadowedCircle(mContext));
        mPlayPauseButton.setTag("pause_light");

        //  mTitleText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
        //   mSubText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));

        //Set the click listeners.
        mPlayPauseBackground.setOnClickListener(playPauseClickListener);
        mPlayPauseButton.setOnClickListener(playPauseClickListener);
        mNextButton.setOnClickListener(mOnClickNextListener);

        //Restrict all touch events to this fragment.
        rootView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }

        });

        //KitKat translucent navigation/status bar.
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int navBarHeight = TestApplication.getNavigationBarHeight(mContext);
            if (mListView != null) {
                mListView.setPadding(0, 0, 0, navBarHeight);
                mListView.setClipToPadding(false);
            }

        }*/

        return rootView;
    }

    /**
     * Broadcast receiver interface that will update this activity as necessary.
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            if (bundle.containsKey(TestApplication.UPDATE_PAGER_POSTIION)) {
                //Update the queue fragment with the new song info.
                initMiniPlayer();

            }

            //Updates the playback control buttons.
            if (intent.hasExtra(TestApplication.UPDATE_PLAYBACK_CONTROLS))
                setPlayPauseButton();

            if (bundle.containsKey(TestApplication.SERVICE_STOPPING)) {
                //    showEmptyTextView();

            }
            //Updates the buffering progress on the seekbar.
            if (intent.hasExtra(TestApplication.UPDATE_BUFFERING_PROGRESS))
                mSeekBar.setSecondaryProgress(Integer.parseInt(
                        bundle.getString(
                                TestApplication.UPDATE_BUFFERING_PROGRESS)));
            //Updates the duration of the SeekBar.
            if (intent.hasExtra(TestApplication.UPDATE_SEEKBAR_DURATION))
                setSeekbarDuration(Integer.parseInt(
                        bundle.getString(
                                TestApplication.UPDATE_SEEKBAR_DURATION)));

        }

    };

    /**
     * Helper method that checks whether the audio playback service
     * is running or not.
     */
    private void checkServiceRunning() {
        if (mApp.isServiceRunning() && mApp.getService().getCursor() != null) {
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
                animatePlayToPause();
            else
                animatePauseToPlay();

        }

    }

    /**
     * Animates the play button to a pause button.
     */
    private void animatePlayToPause() {

        //Check to make sure the current icon is the play icon.
        if (mPlayPauseButton.getTag() != "play_light")
            return;

        //Fade out the play button.
        final ScaleAnimation scaleOut = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                mPlayPauseButton.getWidth() / 2,
                mPlayPauseButton.getHeight() / 2);
        scaleOut.setDuration(150);
        scaleOut.setInterpolator(new AccelerateInterpolator());


        //Scale in the pause button.
        final ScaleAnimation scaleIn = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                mPlayPauseButton.getWidth() / 2,
                mPlayPauseButton.getHeight() / 2);
        scaleIn.setDuration(150);
        scaleIn.setInterpolator(new DecelerateInterpolator());

        scaleOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlayPauseButton.setImageResource(R.drawable.pause_light);
                mPlayPauseButton.setPadding(0, 0, 0, 0);
                mPlayPauseButton.startAnimation(scaleIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        scaleIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlayPauseButton.setScaleX(1.0f);
                mPlayPauseButton.setScaleY(1.0f);
                mPlayPauseButton.setTag("pause_light");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        mPlayPauseButton.startAnimation(scaleOut);
    }

    /**
     * Animates the pause button to a play button.
     */
    private void animatePauseToPlay() {

        //Check to make sure the current icon is the pause icon.
        if (mPlayPauseButton.getTag() != "pause_light")
            return;

        //Scale out the pause button.
        final ScaleAnimation scaleOut = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                mPlayPauseButton.getWidth() / 2,
                mPlayPauseButton.getHeight() / 2);
        scaleOut.setDuration(150);
        scaleOut.setInterpolator(new AccelerateInterpolator());


        //Scale in the play button.
        final ScaleAnimation scaleIn = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                mPlayPauseButton.getWidth() / 2,
                mPlayPauseButton.getHeight() / 2);
        scaleIn.setDuration(150);
        scaleIn.setInterpolator(new DecelerateInterpolator());

        scaleOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlayPauseButton.setImageResource(R.drawable.play_light);
                mPlayPauseButton.setPadding(0, 0, -5, 0);
                mPlayPauseButton.startAnimation(scaleIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        scaleIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlayPauseButton.setScaleX(1.0f);
                mPlayPauseButton.setScaleY(1.0f);
                mPlayPauseButton.setTag("play_light");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        mPlayPauseButton.startAnimation(scaleOut);
    }

    /**
     * Click listener for the play/pause button.
     */
    private View.OnClickListener playPauseClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            //BZZZT! Give the user a brief haptic feedback touch response.
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            //Update the playback UI elements.
            if (mApp.getService().isPlayingMusic())
                animatePauseToPlay();
            else
                animatePlayToPause();

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
     * Click listener for the previous button.
     */
    private View.OnClickListener mOnClickPreviousListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            mApp.getService().skipToPreviousTrack();

        }

    };

    /**
     * Click listener for the next button.
     */
    private View.OnClickListener mOnClickNextListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            mApp.getService().skipToNextTrack();

        }

    };


    /**
     * Click listener for the mini player.
     */
    private View.OnClickListener mOnClickMiniPlayer = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, NowPlayingActivity.class);
            startActivity(intent);
        }

    };

    /**
     * Click listener for the ListView.
     */
    private AdapterView.OnItemClickListener onClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mApp.isServiceRunning())
                mApp.getService().skipToTrack(position);

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
            mSeekBar.setThumb(getResources().getDrawable(R.drawable.transparent_drawable));
            setSeekbarDuration(mApp.getService().getCurrentMediaPlayer().getDuration() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets the seekbar's duration. Also updates the
     * elapsed/remaining duration text.
     */
    private void setSeekbarDuration(int duration) {
        Log.e("Sync", "duration:" + duration);
        mSeekBar.setMax(duration);
        int progress = mApp.getService().getCurrentMediaPlayer().getCurrentPosition() / 1000;
        Log.d("Sync", "progress:" + progress);
        //   mSeekBar.setProgress(duration / 5);
        mHandler.postDelayed(seekbarUpdateRunnable, 100);
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver((mReceiver), new IntentFilter(TestApplication.UPDATE_UI_BROADCAST));

    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
        super.onStop();

    }

    public boolean isDrawerOpen() {
        return mDrawerOpen;
    }

    public void setIsDrawerOpen(boolean isOpen) {
        mDrawerOpen = isOpen;
    }

}


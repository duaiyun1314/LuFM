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
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.providers.ProgramNodesProvider;
import com.github.obsessive.library.blur.ImageBlurManager;
import com.andy.LuFM.app.NowPlayingActivity;
import com.andy.LuFM.app.PlayApplication;
import com.andy.LuFM.event.PlayActionEvent;
import com.andy.LuFM.view.PlayerDiscView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import de.greenrobot.event.EventBus;


public class NowPlayingFragment extends Fragment {

    private Context mContext;
    private PlayApplication mApp;

    private ImageView mBackgroundImage;
    private ImageButton mPlayPauseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mTitleText;
    private TextView mSubText;
    private SeekBar mSeekBar;
    private PlayerDiscView discView;
    private TextView mTotalTimeTv;
    private TextView mCurrentTimeTv;
    private NowPlayingActivityListener mNowPlayingActivityListener;
    //Handler object.
    private Handler mHandler = new Handler();
    private boolean mIsCreating = true;
    public static final String START_SERVICE = "StartService";


    private boolean mDrawerOpen = false;
    private DisplayImageOptions options;
    private static final int BLUR_RADIUS = 100;
    /**
     * Create a new Runnable to update the seekbar and time every 100ms.
     */
    public Runnable seekbarUpdateRunnable = new Runnable() {

        public void run() {

            try {
                long currentPosition = mApp.getService().getCurrentMediaPlayer().getCurrentPosition();
                int currentPositionInSecs = (int) currentPosition / 1000;
                smoothScrollSeekbar(currentPositionInSecs);

                mHandler.postDelayed(seekbarUpdateRunnable, 100);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    /**
     * Create a new Runnable to update the seekbar and time every 100ms.
     */
    public Runnable currentUpdateRunnable = new Runnable() {

        public void run() {

            try {
                long currentPosition = mApp.getService().getCurrentMediaPlayer().getCurrentPosition();
                final int currentPositionInSecs = (int) currentPosition / 1000;
                mCurrentTimeTv.post(new Runnable() {
                    @Override
                    public void run() {
                        mCurrentTimeTv.setText(ProgramNodesProvider.getDurationTime(currentPositionInSecs));
                    }
                });
                //smoothScrollSeekbar(currentPositionInSecs);


                mHandler.postDelayed(currentUpdateRunnable, 1000);

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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
        mApp = PlayApplication.from();
        setNowPlayingActivityListener(mApp.getPlaybackKickstarter());
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


        View rootView = inflater.inflate(R.layout.layout_playing, null);
        mPlayPauseButton = (ImageButton) rootView.findViewById(R.id.musics_player_play_ctrl_btn);
        mNextButton = (ImageButton) rootView.findViewById(R.id.musics_player_play_next_btn);
        mPrevButton = (ImageButton) rootView.findViewById(R.id.musics_player_play_prev_btn);
        mTitleText = (TextView) rootView.findViewById(R.id.musics_player_name);
        mSubText = (TextView) rootView.findViewById(R.id.musics_player_songer_name);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.musics_player_seekbar);
        discView = (PlayerDiscView) rootView.findViewById(R.id.musics_player_disc_view);
        mBackgroundImage = (ImageView) rootView.findViewById(R.id.musics_player_background);
        mTotalTimeTv = (TextView) rootView.findViewById(R.id.musics_player_total_time);
        mCurrentTimeTv = (TextView) rootView.findViewById(R.id.musics_player_current_time);

        mPlayPauseButton.setTag("pause_light");


        //Set the click listeners.
        mPlayPauseButton.setOnClickListener(playPauseClickListener);
        mNextButton.setOnClickListener(mOnClickNextListener);
        mPrevButton.setOnClickListener(mOnClickPreviousListener);

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
        if (bundle.containsKey(PlayApplication.INIT_PAGER)) {
            initMiniPlayer();
        }
        //Updates the buffering progress on the seekbar.
        if (intent.hasExtra(PlayApplication.UPDATE_BUFFERING_PROGRESS))
            mSeekBar.setSecondaryProgress(Integer.parseInt(
                    bundle.getString(
                            PlayApplication.UPDATE_BUFFERING_PROGRESS)));
        //Updates the duration of the SeekBar.
        if (intent.hasExtra(PlayApplication.UPDATE_SEEKBAR_DURATION))
            setSeekbarDuration(Integer.parseInt(
                    bundle.getString(
                            PlayApplication.UPDATE_SEEKBAR_DURATION)));

    }


    /**
     * Initializes the mini player above the current queue.
     */
    private void initMiniPlayer() {
        //  mMiniPlayerAlbumArt.setImageBitmap(mApp.getService().getCurrentSong().getAlbumArt());
        discView.startPlay();
        mTitleText.setText(mApp.getService().getCurrentSong().getTitle());
        mSubText.setText(mApp.getService().getCurrentSong().getAlbum());
        discView.loadAlbumCover(mApp.getService().getCurrentSong().getmThumb());
        mTotalTimeTv.setText(ProgramNodesProvider.getDurationTime((int) (mApp.getService().getCurrentSong().getDuration())));
        ImageLoader.getInstance().loadImage(mApp.getService().getCurrentSong().getmThumb(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Bitmap bitmap = ImageBlurManager.doBlurJniArray(loadedImage, BLUR_RADIUS, false);
                mBackgroundImage.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

    }


    /**
     * Sets the play/pause button states.
     */
    private void setPlayPauseButton() {
        if (mApp.isServiceRunning()) {
            if (mApp.getService().isPlayingMusic()) {
                discView.isPlaying();
                mPlayPauseButton.setImageResource(R.drawable.btn_pause_selector);
            } else {
                mPlayPauseButton.setImageResource(R.drawable.btn_play_selector);
            }

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

            //Update the playback UI elements.
            if (mApp.getService().isPlayingMusic()) {
                discView.pause();
                mPlayPauseButton.setImageResource(R.drawable.btn_play_selector);
            } else {
                discView.rePlay();
                mPlayPauseButton.setImageResource(R.drawable.btn_pause_selector);
            }


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
            discView.next();

        }

    };

    /**
     * Click listener for the next button.
     */
    private View.OnClickListener mOnClickNextListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            mApp.getService().skipToNextTrack();
            discView.next();

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
        mPlayPauseButton.setTag("pause_light");
        if (mIsCreating == false) {
            mHandler.postDelayed(seekbarUpdateRunnable, 100);
            mHandler.postDelayed(currentUpdateRunnable, 100);
            mIsCreating = false;
        }

        //Animate the controls bar in.
        //animateInControlsBar();

        //Update the seekbar.
        try {
            setSeekbarDuration(mApp.getService().getCurrentMediaPlayer().getDuration() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent intent = getActivity().getIntent();
        if (intent.hasExtra(START_SERVICE) &&
                getNowPlayingActivityListener() != null) {
            getNowPlayingActivityListener().onNowPlayingActivityReady();

            /**
             * To prevent the service from being restarted every time this
             * activity is resume, we're gonna have to remove the "START_SERVICE"
             * extra from the intent.
             */
            intent.removeExtra(START_SERVICE);

        }

    }

    /**
     * Sets the seekbar's duration. Also updates the
     * elapsed/remaining duration text.
     */
    private void setSeekbarDuration(int duration) {
        mSeekBar.setMax(duration);
        int progress = mApp.getService().getCurrentMediaPlayer().getCurrentPosition() / 1000;
        mHandler.postDelayed(seekbarUpdateRunnable, 100);
        mHandler.postDelayed(currentUpdateRunnable, 100);
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

    public NowPlayingActivityListener getNowPlayingActivityListener() {
        return mNowPlayingActivityListener;
    }

    public void setNowPlayingActivityListener(NowPlayingActivityListener listener) {
        mNowPlayingActivityListener = listener;
    }

    /**
     * Interface that provides callbacks once this activity is
     * up and running.
     */
    public interface NowPlayingActivityListener {

        /**
         * Called once this activity's onResume() method finishes
         * executing.
         */
        public void onNowPlayingActivityReady();

    }

    /**
     * convert time str
     *
     * @param time
     * @return
     */
    public static String convertTime(int time) {

        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }
}


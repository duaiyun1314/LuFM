package com.andy.LuFM.app;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andy.LuFM.R;
import com.andy.LuFM.data.InfoManager;
import com.andy.LuFM.event.PlayActionEvent;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.helper.ProgramHelper;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.ProgramNode;
import com.andy.LuFM.model.RecommendPlayingItemNode;
import com.andy.LuFM.player.AudioPlaybackService;
import com.andy.LuFM.player.SongHelper;
import com.andy.LuFM.providers.ProgramNodesProvider;
import com.andy.LuFM.view.PlayerDiscView;
import com.github.obsessive.library.blur.ImageBlurManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import roboguice.util.Ln;


/**
 * Created by Andy.Wang on 2015/11/30.
 */
public class NowPlayingActivity extends BaseToolBarActivity implements InfoManager.ISubscribeEventListener, ChannelHelper.IDataChangeObserver {
    public static final String CHANNEL_MODE = "Live_Mode";//点击频道进入界面，只传进频道号，需要在acitivity中主动加载programs
    public static final String PROGRAM_MODE = "Program_Mode";//点击program进入界面，已经加载完了所有的programs
    public static final String MODE_KEY = "MODE_KEY";
    private SongHelper mCurrentSong;
    private AudioPlaybackService mPlaybackService;

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
    private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");


    private boolean mDrawerOpen = false;
    private DisplayImageOptions options;
    private static final int BLUR_RADIUS = 100;

    private String mode;//进入界面时的mode
    private ChannelNode channelNode;
    private int channelId;
    /**
     * Create a new Runnable to update the seekbar and time every 100ms.
     */
    public Runnable seekbarUpdateRunnable = new Runnable() {

        public void run() {

            try {
                long currentPosition = mApp.getService().getCurrentPosition();
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
                long currentPosition = mApp.getService().getCurrentPosition();
                final int currentPositionInSecs = (int) currentPosition / 1000;
                mCurrentTimeTv.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mApp.getService().getCurrentMode() == 1) {
                            mCurrentTimeTv.setText(ProgramNodesProvider.getDurationTime(currentPositionInSecs));
                        } else {
                            mCurrentTimeTv.setText(mFormat.format(new Date()));
                        }
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

    private void initView() {
        View rootView = View.inflate(this, R.layout.layout_playing, null);
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
        content.addView(rootView);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnLoading(R.drawable.recommend_defaultbg)
                .showImageOnFail(R.drawable.recommend_defaultbg)
                .build();
        EventBus.getDefault().register(this);
        mContext = this;
        mApp = PlayApplication.from();
        //Set the volume stream for this activity.
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        mPlaybackService = mApp.getService();
        if (mPlaybackService != null && mPlaybackService.isPlayingMusic()) {
            mCurrentSong = mPlaybackService.getCurrentSong();
            setTitle(mCurrentSong.getAlbum());
        }
        setNowPlayingActivityListener(mApp.getPlaybackKickstarter());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(NowPlayingActivity.CHANNEL_MODE)) {
            mode = NowPlayingActivity.CHANNEL_MODE;


            RecommendPlayingItemNode playingItemNode = (RecommendPlayingItemNode) bundle.getSerializable(NowPlayingActivity.CHANNEL_MODE);
            channelNode = ChannelHelper.getInstance().getChannel(playingItemNode.channelId, 0);
            channelId = playingItemNode.channelId;
            ChannelHelper.getInstance().addObserver(channelId, this);

            if (channelNode == null) {
                Ln.d(" null load live programsschedule async");
                //do nothing 等待后台getChannelInfo 后调用 onChannelNodeInfoUpdate
            } else {
                Ln.d( "not null load directly");
                onNotification(InfoManager.ISubscribeEventListener.RECV_PROGRAMS_SCHEDULE);
            }
        } else {
            mode = NowPlayingActivity.PROGRAM_MODE;
        }

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
        mTotalTimeTv.setText(mApp.getService().getCurrentSong().getSeekbarEndLabel());
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
            setSeekbarDuration(mApp.getService().getDuration() / 1000);
        } catch (Exception e) {
        }


        Intent intent = this.getIntent();
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
    private void setSeekbarDuration(long duration) {
        mSeekBar.setMax((int) duration);
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
        ChannelHelper.getInstance().removeObserver(channelId);
        InfoManager.getInstance().unRegisterSubscribeEventListener(this, InfoManager.ISubscribeEventListener.RECV_PROGRAMS_SCHEDULE);
    }

    public NowPlayingActivityListener getNowPlayingActivityListener() {
        return mNowPlayingActivityListener;
    }

    public void setNowPlayingActivityListener(NowPlayingActivityListener listener) {
        mNowPlayingActivityListener = listener;
    }

    @Override
    public void onNotification(String type) {
        if (type.equalsIgnoreCase(InfoManager.ISubscribeEventListener.RECV_PROGRAMS_SCHEDULE)) {
            Ln.d("点击的Channel:" + channelNode.channelId);
            List<ProgramNode> currentLiveLists = channelNode.getLstProgramNode(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
            PlayApplication.from().getPlaybackKickstarter().initPlayback(this, currentLiveLists, channelNode.getSongIndexByTime(currentLiveLists, System.currentTimeMillis()), false, true);
        }

    }

    @Override
    public void onChannelNodeInfoUpdate(ChannelNode channelNode) {
        this.channelNode = channelNode;
        InfoManager.getInstance().loadLiveProgramSchedule(ProgramHelper.getInstance(), channelNode.channelId, Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + "", this);

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


}

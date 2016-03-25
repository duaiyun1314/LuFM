package com.andy.LuFM.app;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.andy.LuFM.R;
import com.andy.LuFM.fragments.NowPlayingFragment;
import com.andy.LuFM.player.AudioPlaybackService;
import com.andy.LuFM.player.SongHelper;


/**
 * Created by Andy.Wang on 2015/11/30.
 */
public class NowPlayingActivity extends BaseToolBarActivity {
    public static final String CHANNEL_MODE = "Live_Mode";//点击频道进入界面，只传进频道号，需要在acitivity中主动加载programs
    public static final String PROGRAM_MODE = "Program_Mode";//点击program进入界面，已经加载完了所有的programs
    public static final String MODE_KEY = "MODE_KEY";
    private PlayApplication mPlayApplication;
    private SongHelper mCurrentSong;
    private AudioPlaybackService mPlaybackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the volume stream for this activity.
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mPlayApplication = PlayApplication.from();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        mPlaybackService = mPlayApplication.getService();
        if (mPlaybackService != null && mPlaybackService.isPlayingMusic()) {
            mCurrentSong = mPlaybackService.getCurrentSong();
            setTitle(mCurrentSong.getAlbum());
        }
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        Fragment mContentCurr = new NowPlayingFragment();
        Intent intent = getIntent();
        mContentCurr.setArguments(intent.getExtras());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, mContentCurr);
        ft.commit();
    }

}

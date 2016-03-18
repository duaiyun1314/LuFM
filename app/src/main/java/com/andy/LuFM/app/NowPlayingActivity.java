package com.andy.LuFM.app;

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
    private PlayApplication mPlayApplication;
    private SongHelper mCurrentSong;
    private AudioPlaybackService mPlaybackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the volume stream for this activity.
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mPlayApplication = PlayApplication.from();
        mPlaybackService = mPlayApplication.getService();
        mCurrentSong = mPlaybackService.getCurrentSong();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        setTitle(mCurrentSong.getAlbum());
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        Fragment mContentCurr = new NowPlayingFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, mContentCurr);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}

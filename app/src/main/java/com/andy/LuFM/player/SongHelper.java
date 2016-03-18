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
import android.util.Log;

import com.andy.LuFM.app.PlayApplication;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.model.ChannelNode;
import com.andy.LuFM.model.Node;
import com.andy.LuFM.model.ProgramNode;

import java.util.List;


/**
 * 当前播放音频的相关信息
 *
 * @author Andy.Wang
 */
public class SongHelper {

    private SongHelper mSongHelper;
    private PlayApplication mApp;
    private int mIndex;
    private boolean mIsCurrentSong = false;
    private boolean mIsAlbumArtLoaded = false;

    //Song parameters.
    private String mTitle;//标题
    private String mArtist;
    private String mAlbum;//专辑
    private double mDuration;//持续时间
    private String mThumb;//图像
    private int mId;//channel id
    private String mSource;//播放地址
    private long mSavedPosition;

    private AlbumArtLoadedListener mAlbumArtLoadedListener;

    /**
     * Interface that provides callbacks to the provided listener
     * once the song's album art has been loaded.
     */
    public interface AlbumArtLoadedListener {

        /**
         * Called once the album art bitmap is ready for use.
         */
        public void albumArtLoaded();
    }


    /**
     * Moves the specified cursor to the specified index and populates this
     * helper object with new song data.
     *
     * @param context Context used to get a new PlayApplication object.
     * @param index   The index of the song.
     */
    public void populateSongData(Context context, int index) {

        mSongHelper = this;
        mApp = PlayApplication.from();
        mIndex = index;

        if (mApp.isServiceRunning()) {
            List<ProgramNode> programNodes = mApp.getService().getData();
            Log.i("Sync", "当前size:" + programNodes.size());
            Node node = programNodes.get(index);
            if (node instanceof ProgramNode) {
                ProgramNode programNode = (ProgramNode) node;
                //this.setId(programNode.channelId);
                this.setTitle(programNode.title);
                ChannelNode cn = ChannelHelper.getInstance().getChannel(programNode);
                if (cn != null) {
                    this.setId(cn.channelId);
                    this.setAlbum(cn.title);
                    this.setmThumb(cn.getApproximativeThumb(50, 50, true));
                    this.setDuration(programNode.duration);
                }


            }
        }

    }

    public String getmThumb() {
        return mThumb;
    }

    public void setmThumb(String mThumb) {
        this.mThumb = mThumb;
    }

    /**
     * Sets this helper object as the current song. This method
     * will check if the song's album art has already been loaded.
     * If so, the updateNotification() and updateWidget() methods
     * will be called. If not, they'll be called as soon as the
     * album art is loaded.
     */

    public void setIsCurrentSong() {
        mIsCurrentSong = true;
        //The album art has already been loaded.
        if (mIsAlbumArtLoaded) {
            mApp.getService().updateNotification(this);
            mApp.getService().updateWidgets();
        } else {
            /*
             * The album art isn't ready yet. The listener will call
			 * the updateNotification() and updateWidgets() methods.
			 */
        }

    }


    public int getSongIndex() {
        return mIndex;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }


    public double getDuration() {
        return mDuration;
    }

    public void setDuration(double duration) {
        mDuration = duration;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }


    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        mSource = source;
    }


    public long getSavedPosition() {
        return mSavedPosition;
    }

}

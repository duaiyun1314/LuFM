package com.andy.LuFM.Utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wanglu on 15/11/20.
 */
public class PlayStatus implements Parcelable {

    public static final int DETAIL_MASK = 255;
    public static final int DO_PLAY = 4116;
    public static final int ERROR = 8192;
    public static final int INIT = 30583;
    public static final int PAUSE = 1;
    public static final int PLAY = 4096;
    public static final int PLAYING_MASK = 65280;
    public static final int PLAY_COMPLETE = 2;
    public static final int PLAY_PROGRESS = 4099;
    public static final int PLAY_START = 4097;
    public static final int SEEK_COMPLETE = 4113;
    public static final int SEEK_START = 4112;
    public static final int STOP = 0;

    protected PlayStatus(Parcel in) {
    }

    public static final Creator<PlayStatus> CREATOR = new Creator<PlayStatus>() {
        @Override
        public PlayStatus createFromParcel(Parcel in) {
            return new PlayStatus(in);
        }

        @Override
        public PlayStatus[] newArray(int size) {
            return new PlayStatus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}

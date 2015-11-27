package com.andy.LuFM.test;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayStatus implements Parcelable {
    public static final int BUFFER = 4098;
    public static final int BUFFER_CUSTOM = 4101;
    public static final int BUFFER_FULL = 4100;
    public static final Creator<PlayStatus> CREATOR = new Creator<PlayStatus>() {
        public PlayStatus createFromParcel(Parcel in) {
            return new PlayStatus(in);
        }

        public PlayStatus[] newArray(int size) {
            return null;
        }
    };
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
    public static final int WARNING_UNSTABLE = 16384;
    public long bufferLength;
    public long bufferTime;
    public long duration;
    public int state;
    public long time;

    public PlayStatus() {
        this.state = STOP;
        this.duration = 0;
        this.time = 0;
        this.bufferTime = 0;
        this.bufferLength = 0;
    }

    public PlayStatus(int state) {
        this(state, 0, 0);
    }

    public PlayStatus(int state, long duration, long time) {
        this(state, duration, time, 0, 0);
    }

    public PlayStatus(long bufferTime, long bufferLength) {
        this(BUFFER, 0, 0, bufferTime, bufferLength);
    }

    public PlayStatus(int state, long duration, long time, long bufferTime, long bufferLength) {
        this.state = STOP;
        this.duration = 0;
        this.time = 0;
        this.bufferTime = 0;
        this.bufferLength = 0;
        setPlayStatus(state, duration, time, bufferTime, bufferLength);
    }

    public void setPlayStatus(int state, long duration, long time, long bufferTime, long bufferLength) {
        this.state = state;
        this.duration = duration;
        this.time = time;
        this.bufferTime = bufferTime;
        this.bufferLength = bufferLength;
    }

    public PlayStatus clone() {
        return new PlayStatus(this.state, this.duration, this.time, this.bufferTime, this.bufferLength);
    }

    public int getPlayingState() {
        return this.state & PLAYING_MASK;
    }

    public int getState() {
        return this.state;
    }

    public int describeContents() {
        return STOP;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state);
        dest.writeLong(this.duration);
        dest.writeLong(this.time);
        dest.writeLong(this.bufferTime);
        dest.writeLong(this.bufferLength);
    }

    public PlayStatus(Parcel dest) {
        this.state = STOP;
        this.duration = 0;
        this.time = 0;
        this.bufferTime = 0;
        this.bufferLength = 0;
        this.state = dest.readInt();
        this.duration = dest.readLong();
        this.time = dest.readLong();
        this.bufferTime = dest.readLong();
        this.bufferLength = dest.readLong();
    }

    public String toString() {
        String action = "unknown";
        String detail = "";
        switch (this.state) {
            case STOP /*0*/:
                action = "stop";
                break;
            case PAUSE /*1*/:
                action = "pause";
                detail = String.format("time:%d, duration:%d, bufferLength:%d, bufferTime:%d", new Object[]{Long.valueOf(this.time), Long.valueOf(this.duration), Long.valueOf(this.bufferLength), Long.valueOf(this.bufferTime)});
                break;
            case PLAY_COMPLETE /*2*/:
                action = "play_complete";
                detail = String.format("time:%d, duration:%d, bufferLength:%d, bufferTime:%d", new Object[]{Long.valueOf(this.time), Long.valueOf(this.duration), Long.valueOf(this.bufferLength), Long.valueOf(this.bufferTime)});
                break;
            case PLAY /*4096*/:
                action = "play";
                detail = String.format("time:%d, duration:%d, bufferLength:%d, bufferTime:%d", new Object[]{Long.valueOf(this.time), Long.valueOf(this.duration), Long.valueOf(this.bufferLength), Long.valueOf(this.bufferTime)});
                break;
            case PLAY_START /*4097*/:
                action = "play_start";
                break;
            case BUFFER /*4098*/:
                action = "buffer";
                detail = String.format("time:%d, duration:%d, bufferLength:%d, bufferTime:%d", new Object[]{Long.valueOf(this.time), Long.valueOf(this.duration), Long.valueOf(this.bufferLength), Long.valueOf(this.bufferTime)});
                break;
            case PLAY_PROGRESS /*4099*/:
                action = "play_progress";
                break;
            case BUFFER_FULL /*4100*/:
                action = "buffer_full";
                detail = String.format("time:%d, duration:%d, bufferLength:%d, bufferTime:%d", new Object[]{Long.valueOf(this.time), Long.valueOf(this.duration), Long.valueOf(this.bufferLength), Long.valueOf(this.bufferTime)});
                break;
            case SEEK_START /*4112*/:
                action = "seek_start";
                detail = String.format("time:%d, duration:%d, bufferLength:%d, bufferTime:%d", new Object[]{Long.valueOf(this.time), Long.valueOf(this.duration), Long.valueOf(this.bufferLength), Long.valueOf(this.bufferTime)});
                break;
            case SEEK_COMPLETE /*4113*/:
                action = "seek_complete";
                detail = String.format("time:%d, duration:%d, bufferLength:%d, bufferTime:%d", new Object[]{Long.valueOf(this.time), Long.valueOf(this.duration), Long.valueOf(this.bufferLength), Long.valueOf(this.bufferTime)});
                break;
            case ERROR /*8192*/:
                action = "error";
                detail = String.format("time:%d, duration:%d, bufferLength:%d, bufferTime:%d", new Object[]{Long.valueOf(this.time), Long.valueOf(this.duration), Long.valueOf(this.bufferLength), Long.valueOf(this.bufferTime)});
                break;
        }
        Object[] objArr = new Object[PLAY_COMPLETE];
        objArr[STOP] = action;
        objArr[PAUSE] = detail;
        return String.format("%s %s", objArr);
    }
}
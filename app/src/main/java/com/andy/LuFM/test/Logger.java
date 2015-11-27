package com.andy.LuFM.test;

import android.util.Log;

public class Logger {
    private int mLogLevel = 4;
    private String mLogTag = "Logger";

    public Logger(int log_level, String log_tag) {
        this.mLogLevel = log_level;
        this.mLogTag = log_tag;
    }

    public void debug(String format, Object... args) {
        if (this.mLogLevel <= 3) {
            Log.d(this.mLogTag, String.format(format, args));
        }
    }

    public void info(String format, Object... args) {
    }

    public void warning(String format, Object... args) {
        if (this.mLogLevel <= 5) {
            Log.w(this.mLogTag, String.format(format, args));
        }
    }

    public void error(String format, Object... args) {
        if (this.mLogLevel <= 6) {
            Log.e(this.mLogTag, String.format(format, args));
        }
    }
}

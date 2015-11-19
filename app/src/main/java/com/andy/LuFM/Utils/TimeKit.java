package com.andy.LuFM.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wanglu on 15/11/19.
 */
public class TimeKit {
    private static final String JUST = "\u521a\u521a\u66f4\u65b0";
    private static final String MODEL_HOUR = "%d\u5c0f\u65f6\u524d";
    private static final String MODEL_MINUTE = "%d\u5206\u949f\u524d";
    private static final long ONEHOUR = 3600000;
    private static final long TENMINUTE = 600000;
    private static final long THREEHOUR = 10800000;
    private static final String TODAY = "\u4eca\u5929";

    public static String msToDate3(long ms) {
        return new SimpleDateFormat("HH:mm", Locale.US).format(new Date(ms));
    }

    public static long dateToMS(String time) {
        long j = 0;
        if (time != null) {
            try {
                if (time.length() >= 2) {
                    j = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(time).getTime();
                }
            } catch (Exception e) {
            }
        }
        return j;
    }

    public static String getReadableTime(long updateTime) {
        if (updateTime == 0) {
            return JUST;
        }
        long currentTime = System.currentTimeMillis();
        boolean isSameDay = false;
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(updateTime);
        int publishYear = calendar.get(Calendar.YEAR);
        int publishDay = calendar.get(Calendar.DAY_OF_YEAR);
        if (currentYear == publishYear && currentDay == publishDay) {
            isSameDay = true;
        }
        if (!isSameDay) {
            return msToDate5(updateTime);
        }
        long interval = currentTime - updateTime;
        if (interval < TENMINUTE) {
            return JUST;
        }
        if (interval < ONEHOUR) {
            return String.format(Locale.CHINESE, MODEL_MINUTE, new Object[]{Integer.valueOf((int) ((interval / 1000) / 60))});
        } else if (interval >= THREEHOUR) {
            return TODAY;
        } else {
            return String.format(Locale.CHINESE, MODEL_HOUR, new Object[]{Integer.valueOf((int) (((interval / 1000) / 60) / 60))});
        }
    }

    public static String msToDate5(long ms) {
        return new SimpleDateFormat("MM\u6708dd\u65e5", Locale.CHINA).format(new Date(ms));
    }
}

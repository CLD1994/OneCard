package com.zhuoli.onecard.utils;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class TimeHelper {

    private final static String DISPLAY_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static SimpleDateFormat format;

    static {
        format = new SimpleDateFormat(DISPLAY_TIME_FORMAT, Locale.getDefault());
    }


    /**
     * get current date,
     * example: see test
     *
     * @return format: yyyy-MM-dd HH:mm:ss
     */
    public static String native2unix() {
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String unixTime = format.format(new Date());
        format.setTimeZone(TimeZone.getDefault());
        return unixTime;
    }

    /**
     * input format: yyyy-MM-dd HH:mm:ss
     *
     * @param unixTime
     * @return string format: yyyy-MM-dd HH:mm:ss
     */
    public static String unix2native(String unixTime) {
        if (TextUtils.isEmpty(unixTime)) {
            return null;
        }
        Date date = unix2nativeDate(unixTime);
        if (date != null) {
            return format.format(date);
        } else {
            return null;
        }
    }

    /**
     * input format: yyyy-MM-dd HH:mm:ss
     *
     * @param unixTime
     * @return Date
     */
    public static Date unix2nativeDate(String unixTime) {
        if (TextUtils.isEmpty(unixTime)) {
            return null;
        }
        Date date = null;
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            date = format.parse(unixTime);
        } catch (ParseException e) {
            Logger.e(e.toString());
        }
        format.setTimeZone(TimeZone.getDefault());

        return date;
    }

    /**
     * @return String format:yyyy-MM-dd HH:mm:ss
     */
    public static String getDisplayTime(Date date) {
        if (date == null) {
            return null;
        }
        return format.format(date);
    }

    /**
     * String to Date
     *
     * @param time format (yyyy-MM-dd HH:mm:ss)
     * @return
     */
    public static Date getDateFromNativeTime(String time) {
        Date date = null;
        if (TextUtils.isEmpty(time)) {
            return null;
        }
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            Logger.e(e.toString());
        }
        return date;
    }
}

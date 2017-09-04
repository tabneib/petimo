package de.tud.nhd.petimo.controller;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nhd on 01.09.17.
 */

public class TimeUtils {

    private static final String TAG = "TimeUtils";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    /**
     *
     * @param date
     * @return
     */
    public static int getHourFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static int getDateIntFromDate(Date date){
        return Integer.parseInt(dateFormat.format(date));
    }


    /**
     *
     * @param date
     * @return
     */
    public static  long getDateFromStr(String date){
        //TODO implement me
        return Integer.parseInt(date);
    }

    /**
     *
     * @param date
     * @return
     */
    public static String getDateStrFromInt(int date){
        // TODO
        return Integer.toString(date);
    }

    /**
     * Get the integer that represents today date
     * TODO also consider the overNightThreshold ;)
     */
    public static int getTodayDate(){
        int date = Integer.parseInt(dateFormat.format(new Date()));
        return date;
    }

    /**
     * Return the time as milliseconds from the given string of 'HH:MM' format
     * @param time
     * @param date
     * @return
     */
    public static long getMsTimeFromStr(String time, String date){
        //TODO implement me
        return 0;
    }

    /**
     * Return the time string of 'HH:MM' format from the given long value
     * @param time
     * @return
     */
    public static String getDayTimeFromMsTime(long time){
        return timeFormat.format(new Date(time));
    }

    /**
     * Return the duration of 'HH:MM' format from the given duration in milliseconds
     * @param time
     * @return
     */
    public static String getTimeFromMs(long time){
        int hours = (int) time/360000;
        int minutes = (int) (time - hours*360000)/6000;
        return hours + ":" + minutes;
    }
    /**
     *
     * @param date
     * @return
     */
    public static int getWeekDay(int date){
        // TODO implement me. For now it's always sunday :)
        return 8;
    }
}

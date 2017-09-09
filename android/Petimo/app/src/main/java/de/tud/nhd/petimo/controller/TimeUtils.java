package de.tud.nhd.petimo.controller;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by nhd on 01.09.17.
 */

public class TimeUtils {

    private static final String TAG = "TimeUtils";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private static final SimpleDateFormat dateStrFormat = new SimpleDateFormat("dd.MM.yyyy");


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

    /**
     *
     * @param date
     * @return
     */
    public static int getDateIntFromDate(Date date){
        return Integer.parseInt(dateFormat.format(date));
    }


    /**
     *
     * @param calendar
     * @return
     */
    public static int getDateIntFromCalendatr(Calendar calendar){
        return Integer.parseInt(dateFormat.format(new Date(calendar.getTimeInMillis())));
    }

    /**
     *
     * @param calendar
     * @return
     */
    public static String getDateStrFromCalendar(Calendar calendar){
        return dateStrFormat.format(new Date(calendar.getTimeInMillis()));
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
     * Return the string representation from the given date.
     * @param date  the integer representation of the date, in YYYYMMDD format
     * @return
     */
    public static String getDateStrFromInt(int date){
        String result;
        String dateStr = Integer.toString(date);
        result = dateStr.substring(6,8) + ".";
        result = result + dateStr.substring(4,6) + ".";
        result = result + dateStr.substring(0,4);
        return result;
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
        //Calendar calendar = Calendar.getInstance();
        String result = "";
        Date date = new Date(time);
        result = dateFormat.format(date) + " - " + timeFormat.format(date);
        Log.d(TAG, "getDayTimeFromMsTime:" + time + " -> " + result);
        return timeFormat.format(date);
    }

    /**
     * Return the duration of 'HH:MM' format from the given duration in milliseconds
     * @param time
     * @return
     */
    public static String getTimeFromMs(long time){
        long timeInMinutes = time / (1000 * 60);
        int hours = (int) (timeInMinutes / 60);
        int minutes = (int) (timeInMinutes % 60);
        return hours + ":" + minutes;
    }

    /**
     * Determine the week day of the given date
     * @param date the integer representation of the given date, in yyyyMMdd format
     * @return day of week, start from sunday as value 1
     */
    public static int getWeekDay(int date){
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyyMMdd").parse(Integer.toString(date)));
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
}

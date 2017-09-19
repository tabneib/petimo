package de.tud.nhd.petimo.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.tud.nhd.petimo.model.PetimoSharedPref;

/**
 * Created by nhd on 01.09.17.
 */

public class PetimoTimeUtils {

    private static final String TAG = "PetimoTimeUtils";

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
     * TODO: WTH is this ???????
     * @param date
     * @return
     */
    public static long getDateFromStr(String date){
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
     * Calculate the time in milliseconds of the start of the given day
     * @param date the Date object that describes the given day
     * @return day start in milliseconds
     */
    public static long getDayStartInMillis(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Calculate the time in milliseconds of the start of the given day
     * @param dateInt  int representation of the given date
     * @return
     */
    public static long getDayStartInMillis(int dateInt){
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateInt/10000, (dateInt % 10000)/100, dateInt % 100, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Get the integer that represents today date
     * overNightThreshold is also considered: If current time has passed midnight but not yet Over-
     * night threshold, so today date is yesterday date
     */
    public static int getTodayDate(){
        int date = Integer.parseInt(dateFormat.format(new Date()));
        if (getCurrentHour() < PetimoSharedPref.getInstance().getOvThreshold())
            date--;
        return date;
    }

    /**
     * @return the current hour
     */
    public static int getCurrentHour(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * @return the current minute
     */
    public static int getCurrentMinute(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
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
     * Calculate the time in milliseconds from the given hour and minute chosen by user.
     * The Overnight threshold is also taken into account to identify the case in which there is
     * conflict between system date and monitor date: User intends to choose a time point which is
     * still on current monitor date but according to system date it is on yesterday.
     * @param hour
     * @param minute
     * @return
     */
    public static long getTimeMillisFromHM(int hour, int minute){
        long millis = getDayStartInMillis(new Date()) + hour * 60*60*1000 + minute * 60*1000;
        // If user manually set the time to a time point on yesterday and the current time
        // has passed midnight but not yet passed overnight threshold
        // => set millis to 1 day earlier
        if (hour >= PetimoSharedPref.getInstance().getOvThreshold() &&
                getCurrentHour() < PetimoSharedPref.getInstance().getOvThreshold())
            millis = millis - 24 * 60 * 60 * 1000;
        return millis;
    }


    /**
     * Calculate the time in milliseconds from the given date, hour and minute chosen by user.
     * The Overnight threshold is also taken into account.
     * @param hour
     * @param minute
     * @return
     */
    public static long getTimeMillisFromHM(int date, int hour, int minute){
        if (hour < PetimoSharedPref.getInstance().getOvThreshold())
            hour = hour + 24;
        return getDayStartInMillis(date) + hour * 60*60*1000 + minute * 60*1000;
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
        return hours + ":" + (minutes < 10 ? "0" + minutes : Integer.toString(minutes));
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

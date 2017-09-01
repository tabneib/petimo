package de.tud.nhd.petimo.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nhd on 01.09.17.
 */

public class TimeUtils {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

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

    public static int getDateInt(Date date){
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
     * Return the time as number of minutes from the start of the given day.
     * @param time
     * @param date
     * @return
     */
    public static long getTimeFromStr(String time, String date){
        //TODO implement me
        return 0;
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

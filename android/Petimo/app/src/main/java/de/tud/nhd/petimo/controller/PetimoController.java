package de.tud.nhd.petimo.controller;


import android.content.Context;
import android.util.Log;

import java.sql.Time;
import java.util.Date;

import de.tud.nhd.petimo.model.PetimoDbWrapper;
import de.tud.nhd.petimo.model.PetimoSharedPref;

/**
 * Created by nhd on 31.08.17.
 */

public class PetimoController {
    private static final String TAG = "PetimoController";
    private static PetimoController _instance;
    private PetimoDbWrapper dbWrapper;
    private PetimoSharedPref sharedPref;


    //<---------------------------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------------------------->

    private PetimoController(Context context){
        try{
            PetimoDbWrapper.initialize(context);
            this.dbWrapper = PetimoDbWrapper.getInstance();
            PetimoSharedPref.initialize(context);
            this.sharedPref = PetimoSharedPref.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void initialize(Context context) throws Exception{
        if(_instance != null)
            throw new Exception("Cannot initialize multiple instances of Controller!");
        else {
            _instance = new PetimoController(context);
            Log.d(TAG, "Initialized!");
        }
    }

    public static PetimoController getInstance() throws Exception{
        if (_instance == null)
            throw new Exception("PetimoController is not yet initialized!");
        else
            return _instance;
    }

    //<---------------------------------------------------------------------------------------------
    //  Core - Handle User inputs
    // -------------------------------------------------------------------------------------------->

    /**
     * TODO comment em
     * @param name
     * @param priority
     */
    public void addCategory(String name, int priority){
        long returnCode = this.dbWrapper.insertCategory(name, priority);
        // TODO update view: notify the user according to the return code
    }

    /**
     * TODO comment em
     * @param name
     * @param category
     * @param priority
     */
    public void addTask(String name, String category, int priority){
        long returnCode = this.dbWrapper.insertTask(name, category, priority);
        // TODO update view: notify the user according to the return code
    }

    /**
     * TODO: Check for invalid information - Time in the future; Time conflicts with other blocks
     * @param inputTask
     * @param inputCat
     * @param inputStart
     * @param inputEnd
     * @param inputDate
     */
    public void addBlockManually(String inputTask, String inputCat,
                                 String inputStart, String inputEnd, String inputDate){
        if (TimeUtils.getDateFromStr(inputDate) == -1){
            // TODO update view: notify user about invalid input date
            return;
        }
        long start = TimeUtils.getTimeFromStr(inputStart, inputDate);
        if (start == -1){
            // TODO update view: notify user about invalid input start time
            return;
        }
        long end = TimeUtils.getTimeFromStr(inputEnd, inputDate);
        if (end == -1){
            // TODO update view: notify user about invalid input end time
            return;
        }
        if (end <= start){
            // TODO update view:notify user about invalid input start and end time
            return;
        }
        int date = (int) TimeUtils.getDateFromStr(inputDate);
        long returnCode = this.dbWrapper.insertMonitorBlock(
                inputTask, inputCat, start, end, end - start, date, TimeUtils.getWeekDay(date),
                isOverNight(inputDate, inputStart, inputEnd));
        // TODO update view: notify the user according to the return code
    }

    /**
     *
     * We assume that the input category, task are correct because the user has to choose them from
     * a drop down menu.
     * @param inputCat
     * @param inputTask
     */
    public void addBlockLive(String inputCat, String inputTask){
        Date current = new Date();
        if (!sharedPref.isMonitoring()){
            // Case there is no ongoing monitor
            sharedPref.setLiveMonitor(inputCat, inputTask, getLiveDate(current), current.getTime());
            // TODO: update view: Notify the user about the ongoing monitor
            return;
        }
        else{
            // Case there's an ongoing monitor
            // In this case all the parameters are ignored.
            long start = sharedPref.getMonitorStart();
            long end = current.getTime();
            int date = sharedPref.getMonitorDate();
            long returnCode = this.dbWrapper.insertMonitorBlock(
                    sharedPref.getMonitorTask(), sharedPref.getMonitorCat(),
                    sharedPref.getMonitorStart(), end, end - start,
                    sharedPref.getMonitorDate(), TimeUtils.getWeekDay(date),
                    isOverNight(date, start, end));
            // TODO update view: notify the user according to the return code
        }
    }

    //<---------------------------------------------------------------------------------------------
    //  Auxiliary
    // -------------------------------------------------------------------------------------------->

    /**
     * The live monitoring date is the day before if the monitor starts between midnight and the
     * overnight threshold.
     * @param date the date object that capture the start time of the monitor
     * @return the live monitoring date as an integer
     */
    private int getLiveDate(Date date){
        int dateInt = TimeUtils.getDateIntFromDate(date);
        int hours = TimeUtils.getHourFromDate(date);
        if (0 <= hours && hours <= sharedPref.getOvThreshold())
            // the user is working overnight
            dateInt--;
        return dateInt;
    }

    /**
     *
     * @param date
     * @param start the start time string in 'HH:MM' format
     * @param end
     * @return
     */
    public int isOverNight(String date, String start, String end){
        return isOverNight(Integer.parseInt(date),
                TimeUtils.getTimeFromStr(start, date), TimeUtils.getTimeFromStr(end,date));
    }

    /**
     *
     * @param date
     * @param start
     * @param end
     * @return
     */
    public int isOverNight(int date, long start, long end){
        // TODO implement me. For now never overnight, good boy :)
        return 0;
    }
}

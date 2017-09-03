package de.tud.nhd.petimo.controller;


import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.List;

import de.tud.nhd.petimo.model.MonitorDay;
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
    //  Core - Inputting
    // -------------------------------------------------------------------------------------------->

    /**
     * TODO comment em
     * @param name
     * @param priority
     * @return The corresponding response code
     */
    public ResponseCode addCategory(String name, int priority){
        return this.dbWrapper.insertCategory(name, priority);
    }

    /**
     * TODO comment em
     * @param name
     * @param category
     * @param priority
     */
    public ResponseCode addTask(String name, String category, int priority){
        return this.dbWrapper.insertTask(name, category, priority);
    }

    /**
     * TODO: Check for invalid information - Time in the future; Time conflicts with other blocks
     * @param inputTask
     * @param inputCat
     * @param inputStart
     * @param inputEnd
     * @param inputDate
     * @return
     */
    public ResponseCode addBlockManually(String inputTask, String inputCat,
                                         String inputStart, String inputEnd, String inputDate){
        if (TimeUtils.getDateFromStr(inputDate) == -1)
            return ResponseCode.INVALID_INPUT_STRING_DATE;
        long start = TimeUtils.getTimeFromStr(inputStart, inputDate);
        if (start == -1)
            return ResponseCode.INVALID_INPUT_STRING_TIME;
        long end = TimeUtils.getTimeFromStr(inputEnd, inputDate);
        if (end == -1)
            return ResponseCode.INVALID_INPUT_STRING_TIME;
        if (end <= start)
            return ResponseCode.INVALID_TIME;
        int date = (int) TimeUtils.getDateFromStr(inputDate);
        return this.dbWrapper.insertMonitorBlock(
                inputTask, inputCat, start, end, end - start, date, TimeUtils.getWeekDay(date),
                isOverNight(inputDate, inputStart, inputEnd));
    }

    /**
     *
     * We assume that the input category, task are correct because the user has to choose them from
     * a drop down menu.
     * @param inputCat
     * @param inputTask
     * @return the corresponding response code
     */
    public ResponseCode addBlockLive(String inputCat, String inputTask){
        Date current = new Date();
        if (!sharedPref.isMonitoring()){
            // Case there is no ongoing monitor
            sharedPref.setLiveMonitor(inputCat, inputTask, getLiveDate(current), current.getTime());
            return ResponseCode.OK;
        }
        else{
            // Case there's an ongoing monitor
            // In this case all the parameters are ignored.
            long start = sharedPref.getMonitorStart();
            long end = current.getTime();
            int date = sharedPref.getMonitorDate();
            sharedPref.clearLiveMonitor();
            return this.dbWrapper.insertMonitorBlock(
                    sharedPref.getMonitorTask(), sharedPref.getMonitorCat(),
                    sharedPref.getMonitorStart(), end, end - start,
                    sharedPref.getMonitorDate(), TimeUtils.getWeekDay(date),
                    isOverNight(date, start, end));
        }
    }

    //<---------------------------------------------------------------------------------------------
    //  Core - Outputting
    // -------------------------------------------------------------------------------------------->

    /**
     *
     * @param inputStartDate
     * @param inputEndDate
     * @return the list of all satisfied monitor days, or null if the inputs are invalid
     */
    public List<MonitorDay> getDaysFromRange(String inputStartDate, String inputEndDate){
        long startDate = TimeUtils.getDateFromStr(inputStartDate);
        long endDate = TimeUtils.getDateFromStr(inputEndDate);
        if (startDate == -1 || endDate == -1)
            return null;
        else
            return this.dbWrapper.getDaysByRange((int) startDate, (int) endDate);
    }

    /**
     *
     * @return
     */
    public List<String> getAllCatNames(){
        return dbWrapper.getAllCatNames();
    }

    /**
     *
     * @return
     */
    public List<String> getAllTaskNames(){
        return dbWrapper.getAllTaskName();
    }

    /**
     *
     * @return
     */
    public boolean isDbReady(){
        return dbWrapper.isReady();
    }
    //<---------------------------------------------------------------------------------------------
    //  Core - GUI updating
    // -------------------------------------------------------------------------------------------->

    /**
     *
     */
    public boolean isMonitoring(){
        return sharedPref.isMonitoring();
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

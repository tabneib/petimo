package de.tud.nhd.petimo.controller;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.util.Log;

import de.tud.nhd.petimo.model.PetimoModel;

/**
 * Created by nhd on 31.08.17.
 */

public class PetimoController {
    private static final String TAG = "PetimoController";
    private static PetimoController _instance;
    private PetimoModel model;

    //<---------------------------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------------------------->

    private PetimoController(Context context){
        try{
            PetimoModel.initialize(context);
            this.model = PetimoModel.getInstance();
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
            throw new Exception("PetimoModel is not yet initialized!");
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
        long returnCode = this.model.insertCategory(name, priority);
        // TODO update view: notify the user according to the return code
    }

    /**
     * TODO comment em
     * @param name
     * @param category
     * @param priority
     */
    public void addTask(String name, String category, int priority){
        long returnCode = this.model.insertTask(name, category, priority);
        // TODO update view: notify the user according to the return code
    }

    /**
     *
     * @param inputTask
     * @param inputCat
     * @param inputStart
     * @param inputEnd
     * @param inputDate
     */
    public void addBlockManually(String inputTask, String inputCat,
                                 String inputStart, String inputEnd, String inputDate){
        if (getTimeFromStr(inputStart, inputDate) == -1){
            // TODO update view: notify user about invalid input time
            return;
        }
        if (getTimeFromStr(inputEnd, inputDate) == -1){
            // TODO update view: notify user about invalid input time
            return;
        }
        if (getDateFromStr(inputDate) == -1){
            // TODO update view: notify user about invalid input time
            return;
        }
        int start = (int) getTimeFromStr(inputStart, inputDate);
        int end = (int) getTimeFromStr(inputEnd, inputDate);
        int date = (int) getDateFromStr(inputDate);

        long returnCode = this.model.insertMonitorBlock(
                inputTask, inputCat, start, end, end - start, date, getWeekDay(date),
                checkOverNight(date, start, end));
        // TODO update view: notify the user according to the return code
    }
    //<---------------------------------------------------------------------------------------------
    //  Auxiliary
    // -------------------------------------------------------------------------------------------->

    /**
     *
     * @param date
     * @return
     */
    private long getDateFromStr(String date){
        //TODO implement me
        return Integer.parseInt(date);
    }

    /**
     * Return the time as number of minutes from the start of the given day.
     * @param time
     * @param date
     * @return
     */
    private long getTimeFromStr(String time, String date){
        //TODO implement me
        return 0;
    }
    /**
     *
     * @param date
     * @return
     */
    private int getWeekDay(int date){
        // TODO implement me. For now it's always sunday :)
        return 8;
    }

    /**
     *
     * @param date
     * @param start
     * @param end
     * @return
     */
    private int checkOverNight(int date, int start, int end){
        // TODO implement me. For now never overnight, good boy :)
        return 0;
    }
}

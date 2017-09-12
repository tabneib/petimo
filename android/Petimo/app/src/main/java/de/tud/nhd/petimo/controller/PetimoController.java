package de.tud.nhd.petimo.controller;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.controller.exception.InvalidInputDateException;
import de.tud.nhd.petimo.controller.exception.InvalidInputNameException;
import de.tud.nhd.petimo.controller.exception.InvalidInputTimeException;
import de.tud.nhd.petimo.controller.exception.InvalidTimeException;
import de.tud.nhd.petimo.model.MonitorBlock;
import de.tud.nhd.petimo.model.MonitorCategory;
import de.tud.nhd.petimo.model.MonitorDay;
import de.tud.nhd.petimo.model.MonitorTask;
import de.tud.nhd.petimo.model.PetimoDbWrapper;
import de.tud.nhd.petimo.model.PetimoSharedPref;
import de.tud.nhd.petimo.utils.TimeUtils;

/**
 * Created by nhd on 31.08.17.
 */

public class PetimoController {

    private static final String TAG = "PetimoController";
    private static PetimoController _instance;
    private PetimoDbWrapper dbWrapper;
    private PetimoSharedPref sharedPref;
    private static Context context;

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

    /**
     *
     * @param kontext
     */
    public static void setContext(Context kontext){
        context = kontext;
    }

    public static PetimoController getInstance(){
        if (_instance == null){
            _instance = new PetimoController(context);
            return _instance;
        }
        else
            return _instance;
    }

    /*
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
    */

    //<---------------------------------------------------------------------------------------------
    //  Core - Inputting
    // -------------------------------------------------------------------------------------------->

    /**
     * TODO comment em
     * @param name
     * @param priority
     * @return The corresponding response code
     */
    public ResponseCode addCategory(String name, int priority)
            throws DbErrorException, InvalidCategoryException, InvalidInputNameException {
        return this.dbWrapper.insertCategory(name, priority);
    }

    /**
     * TODO comment em
     * @param name
     * @param category
     * @param priority
     */
    public ResponseCode addTask(String name, String category, int priority)
            throws DbErrorException, InvalidCategoryException, InvalidInputNameException {
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
    public ResponseCode addBlockManually(
            String inputTask, String inputCat, String inputStart, String inputEnd, String inputDate)
            throws DbErrorException, InvalidInputDateException,
            InvalidInputTimeException, InvalidTimeException, InvalidCategoryException {
        if (TimeUtils.getDateFromStr(inputDate) == -1)
            throw new InvalidInputDateException("The input date is invalid: " + inputDate);
        long start = TimeUtils.getMsTimeFromStr(inputStart, inputDate);
        if (start == -1)
            throw new InvalidInputTimeException("The input start time is invalid: " + inputStart);
        long end = TimeUtils.getMsTimeFromStr(inputEnd, inputDate);
        if (end == -1)
            throw new InvalidInputTimeException("The input end time is invalid: " + inputEnd);
        if (end <= start)
            throw new InvalidTimeException(
                    "End time lays before start time: " + inputEnd + " < " + inputStart);
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
    public ResponseCode addBlockLive(String inputCat, String inputTask)
            throws DbErrorException, InvalidCategoryException {
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
            String cat = sharedPref.getMonitorCat();
            String task = sharedPref.getMonitorTask();
            sharedPref.clearLiveMonitor();
            return this.dbWrapper.insertMonitorBlock(task, cat,start, end, end - start,
                    date, TimeUtils.getWeekDay(date), isOverNight(date, start, end));
        }
    }

    /**
     * Remove a monitor task
     * @param taskName
     * @param catName
     * @return
     */
    public int removeTask(String taskName, String catName){
        return this.dbWrapper.removeTask(taskName, catName);
    }

    /**
     * Remove a monitor catefory
     * @param catName
     * @return
     */
    public int removeCategory(String catName){
        return this.dbWrapper.removeCategory(catName);
    }

    /**
     * Remove a monitor block
     * @param id id of the block to be removed
     * @return
     */
    public int removeBlock(int id){
        return this.dbWrapper.removeBlockById(id);
    }


    /**
     * Update the saved list of monitored tasks.
     * This must be called just before stopping the monitor
     */
    public void updateMonitoredTaskList(){
        this.sharedPref.updateMonitoredTask(this.sharedPref.getMonitorCat(),
                this.sharedPref.getMonitorTask(), System.currentTimeMillis());
    }

    /**
     * update the last monitored cat/task to the current one
     */
    public void updateLastMonitored(){
        this.sharedPref.setLastMonitored(sharedPref.getMonitorCat(), sharedPref.getMonitorTask());
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
     * @param startDate
     * @param endDate
     * @param displayEmptyDay
     * @return the list of all satisfied monitor days in DESC order
     */
    public ArrayList<MonitorDay> getDaysFromRange(int startDate, int endDate, boolean displayEmptyDay){

        if(!displayEmptyDay)
            // The list will not contain empty days
            return this.dbWrapper.getDaysByRange(startDate, endDate);
        else {
            ArrayList<MonitorDay> dayList =
                    (ArrayList<MonitorDay>) this.dbWrapper.getDaysByRange(startDate, endDate);
            ArrayList<MonitorDay> resultList = new ArrayList<>();
            Log.d(TAG, "dayList size =====> " + dayList.size());
            for (int day = endDate; day >= startDate; day--) {
                Log.d(TAG, "day ====> " + day);
                if(!dayList.isEmpty() && dayList.get(0).getDate() == day) {
                    // Insert the Monitor Day returned from DB
                    Log.d(TAG, "Day/BlockList.size  ====> " + dayList.get(0).getDate() +
                    " / " + dayList.get(0).getMonitorBlocks().size());
                    resultList.add(dayList.get(0));
                    dayList.remove(0);
                }
                else
                    // Insert a MonitorDay with empty block list
                    resultList.add(new MonitorDay(day, new ArrayList<MonitorBlock>()));
            }
            return resultList;
        }
    }

    /**
     *
     * @param inputStartDate
     * @param inputEndDate
     * @return the list of all satisfied monitor blocks, or null if the inputs are invalid
     */
    public List<MonitorBlock> getBlocksFromRange(String inputStartDate, String inputEndDate){
        long startDate = TimeUtils.getDateFromStr(inputStartDate);
        long endDate = TimeUtils.getDateFromStr(inputEndDate);
        if (startDate == -1 || endDate == -1)
            return null;
        else
            return this.dbWrapper.getBlocksByRange((int) startDate, (int) endDate);
    }

    /**
     * Get a list of all categories
     * @return the list of {@link MonitorCategory} objects
     */
    public List<MonitorCategory> getAllCats(){
        return dbWrapper.getAllCategories();
    }

    /**
     * Return all tasks the belong to the given category
     * @param cat name of the category
     * @return  the list of all corresponding tasks
     */
    public List<MonitorTask> getAllTasks(String cat){
        return dbWrapper.getTasksByCat(cat);
    }

    /**
     *
     * @param catName
     * @return
     */
    public List<String> getTaskNameByCat(String catName){
        return dbWrapper.getTaskNamesByCat(catName);
    }
    /**
     *
     * @param startDate
     * @param endDate
     * @return the list of all satisfied monitor blocks
     */
    public List<MonitorBlock> getBlocksFromRange(int startDate, int endDate){
        return this.dbWrapper.getBlocksByRange(startDate, endDate);
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
     * @param catName
     * @return
     */
    public MonitorCategory getCatByName(String catName){
        Log.d(TAG, "is about to getCatByName ====> " + catName);
        return dbWrapper.getCatByName(catName);
    }

    /**
     *
     * @param taskName
     * @param catName
     * @return
     */
    public MonitorTask getTaskByName(String taskName, String catName){
        return dbWrapper.getTaskByName(taskName, catName);
    }
    /**
     *
     * @return
     */
    public List<String> getAllTaskNames(){
        return dbWrapper.getAllTaskName();
    }


    /**
     * Return a string of size 4 containing all information of the ongoing monitor.
     * The information includes: Category, Task, Date, Start time
     * @return  the string, or null if there is no ongoing monitor
     */
    public String[] getLiveMonitorInfo(){
        if (!isMonitoring())
            return null;
        else
            return new String[] {sharedPref.getMonitorCat(), sharedPref.getMonitorTask(),
                    TimeUtils.getDateStrFromInt(sharedPref.getMonitorDate()),
                    TimeUtils.getDayTimeFromMsTime(sharedPref.getMonitorStart())};
    }


    /**
     *
     * @return
     */
    public ArrayList<String[]> getMonitoredTasks(){
        return sharedPref.getMonitored(sharedPref.getUsrMonitoredSortOrder());
    }

    /**
     * Calculate the position of the last monitored cat/task
     * @return
     */
    public int[] getLastMonitoredTask(){
        String[] lastCatTask = sharedPref.getLastMonitoredTask();
        if (lastCatTask[0] != null && lastCatTask[1] != null) {
            return new int[]{
                    getAllCatNames().indexOf(lastCatTask[0]),
                    getTaskNameByCat(lastCatTask[0]).indexOf(lastCatTask[1])
            };
        }
        else
            // no last monitored cat/task saved, return the first position
            return new int[]{0,0};
    }

    /**
     *
     * @return
     */
    public boolean isDbReady(){
        return dbWrapper.isReady();
    }


    //<---------------------------------------------------------------------------------------------
    //  Auxiliary
    // -------------------------------------------------------------------------------------------->

    /**
     *
     */
    public boolean isMonitoring(){
        return sharedPref.isMonitoring();
    }

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
                TimeUtils.getMsTimeFromStr(start, date), TimeUtils.getMsTimeFromStr(end,date));
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

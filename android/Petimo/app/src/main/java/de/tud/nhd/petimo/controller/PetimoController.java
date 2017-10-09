package de.tud.nhd.petimo.controller;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.controller.exception.InvalidInputNameException;
import de.tud.nhd.petimo.controller.exception.InvalidInputTimeException;
import de.tud.nhd.petimo.controller.exception.InvalidTimeException;
import de.tud.nhd.petimo.model.db.MonitorBlock;
import de.tud.nhd.petimo.model.db.MonitorCategory;
import de.tud.nhd.petimo.model.db.MonitorDay;
import de.tud.nhd.petimo.model.db.MonitorTask;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.model.sharedpref.PetimoMonitorSPref;
import de.tud.nhd.petimo.model.sharedpref.PetimoSPref;
import de.tud.nhd.petimo.model.sharedpref.PetimoSettingsSPref;
import de.tud.nhd.petimo.model.sharedpref.SharedPref;
import de.tud.nhd.petimo.model.sharedpref.TaskSelector;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.model.sharedpref.PetimoSPref.Consts;

/**
 * Created by nhd on 31.08.17.
 */

public class PetimoController {

    private static final String TAG = "PetimoController";
    private static PetimoController _instance;
    private PetimoDbWrapper dbWrapper;
    private SharedPref sharedPref;
    private PetimoSettingsSPref settingsPref;
    private PetimoMonitorSPref monitorPref;
    private TaskSelector taskSelector;
    private static Context context;



    private HashMap<String, Boolean> tags = new HashMap<>();
    //<---------------------------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------------------------->

    private PetimoController(Context context){
        try{
            PetimoDbWrapper.setContext(context);
            SharedPref.initialize(context);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        this.dbWrapper = PetimoDbWrapper.getInstance();
        this.sharedPref = SharedPref.getInstance();
        PetimoSPref.initialize(context);
        this.settingsPref = PetimoSettingsSPref.getInstance();
        this.monitorPref = PetimoMonitorSPref.getInstance();
        this.taskSelector = TaskSelector.getInstance();


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

    //<---------------------------------------------------------------------------------------------
    //  Core - Inputting
    // -------------------------------------------------------------------------------------------->

    /**
     * @param name
     * @param priority
     * @return The corresponding response code
     */
    public ResponseCode addCategory(String name, int priority, String note)
            throws DbErrorException, InvalidCategoryException, InvalidInputNameException {
        return this.dbWrapper.insertCategory(name, priority, MonitorCategory.ACTIVE, -1, note);
    }

    /**
     * TODO comment em
     * @param name
     * @param catId
     * @param priority
     */
    public ResponseCode addTask(String name, int catId, int priority, String note)
            throws DbErrorException, InvalidCategoryException, InvalidInputNameException {
        return this.dbWrapper.insertTask(name, catId, priority, MonitorTask.ACTIVE, -1, note);
    }

    /**
     * TODO: Check for invalid information - Time in the future; Time conflicts with other blocks
     * @param catId
     * @param taskId
     * @param start
     * @param end
     * @param date
     * @param note
     * @return
     * @throws DbErrorException
     * @throws InvalidInputTimeException
     * @throws InvalidTimeException
     * @throws InvalidCategoryException
     */
    public ResponseCode addBlockManually(
            int catId, int taskId, long start, long end, int date, String note)
            throws DbErrorException, InvalidInputTimeException,
            InvalidTimeException, InvalidCategoryException {
        if (end <= start)
            throw new InvalidTimeException(
                    "End time lays before start time: " + end + " < " + start);
        return this.dbWrapper.insertMonitorBlock(
                taskId, catId, start, end, end - start, date, PetimoTimeUtils.getWeekDay(date),
                isOverNight(date, start, end), sharedPref.getOvThreshold(), MonitorBlock.ACTIVE,
                note);
    }

    public ResponseCode addBlockManually(
            String catName, String taskName, long start, long end, int date, String note)
            throws DbErrorException, InvalidInputTimeException,
            InvalidTimeException, InvalidCategoryException {
        return addBlockManually(dbWrapper.getCatIdFromName(catName),
                dbWrapper.getTaskIdFromName(taskName, dbWrapper.getCatIdFromName(catName)),
                start, end, date, note);
    }
    /**
     *
     * We assume that the input category, task are correct because the user has to choose them from
     * a drop down menu.
     * Database version: V.2
     * @param catId
     * @param taskId
     * @return the corresponding response code
     */
    public ResponseCode monitor(
            int catId, int taskId, long startTime, long stopTime)
            throws DbErrorException, InvalidCategoryException {
        //Date current = new Date();
        if (!sharedPref.isMonitoring()){
            // Case there is no ongoing monitor
            //sharedPref.setLiveMonitor(inputCat, inputTask, getLiveDate(current), current.getTime());
            sharedPref.setLiveMonitor(catId, taskId,
                    getDateFromMillis(startTime, sharedPref.getOvThreshold()), startTime);
            return ResponseCode.OK;
        }
        else{
            // Case there's an ongoing monitor
            // In this case all given arguments are ignored.
            // TODO Check for startDate and end date !
            // TODO If the monitor go to another day so add multiple blocks !
            long start = sharedPref.getMonitorStart();
            int date = sharedPref.getMonitorDate();
            catId = sharedPref.getMonitorCatId();
            taskId = sharedPref.getMonitorTaskId();
            sharedPref.clearLiveMonitor();
            ResponseCode rCode =  this.dbWrapper.insertMonitorBlock(
                    taskId, catId, start, stopTime, stopTime - start,
                    date, PetimoTimeUtils.getWeekDay(date), isOverNight(date, start, stopTime),
                    sharedPref.getSettingsInt(SharedPref.SETTINGS_OVERNIGHT_THRESHOLD, 6),
                    MonitorBlock.ACTIVE, "");
            return rCode;
        }
    }

    /**
     * Update the saved list of monitored tasks.
     * This must be called just before stopping the monitor
     */
    public void updateMonitoredTaskList(){
        this.sharedPref.updateMonitoredTask(this.sharedPref.getMonitorCatId(),
                this.sharedPref.getMonitorTaskId(), System.currentTimeMillis());
    }

    /**
     * update the last monitored cat/task to the current one
     */
    public void updateLastMonitored(){
        this.sharedPref.setLastMonitored(
                sharedPref.getMonitorCatId(), sharedPref.getMonitorTaskId());
    }

    /**
     * update the last monitored cat/task to the given one
     */
    public void updateLastMonitored(int catId, int taskId){
        this.sharedPref.setLastMonitored(catId, taskId);
    }

    /**
     * Update the chosen sort order for displaying monitored tasks
     * @param sortOrder
     */
    public void updateUsrMonitoredTasksSortOrder(String sortOrder){
        switch (sortOrder){
            case SharedPref.FREQUENCY:
                sharedPref.setUsrMonitoredSortOrder(SharedPref.FREQUENCY);
                break;
            default:
                sharedPref.setUsrMonitoredSortOrder(SharedPref.TIME);
        }
    }
    //<---------------------------------------------------------------------------------------------
    //  Core - Outputting
    // -------------------------------------------------------------------------------------------->

    /**
     *
     * @param startDate
     * @param endDate
     * @param displayEmptyDay
     * @return the list of all satisfied monitor days in DESC order
     */
    public ArrayList<MonitorDay> getDaysFromRange(
            String mode, Calendar startDate, Calendar endDate,
            boolean displayEmptyDay, boolean showSelectedTasks){

        int startDateInt = PetimoTimeUtils.getDateIntFromCalendar(startDate);
        int endDateInt = PetimoTimeUtils.getDateIntFromCalendar(endDate);

        ArrayList<Integer> selectedTasks = new ArrayList<>();
        if (showSelectedTasks)
            selectedTasks = taskSelector.getSelectedTasks(mode);
        else
            selectedTasks = null;

        ArrayList<MonitorDay> dayList =
                this.dbWrapper.getDaysByRange(startDateInt, endDateInt, selectedTasks);

        ArrayList<MonitorDay> resultList = new ArrayList<>();
        switch (mode){
            case Consts.EDIT_BLOCK:
                if(!displayEmptyDay)
                    // The list will not contain empty days
                    return dayList;
            case Consts.STATISTICS:
                ArrayList<Integer> dates =
                        PetimoTimeUtils.getDateIntFromRange(startDate, endDate);
                // Reverse order
                ListIterator<Integer> iterator = dates.listIterator(dates.size());
                while (iterator.hasPrevious()){
                    int day = iterator.previous();
                    if(!dayList.isEmpty() && dayList.get(0).getDate() == day) {
                        // Insert the Monitor Day returned from DB
                        resultList.add(dayList.get(0));
                        dayList.remove(0);
                    }
                    else
                        // Insert a MonitorDay with empty block list
                        resultList.add(new MonitorDay(day, new ArrayList<MonitorBlock>()));
                }
        }
        return resultList;
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @param displayEmptyDay
     * @return the list of all satisfied monitor days in DESC order
     */
    public ArrayList<MonitorDay> getDaysFromRange(
            String mode, int startDate, int endDate,
            boolean displayEmptyDay, boolean showSelectedTasks){
        return getDaysFromRange(mode, PetimoTimeUtils.getCalendarFromDateInt(startDate),
                PetimoTimeUtils.getCalendarFromDateInt(PetimoTimeUtils.getTodayDate()),
                displayEmptyDay, showSelectedTasks);
    }

    /**
     *
     * @param inputStartDate
     * @param inputEndDate
     * @return the list of all satisfied monitor blocks, or null if the inputs are invalid
     */
    // unused
    public List<MonitorBlock> getBlocksFromRange(String inputStartDate, String inputEndDate){
        long startDate = PetimoTimeUtils.getDateFromStr(inputStartDate);
        long endDate = PetimoTimeUtils.getDateFromStr(inputEndDate);
        if (startDate == -1 || endDate == -1)
            return null;
        else
            return this.dbWrapper.getBlocksByRange((int) startDate, (int) endDate);
    }

    /**
     * Return a string of size 5 containing all information of the ongoing monitor.
     * The information includes: Category, Task, Date, Start time in HH:MM, Start time in millis
     * @return  the string, or null if there is no ongoing monitor
     */
    public String[] getLiveMonitorInfo(){
        if (!sharedPref.isMonitoring())
            return null;
        else
            return new String[] {
                    PetimoDbWrapper.getInstance().getCatNameById(sharedPref.getMonitorCatId()),
                    PetimoDbWrapper.getInstance().getTaskNameById(sharedPref.getMonitorTaskId()),
                    PetimoTimeUtils.getDateStrFromInt(sharedPref.getMonitorDate()),
                    PetimoTimeUtils.getDayTimeFromMsTime(sharedPref.getMonitorStart()),
                    Long.toString(sharedPref.getMonitorStart())};
    }

    /**
     *
     * @return
     */
    public ArrayList<String[]> getMonitoredTasks(){
        ArrayList<String[]> monitoredTasks =
                sharedPref.getMonitored(sharedPref.getUsrMonitoredSortOrder());
        if (monitoredTasks == null)
            return new ArrayList<>();
        else
            return monitoredTasks;
    }

    /**
     * Calculate the position of the last monitored cat/task
     * Database version: V.2
     * @return
     */
    public int[] getLastMonitoredTask(){
        int[] lastCatTask = sharedPref.getLastMonitoredTask();
        if (lastCatTask[0] != -1 && lastCatTask[1] != -1) {
            return new int[]{
                    dbWrapper.getAllCatIds().indexOf(lastCatTask[0]),
                    dbWrapper.getTaskIdsByCat(lastCatTask[0]).indexOf(lastCatTask[1])
            };
        }
        else
            // no last monitored cat/task saved, return the first position
            return new int[]{0,0};
    }

    //<---------------------------------------------------------------------------------------------
    //  Auxiliary
    // -------------------------------------------------------------------------------------------->

    /**
     * Determine the monitor date according to the given time.
     * The monitor date is the day before if the given time is between midnight and the
     * overnight threshold.
     * @param time the time in milliseconds
     * @param ovThreshold overnight threshold
     * @return the monitor date as an integer
     */
    private int getDateFromMillis(long time, int ovThreshold){
        Date date = new Date(time);
        int dateInt = PetimoTimeUtils.getDateIntFromDate(date);
        int hours = PetimoTimeUtils.getHourFromDate(date);
        if (hours < ovThreshold)
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
                PetimoTimeUtils.getMsTimeFromStr(start, date),
                PetimoTimeUtils.getMsTimeFromStr(end,date));
    }

    /**
     * TODO Implement me !
     * @param date
     * @param start
     * @param end
     * @return
     */
    public int isOverNight(int date, long start, long end){
        return sharedPref.getOvThreshold();
    }

    /**
     * Check if the given time is a valid start time
     * @param hour
     * @param minute
     * @return
     */
    public boolean checkValidLiveStartTime(int hour, int minute){
        int currentHour = PetimoTimeUtils.getCurrentHour();
        int currentMinute = PetimoTimeUtils.getCurrentMinute();
        long startTimeMillis = PetimoTimeUtils.getTimeMillisFromHM(hour, minute);
        // Check if the chosen start time is not equal or before the last stop time
        // Bug: If this is the first monitor of the day, dbWrapper.getBlocksByRange() will return
        // an empty list
        List<MonitorBlock> todayBlocks = dbWrapper.getBlocksByRange(
                PetimoTimeUtils.getTodayDate(), PetimoTimeUtils.getTodayDate());
        if (todayBlocks != null && !todayBlocks.isEmpty()
                && startTimeMillis <= todayBlocks.get(0).getEnd())
            return false;

        currentHour = currentHour < sharedPref.getOvThreshold() ? currentHour + 24 : currentHour;
        hour = hour < sharedPref.getOvThreshold() ? hour + 24 : hour;

        if (currentHour > hour)
            return true;
        else if (currentHour == hour){
            if (currentMinute < minute)
                return false;
            return true;
        }
        return false;
    }


    /**
     * Check if the given manual time is a valid start time
     * @param hour
     * @param minute
     * @return
     */
    public boolean checkValidManualStartTime(int date, int hour, int minute){
        return checkValidManualTime(date, hour, minute);
    }

    /**
     *
     * Check if the given time is a valid stop time according to the current saved start time
     * @param hour
     * @param minute
     * @return
     */
    public boolean checkValidLiveStopTime(int hour, int minute){
        return checkValidLiveStopTime(hour, minute, sharedPref.getMonitorStart());
    }


    /**
     *
     * Check if the given time is a valid stop time
     * @param hour
     * @param minute
     * @return
     */
    public boolean checkValidLiveStopTime(int hour, int minute, long startTimeMillis){

        // Check if the stop time is not before or equal start time
        long stopTimeMillis = PetimoTimeUtils.getTimeMillisFromHM(hour, minute);
        if (stopTimeMillis <= startTimeMillis)
            return false;

        int currentHour = PetimoTimeUtils.getCurrentHour();
        int currentMinute = PetimoTimeUtils.getCurrentMinute();

        // change the format of hours to 24+
        currentHour = currentHour < sharedPref.getOvThreshold() ? currentHour + 24 : currentHour;
        hour = hour < sharedPref.getOvThreshold() ? hour + 24 : hour;

        // Check if the stop time is not in the future
        if (currentHour < hour)
            return false;
        else if ((currentHour == hour) && (currentMinute < minute))
            return false;
        return true;
    }

    /**
     *
     * @param hour
     * @param minute
     * @param startTimeMillis
     * @return
     */
    public boolean checkValidManualStopTime(int date, int hour, int minute, long startTimeMillis){

        long stopTimeMillis = PetimoTimeUtils.getTimeMillisFromHM(date, hour, minute);
        // First, trivially, it has to be after the given start time
        if (stopTimeMillis <= startTimeMillis)
            return false;

        // Second, it has to be a valid time
        if (!checkValidManualTime(date, hour, minute))
            return false;

        // Third, there must be no monitored time interval between it and the given start time
        List<MonitorBlock> todayBlocks = dbWrapper.getBlocksByRange(date, date);
        if (todayBlocks == null || todayBlocks.isEmpty())
            // If there is not yet any monitor block today, so any given time is valid
            return true;
        for (MonitorBlock block : todayBlocks)
            if (stopTimeMillis >= block.getStart() && startTimeMillis <= block.getStart())
                // If there is some monitored start time laying between it and the given start time,
                // so it is invalid
                return false;
        return true;
    }


    /**
     * Check if the given time is a valid time. This means it does not lay in any already monitored
     * time interval
     * @param hour
     * @param minute
     * @return
     */
    public boolean checkValidManualTime(int date, int hour, int minute){

        Log.d(TAG, "checkValidManualTime: date/hour/minute ===> " + date + " / "+ hour + " / " + minute);
        long startTimeMillis = PetimoTimeUtils.getTimeMillisFromHM(date, hour, minute);
        // Check if the chosen time is not between any pair of existed start/stop time
        List<MonitorBlock> todayBlocks = dbWrapper.getBlocksByRange(date, date);
        Log.d(TAG, "checkValidManualTime: todayBlocks size ===> " + todayBlocks.size());
        for (MonitorBlock block : todayBlocks)
            Log.d(TAG, block.toXml(0));
        if (todayBlocks == null || todayBlocks.isEmpty())
            // If there is not yet any monitor block today, so any given time is valid
            return true;
        for (MonitorBlock block : todayBlocks) {
            if (startTimeMillis >= block.getStart() && startTimeMillis <= block.getEnd())
                // If the given time lays between any pair of start/stop time, so it is invalid
                return false;
        }
        return true;
    }

    /**
     *
     * @param lang
     * @return
     */
    public int getLangId(String lang){
        switch (lang){
            case SharedPref.LANG_EN:
            case SharedPref.LANG_DE:
            case SharedPref.LANG_VI:
                return SharedPref.LANGUAGES.indexOf(lang);
            default:
                return SharedPref.LANGUAGES.indexOf(SharedPref.LANG_EN);
        }
    }

    public String getLangFromId(int id){
        if (id < SharedPref.LANGUAGES.size() && id >= 0)
            return SharedPref.LANGUAGES.get(id);
        else
            return SharedPref.LANG_EN;
    }


    /**
     * Used by view to set controlling tags
     * @param tag
     * @param content
     */
    public void setTag(String tag, boolean content){
        tags.put(tag, content);
    }

    /**
     * used by view to get controlling tags
     * @param tag
     * @param defaultValue
     * @return
     */
    public boolean getTag(String tag, boolean defaultValue){
        if (tags.containsKey(tag))
            return tags.get(tag);
        else
            return defaultValue;
    }

}

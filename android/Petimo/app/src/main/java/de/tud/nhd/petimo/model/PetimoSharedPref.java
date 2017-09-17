package de.tud.nhd.petimo.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.utils.StringParsingException;
import de.tud.nhd.petimo.utils.PetimoStringUtils;

/**
 * Created by nhd on 01.09.17.
 */

public class PetimoSharedPref {

    private static final String TAG = "PetimoSharedPref";
    private final String MONITOR_LIVE_DATE =
            "de.tud.nhd.petimo.model.PetimoSharedPref.MONITOR_LIVE_DATE";
    private final String MONITOR_LIVE_START =
            "de.tud.nhd.petimo.model.PetimoSharedPref.MONITOR_LIVE_START";
    private final String MONITOR_LIVE_CAT =
            "de.tud.nhd.petimo.model.PetimoSharedPref.MONITOR_LIVE_CAT";
    private final String MONITOR_LIVE_TASK =
            "de.tud.nhd.petimo.model.PetimoSharedPref.MONITOR_LIVE_TASK";
    private final String MONITOR_MONITORED_TASKS =
            "de.tud.nhd.petimo.model.PetimoSharedPref.MONITOR_MONITORED_TASKS";
    private final String MONITOR_LAST_MONITORED_CATEGORY =
            "de.tud.nhd.petimo.model.PetimoSharedPref.MONITOR_LAST_MONITORED_CATEGORY";
    private final String MONITOR_LAST_MONITORED_TASK =
            "de.tud.nhd.petimo.model.PetimoSharedPref.MONITOR_LAST_MONITORED_TASK";

    //------------------------------- User's choices ---------------------------------------------->
    public static final String SETTINGS_MONITORED_TASKS_SORT_ORDER =
            "de.tud.nhd.petimo.model.PetimoSharedPref.SETTINGS_MONITORED_TASKS_SORT_ORDER";

    public static final String SETTINGS_MONITORED_BLOCKS_REMEMBER =
            "de.tud.nhd.petimo.model.PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_REMEMBER";
    public static final String SETTINGS_MONITORED_BLOCKS_LOCK =
            "de.tud.nhd.petimo.model.PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_LOCK";
    public static final String SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS =
            "de.tud.nhd.petimo.model.PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS";
    public static final String SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS =
            "de.tud.nhd.petimo.model.PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS";

    public static final String SETTINGS_OVERNIGHT_THRESHOLD =
            "de.tud.nhd.petimo.model.PetimoSharedPref.SETTINGS_OVERNIGHT_THRESHOLD";

    private final int DEFAULT_OVERNIGHT_THRESHOLD = 6;


    private static PetimoSharedPref _instance;
    private static Context context;

    private SharedPreferences settingPref;
    private SharedPreferences monitorPref;
    private SharedPreferences.Editor settingsEditor;
    private SharedPreferences.Editor monitorEditor;

    public static final String TIME = "time";
    public static final String FREQUENCY = "frequency";
    public static final String NONE = "none";
    //<---------------------------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------------------------->

    private PetimoSharedPref(Context c){
        this.context = c;
        // TODO get monitor data from shared preferences
        this.settingPref = this.context.getSharedPreferences(
                this.context.getString(R.string.preference_file_settings), Context.MODE_PRIVATE);
        this.monitorPref = this.context.getSharedPreferences(
                this.context.getString(R.string.preference_file_monitor), Context.MODE_PRIVATE);
        this.settingsEditor = settingPref.edit();
        this.monitorEditor = monitorPref.edit();
    }

    public static void initialize(Context kontext) throws Exception{
        if(_instance != null)
            throw new Exception("Cannot initialize multiple instances of PetimoSharedPref!");
        else {
            context = kontext;
            Log.d(TAG, "Initialized!");
        }
    }

    public static PetimoSharedPref getInstance() {
        if (_instance == null) {
            _instance = new PetimoSharedPref(context);
            return _instance;
        }
        else
            return _instance;
    }

    //<---------------------------------------------------------------------------------------------
    // Monitor Preferences
    // -------------------------------------------------------------------------------------------->

    //------------------------------------------ Write -------------------------------------------->
    /**
     * Save information about the ongoing monitor into the preferences
     * @param category  category of the ongoing monitor
     * @param task      task of the ongoing monitor
     * @param date      date of the ongoing monitor
     * @param start     start of the ongoing monitor
     */
    public void setLiveMonitor(String category, String task, int date, long start){
        monitorEditor.putString(MONITOR_LIVE_CAT, category);
        monitorEditor.putString(MONITOR_LIVE_TASK, task);
        monitorEditor.putInt(MONITOR_LIVE_DATE, date);
        monitorEditor.putLong(MONITOR_LIVE_START, start);
        monitorEditor.apply();
    }

    /**
     * Clear all saved preferences about the ongoing live monitor
     */
    public void clearLiveMonitor(){
        monitorEditor.remove(MONITOR_LIVE_CAT);
        monitorEditor.remove(MONITOR_LIVE_TASK);
        monitorEditor.remove(MONITOR_LIVE_DATE);
        monitorEditor.remove(MONITOR_LIVE_START);
        monitorEditor.apply();
    }

    /**
     * Save or update information about monitored task
     * @param category  the category of the given monitored task
     * @param task  the
     */
    public void updateMonitoredTask(String category, String task, long time){
        ArrayList<String[]> monitoredTasks = this.getMonitored(this.NONE);
        boolean notExist = true;
        if (monitoredTasks!=null){
            for (String[] item : monitoredTasks){
                if(item[0].equals(category) && item[1].equals(task)){
                    // update time
                    item[2] = Long.toHexString(time);
                    // update frequency;
                    item[3] = Integer.toString(Integer.parseInt(item[3]) + 1);
                    notExist = false;
                    break;
                }
            }
            if (notExist){
                // Add a new item to the monitored task list
                monitoredTasks.add(
                        new String[]{category, task, Long.toHexString(time), Integer.toString(1)});
            }
        }
        else{
            // initialize
            monitoredTasks = new ArrayList<>();
            monitoredTasks.add(
                    new String[]{category, task, Long.toHexString(time), Integer.toString(1)});
        }

        monitorEditor.putString(MONITOR_MONITORED_TASKS, PetimoStringUtils.encode(monitoredTasks, 4));
        monitorEditor.apply();
    }


    /**
     * Remove all saved monitored tasks
     */
    public void clearMonitoredTasks(){
        monitorEditor.remove(MONITOR_MONITORED_TASKS);
        monitorEditor.apply();
    }

    /**
     * Save the sort order chosen by user to display monitored task.
     * If the given value is NONE or invalid, the sort order is set to NONE
     * @param sortOrder the given sort order
     */
    public void setUsrMonitoredSortOrder(String sortOrder){
        switch (sortOrder){
            case FREQUENCY:
                settingsEditor.putString(SETTINGS_MONITORED_TASKS_SORT_ORDER, FREQUENCY);
                break;
            case TIME:
                settingsEditor.putString(SETTINGS_MONITORED_TASKS_SORT_ORDER, TIME);
                break;
            default:
                settingsEditor.putString(SETTINGS_MONITORED_TASKS_SORT_ORDER, NONE);
        }
        settingsEditor.apply();
    }

    /**
     * Stored the last monitored task
     * @param cat   category of the task
     * @param task  the task
     */
    public void setLastMonitored(String cat, String task){
        monitorEditor.putString(MONITOR_LAST_MONITORED_CATEGORY, cat);
        monitorEditor.putString(MONITOR_LAST_MONITORED_TASK, task);
        monitorEditor.apply();
    }



    //------------------------------------------- Read -------------------------------------------->

    /**
     * Return the category string of the ongoing monitor
     * @return the category string, or null if there is no ongoing monitor
     */
    public String getMonitorCat(){
        return monitorPref.getString(MONITOR_LIVE_CAT, null);
    }

    /**
     * Return the task string of the ongoing monitor
     * @return the task string, or null if there is no ongoing monitor
     */
    public String getMonitorTask(){
        return monitorPref.getString(MONITOR_LIVE_TASK, null);
    }

    /**
     * Return the date of the ongoing monitor
     * @return the date, or 0 if there is no ongoing monitor
     */
    public int getMonitorDate(){
        return monitorPref.getInt(MONITOR_LIVE_DATE, 0);
    }

    /**
     * Return the start time of the ongoing monitor
     * @return the start time, or 0 if there is no ongoing monitor
     */
    public long getMonitorStart(){
        return monitorPref.getLong(MONITOR_LIVE_START, 0);
    }

    /**
     * Check if there is an ongoing live monitor
     * @return true if there is an ongoing live monitor, false otherwise
     */
    public boolean isMonitoring(){
        return !(monitorPref.getLong(MONITOR_LIVE_START, -1) == -1);
    }

    /**
     * Return a list of all monitored tasks
     * @return  the list of monitored tasks, or null if there is no saved monitored task or
     *          some error occurs.
     *          Each item contains the category, the task, last monitored time, and the monitor
     *          frequency
     */
    public ArrayList<String[]> getMonitored(String sortOpt){

        try{
            ArrayList<String[]> monitoredTaskList = PetimoStringUtils.parse(
                    monitorPref.getString(MONITOR_MONITORED_TASKS, null), 4);
            // Remove all cat / task that don't exist anymore
            Iterator<String[]> monitoredTaskIterator = monitoredTaskList.iterator();
            while (monitoredTaskIterator.hasNext()) {
                String[] catTask = monitoredTaskIterator.next();

                // Check if the cat/task is not yet removed by the user
                if (!PetimoDbWrapper.getInstance().checkCatExists(catTask[0]) ||
                        !PetimoDbWrapper.getInstance().checkTaskExists(catTask[0], catTask[1])) {
                    monitoredTaskIterator.remove();
                    // TODO: consider to remove it from the saved list
                    //removeMonitoredTask(catTask[0], catTask[1]);
                }
            }
            switch (sortOpt){
                case TIME:
                    // DESC sort by time
                    Collections.sort(monitoredTaskList,
                            Collections.reverseOrder(new Comparator<String[]>() {
                        @Override
                        public int compare(String[] o1, String[] o2) {
                            if (Long.parseLong(o1[2],16) > Long.parseLong(o2[2], 16))
                                return 1;
                            else if (Long.parseLong(o1[2],16) < Long.parseLong(o2[2],16))
                                return -1;
                            else
                                return 0;
                        }
                    }));
                    return monitoredTaskList;

                case FREQUENCY:
                    // DESC sort by frequency
                    Collections.sort(monitoredTaskList,
                            Collections.reverseOrder(new Comparator<String[]>() {
                                @Override
                                public int compare(String[] o1, String[] o2) {
                                    if (Integer.parseInt(o1[3]) > Integer.parseInt(o2[3]))
                                        return 1;
                                    else if (Integer.parseInt(o1[3]) < Integer.parseInt(o2[3]))
                                        return -1;
                                    else
                                        return 0;
                                }
                            }));
                    return monitoredTaskList;
                default:
                    return monitoredTaskList;
            }
        }
        catch (StringParsingException e){
            return null;
        }
    }

    /**
     * Get the last monitored cat/task
     * @return a string array containing the category and task, null if no category/task is stored
     */
    public String[] getLastMonitoredTask(){
        String cat = monitorPref.getString(MONITOR_LAST_MONITORED_CATEGORY, null);
        String task = monitorPref.getString(MONITOR_LAST_MONITORED_TASK, null);

        if (cat != null && task != null && PetimoDbWrapper.getInstance().checkCatExists(cat) &&
                PetimoDbWrapper.getInstance().checkTaskExists(cat, task))
                return new String[]{cat, task};
        return new String[]{null, null};
    }


    /**
    * Get the sort order chosen by user for displaying monitored tasks
    * @return the sort order
    */
    public String getUsrMonitoredSortOrder(){
        switch (settingPref.getString(SETTINGS_MONITORED_TASKS_SORT_ORDER, NONE)){
            case FREQUENCY:
                return FREQUENCY;
            case TIME:
                return TIME;
            default:
                return TIME;
        }
    }

    //<---------------------------------------------------------------------------------------------
    // Settings Preferences
    // -------------------------------------------------------------------------------------------->

    //------------------------------------------- Write -------------------------------------------->

    /**
     *
     * @param tag
     * @param content
     */
    public void setSettingsInt(String tag, int content){
        switch (tag){
            case SETTINGS_OVERNIGHT_THRESHOLD:
                settingsEditor.putInt(tag, content);
                settingsEditor.apply();
                break;
            default:
                throw new SettingsException("setSettingsInt: Unknown settings tag ==> " + tag);
        }
    }

    /**
     *
     * @param tag
     * @param content
     */
    public void setSettingsString(String tag, String content){
        switch (tag){
            case SETTINGS_MONITORED_TASKS_SORT_ORDER:
                settingsEditor.putString(tag, content);
                settingsEditor.apply();
                break;
            default:
                throw new SettingsException("setSettingsString: Unknown settings tag ==> " + tag);
        }
    }

    /**
     *
     * @param tag
     * @param content
     */
    public void setSettingsBoolean(String tag, boolean content){
        switch (tag){
            case SETTINGS_MONITORED_BLOCKS_LOCK:
            case SETTINGS_MONITORED_BLOCKS_REMEMBER:
            case SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS:
            case SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS:
                settingsEditor.putBoolean(tag, content);
                settingsEditor.apply();
                break;
            default:
                throw new SettingsException("setSettingsBoolean: Unknown settings tag ==> " + tag);
        }
    }

    //------------------------------------------- Read -------------------------------------------->

    /**
     *
     * @param tag
     * @param defaultValue
     * @return
     */
    public int getSettingsInt(String tag, int defaultValue){
        switch (tag){
            case SETTINGS_OVERNIGHT_THRESHOLD:
                return settingPref.getInt(tag, defaultValue);
            default:
                throw new SettingsException("getSettingsInt: Unknown settings tag ==> " + tag);
        }
    }

    /**
     *
     * @param tag
     * @param defaultValue
     * @return
     */
    public String getSettingsString(String tag, String defaultValue){
        switch (tag){
            case SETTINGS_MONITORED_TASKS_SORT_ORDER:
                return settingPref.getString(tag, defaultValue);
            default:
                throw new SettingsException("getSettingsString: Unknown settings tag ==> " + tag);
        }
    }

    /**
     *
     * @param tag
     * @param defaultValue
     * @return
     */
    public boolean getSettingsBoolean(String tag, boolean defaultValue){
        switch (tag){
            case SETTINGS_MONITORED_BLOCKS_LOCK:
            case SETTINGS_MONITORED_BLOCKS_REMEMBER:
            case SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS:
            case SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS:
                return settingPref.getBoolean(tag, defaultValue);
            default:
                throw new SettingsException("getSettingsBoolean: Unknown settings tag ==> " + tag);
        }
    }

    /**
     * Get the overnight threshold
     * @return the threshold
     * TODO remove this method, use generic method above instead
     */
    public int getOvThreshold(){
        return settingPref.getInt(SETTINGS_OVERNIGHT_THRESHOLD, DEFAULT_OVERNIGHT_THRESHOLD);
    }
}

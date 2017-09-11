package de.tud.nhd.petimo.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.utils.StringParsingException;
import de.tud.nhd.petimo.utils.StringUtils;

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

    private final String SETTINGS_OVERNIGHT_THRESHOLD =
            "de.tud.nhd.petimo.model.PetimoSharedPref.SETTINGS_OVERNIGHT_THRESHOLD";

    private final int DEFAULT_OVERNIGHT_THRESHOLD = 6;


    private static PetimoSharedPref _instance;
    private Context context;

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

    public static void initialize(Context context) throws Exception{
        if(_instance != null)
            throw new Exception("Cannot initialize multiple instances of PetimoSharedPref!");
        else {
            _instance = new PetimoSharedPref(context);
            Log.d(TAG, "Initialized!");
        }
    }

    public static PetimoSharedPref getInstance() throws Exception{
        if (_instance == null)
            throw new Exception("PetimoSharedPref is not yet initialized!");
        else
            return _instance;
    }

    //<---------------------------------------------------------------------------------------------
    // Monitor Preferences
    // -------------------------------------------------------------------------------------------->

    // Write
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
            monitoredTasks = new ArrayList<String[]>();
            monitoredTasks.add(
                    new String[]{category, task, Long.toHexString(time), Integer.toString(1)});
        }

        monitorEditor.remove(MONITOR_MONITORED_TASKS);
        monitorEditor.putString(MONITOR_MONITORED_TASKS, StringUtils.encode(monitoredTasks, 4));
        monitorEditor.apply();
    }

    /**
     * Remove a task from the list of save monitored tasks
     * @param category
     * @param task
     * @return
     */
    public boolean removeMonitoredTask(String category, String task){
        ArrayList<String[]> monitoredTasks = this.getMonitored(this.NONE);
        if (monitoredTasks!=null) {
            for (String[] item : monitoredTasks) {
                if (item[0].equals(category) && item[1].equals(task)) {
                    monitoredTasks.remove(item);
                    return true;
                }
            }
        }
        return false;
    }
    // Read

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
     *          some error occurs
     */
    public ArrayList<String[]> getMonitored(String sortOpt){

        //monitorEditor.remove(MONITOR_MONITORED_TASKS).apply();
        try{
            ArrayList<String[]> monitoredTaskList = StringUtils.parse(
                    monitorPref.getString(MONITOR_MONITORED_TASKS, null), 4);
            switch (sortOpt){
                case TIME:
                    // DESC sort by time
                    Collections.sort(monitoredTaskList,
                            Collections.<String[]>reverseOrder(new Comparator<String[]>() {
                        @Override
                        public int compare(String[] o1, String[] o2) {
                            if (Long.parseLong(o1[2],16) > Long.parseLong(o2[2]))
                                return 1;
                            else if (Long.parseLong(o1[2],16) < Long.parseLong(o2[2]))
                                return -1;
                            else
                                return 0;
                        }
                    }));
                    return monitoredTaskList;

                case FREQUENCY:
                    // DESC sort by frequency
                    Collections.sort(monitoredTaskList,
                            Collections.<String[]>reverseOrder(new Comparator<String[]>() {
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
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    //<---------------------------------------------------------------------------------------------
    // Settings Preferences
    // -------------------------------------------------------------------------------------------->

    // Write


    // Read

    /**
     * Get the overnight threshold
     * @return the threshold
     */
    public int getOvThreshold(){
        return settingPref.getInt(SETTINGS_OVERNIGHT_THRESHOLD, DEFAULT_OVERNIGHT_THRESHOLD);
    }
}

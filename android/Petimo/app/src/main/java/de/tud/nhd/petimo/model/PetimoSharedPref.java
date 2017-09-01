package de.tud.nhd.petimo.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import de.tud.nhd.petimo.R;

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

    private final String SETTINGS_OVERNIGHT_THRESHOLD =
            "de.tud.nhd.petimo.model.PetimoSharedPref.SETTINGS_OVERNIGHT_THRESHOLD";

    private final int DEFAULT_OVERNIGHT_THRESHOLD = 6;


    private static PetimoSharedPref _instance;
    private Context context;

    private SharedPreferences settingPref;
    private SharedPreferences monitorPref;
    private SharedPreferences.Editor settingsEditor;
    private SharedPreferences.Editor monitorEditor;

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
    }

    /**
     * Clear all saved preferences about the ongoing live monitor
     */
    public void clearLiveMonitor(){
        monitorEditor.remove(MONITOR_LIVE_CAT);
        monitorEditor.remove(MONITOR_LIVE_TASK);
        monitorEditor.remove(MONITOR_LIVE_DATE);
        monitorEditor.remove(MONITOR_LIVE_START);
    }

    // Read
    /**
     * Return the start time string of the ongoing monitor
     * @return the start time string, or null if there is no ongoing monitor
     */
    public String getMonitorStart(){
        return monitorPref.getString(MONITOR_LIVE_START, null);
    }

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
     * Check if there is an ongoing live monitor
     * @return true if there is an ongoing live monitor, false otherwise
     */
    public boolean isMonitoring(){
        return !monitorPref.getString(MONITOR_LIVE_START, "NONE").equals("NONE");
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

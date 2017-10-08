package de.tud.nhd.petimo.model.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.SettingsException;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.utils.PetimoStringUtils;
import de.tud.nhd.petimo.utils.StringParsingException;

/**
 * Created by nhd on 08.10.17.
 */

public class SettingsSharedPref extends PetimoSharedPref {

    static SettingsSharedPref _instance = null;

    private final String TAG = "SettingsSharedPref";

    SharedPreferences settingPref;
    SharedPreferences.Editor settingsEditor;

    //------------------------------- User's choices ---------------------------------------------->
    public static final String SETTINGS_MONITORED_TASKS_SORT_ORDER =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.SETTINGS_MONITORED_TASKS_SORT_ORDER";

    @Deprecated
    public static final String SETTINGS_MONITORED_BLOCKS_REMEMBER =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.SETTINGS_MONITORED_BLOCKS_REMEMBER";
    public static final String SETTINGS_MONITORED_BLOCKS_LOCK =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.SETTINGS_MONITORED_BLOCKS_LOCK";
    public static final String SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS";
    public static final String SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS";


    public static final String SETTINGS_LANGUAGE =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.SETTINGS_LANGUAGE";
    public static final String LANG_VI = "vi";
    public static final String LANG_EN = "en";
    public static final String LANG_DE = "de";
    // This order is fixed
    public static final ArrayList<String> LANGUAGES =
            new ArrayList(Arrays.asList(new String[]{LANG_EN, LANG_DE, LANG_VI}));

    public static final String SETTINGS_OVERNIGHT_THRESHOLD =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.SETTINGS_OVERNIGHT_THRESHOLD";

    private final int DEFAULT_OVERNIGHT_THRESHOLD = 6;



    SettingsSharedPref(){
        super();
        this.settingPref = this.context.getSharedPreferences(
                this.context.getString(R.string.preference_file_settings), Context.MODE_PRIVATE);
        this.settingsEditor = settingPref.edit();
    }

    @Override
    public PetimoSharedPref getInstance() throws Exception{
        if (_instance == null)
            if (context == null)
                throw new Exception("PetimoSharedPref must be initialized first!");
            else
                _instance = new SettingsSharedPref();
        return _instance;
    }


    //<---------------------------------------------------------------------------------------------
    // Write
    // -------------------------------------------------------------------------------------------->

    /**
     *
     * @param tag
     * @param content
     */
    public void putInt(String tag, int content){
        switch (tag){
            case SETTINGS_OVERNIGHT_THRESHOLD:
                settingsEditor.putInt(tag, content);
                settingsEditor.apply();
                break;
            default:
                throw new SettingsException("putInt: Unknown settings tag ==> " + tag);
        }
    }

    /**
     *
     * @param tag
     * @param content
     */
    public void putString(String tag, String content){
        switch (tag){
            case SETTINGS_MONITORED_TASKS_SORT_ORDER:
                settingsEditor.putString(tag, content);
                settingsEditor.apply();
                break;
            case SETTINGS_LANGUAGE:
                switch (content){
                    case LANG_VI:
                    case LANG_DE:
                        settingsEditor.putString(tag, content);
                        break;
                    default:
                        // Default is English
                        settingsEditor.putString(tag, LANG_EN);
                }
                settingsEditor.apply();
                break;
            default:
                throw new SettingsException("putString: Unknown settings tag ==> " + tag);
        }
    }

    /**
     *
     * @param tag
     * @param content
     */
    public void putBoolean(String tag, boolean content){
        switch (tag){
            case SETTINGS_MONITORED_BLOCKS_LOCK:
            case SETTINGS_MONITORED_BLOCKS_REMEMBER:
            case SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS:
            case SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS:
                settingsEditor.putBoolean(tag, content);
                settingsEditor.apply();
                break;
            default:
                throw new SettingsException("putBoolean: Unknown settings tag ==> " + tag);
        }
    }

    /**
     * Save the sort order chosen by user to display monitored task.
     * If the given value is UNSORTED or invalid, the sort order is set to UNSORTED
     * @param sortOrder the given sort order
     */
    public void setUsrMonitoredSortOrder(Sort sortOrder){
        switch (sortOrder){
            case FREQUENCY:
                settingsEditor.putString(
                        SETTINGS_MONITORED_TASKS_SORT_ORDER, Sort.FREQUENCY.toString());
                break;
            case TIME:
                settingsEditor.putString(
                        SETTINGS_MONITORED_TASKS_SORT_ORDER, Sort.TIME.toString());
                break;
            default:
                settingsEditor.putString(
                        SETTINGS_MONITORED_TASKS_SORT_ORDER, Sort.UNSORTED.toString());
        }
        settingsEditor.apply();
    }


    //<---------------------------------------------------------------------------------------------
    // Read
    // -------------------------------------------------------------------------------------------->

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
     * @param defaultValue TODO passing null to use app default value
     * @return
     */
    public String getSettingsString(String tag, String defaultValue){
        switch (tag){
            case SETTINGS_MONITORED_TASKS_SORT_ORDER:
            case SETTINGS_LANGUAGE:
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
     * Get the sort order chosen by user for displaying monitored tasks
     * @return the sort order
     */
    public String getUsrMonitoredSortOrder(){
        switch (Sort.valueOf(settingPref.getString(
                SETTINGS_MONITORED_TASKS_SORT_ORDER, Sort.UNSORTED.toString()))){
            case FREQUENCY:
                return Sort.FREQUENCY.toString();
            case TIME:
                return Sort.TIME.toString();
            default:
                return Sort.TIME.toString();
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


    @Override
    public void updateV1toV2() {
        // do nothing here
    }
}

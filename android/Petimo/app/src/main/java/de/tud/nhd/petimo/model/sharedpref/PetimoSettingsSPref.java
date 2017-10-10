package de.tud.nhd.petimo.model.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.SettingsException;

/**
 * Created by nhd on 08.10.17.
 */

public class PetimoSettingsSPref extends PetimoSPref {

    static PetimoSettingsSPref _instance = null;

    private final String TAG = "PetimoSettingsSPref";

    SharedPreferences settingPref;
    SharedPreferences.Editor settingsEditor;

    //------------------------------- Monitored Tasks --------------------------------------------->
    public static final String MONITORED_TASKS_SORT_ORDER =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_TASKS_SORT_ORDER";

    @Deprecated
    public static final String MONITORED_BLOCKS_REMEMBER =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_BLOCKS_REMEMBER";
    public static final String MONITORED_BLOCKS_LOCK =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_BLOCKS_LOCK";
    public static final String MONITORED_BLOCKS_SHOW_SELECTED_TASKS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_BLOCKS_SHOW_SELECTED_TASKS";
    public static final String MONITORED_BLOCKS_SHOW_EMPTY_DAYS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_BLOCKS_SHOW_EMPTY_DAYS";
    public static final String MONITORED_BLOCKS_GROUP_BY =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_BLOCKS_GROUP_BY";


    //-------------------------------- Statistics ------------------------------------------------->

    public static final String STATISTICS_GROUP_BY =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.STATISTICS_GROUP_BY";
    public static final String STATISTICS_SHOW_SELECTED_TASKS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.STATISTICS_SHOW_SELECTED_TASKS";
    public static final String STATISTICS_SHOW_SELECTED_CATS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.STATISTICS_SHOW_SELECTED_CATS";


    public static final String LANGUAGE =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.LANGUAGE";
    public static final String LANG_VI = "vi";
    public static final String LANG_EN = "en";
    public static final String LANG_DE = "de";
    // This order is fixed
    public static final ArrayList<String> LANGS =
            new ArrayList(Arrays.asList(new String[]{LANG_EN, LANG_DE, LANG_VI}));

    public static final String OVERNIGHT_THRESHOLD =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.OVERNIGHT_THRESHOLD";

    private final int DEFAULT_OVERNIGHT_THRESHOLD = 6;



    PetimoSettingsSPref(){
        super();
        this.settingPref = this.context.getSharedPreferences(
                this.context.getString(R.string.preference_file_settings), Context.MODE_PRIVATE);
        this.settingsEditor = settingPref.edit();
    }

    public static PetimoSettingsSPref getInstance() throws RuntimeException{
        if (_instance == null)
            if (context == null)
                throw new RuntimeException("PetimoSPref must be initialized first!");
            else
                _instance = new PetimoSettingsSPref();
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
            case OVERNIGHT_THRESHOLD:
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
            case MONITORED_TASKS_SORT_ORDER:
            case MONITORED_BLOCKS_GROUP_BY:
                settingsEditor.putString(tag, content);
                settingsEditor.apply();
                break;
            case LANGUAGE:
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
            case MONITORED_BLOCKS_LOCK:
            case MONITORED_BLOCKS_REMEMBER:
            case MONITORED_BLOCKS_SHOW_SELECTED_TASKS:
            case MONITORED_BLOCKS_SHOW_EMPTY_DAYS:
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
    public void setUsrMonitoredSortOrder(String sortOrder){
        switch (sortOrder){
            case Consts.FREQUENCY:
                settingsEditor.putString(
                        MONITORED_TASKS_SORT_ORDER, Consts.FREQUENCY.toString());
                break;
            case Consts.TIME:
                settingsEditor.putString(
                        MONITORED_TASKS_SORT_ORDER, Consts.TIME.toString());
                break;
            default:
                settingsEditor.putString(
                        MONITORED_TASKS_SORT_ORDER, Consts.UNSORTED.toString());
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
            case OVERNIGHT_THRESHOLD:
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
            case MONITORED_TASKS_SORT_ORDER:
            case MONITORED_BLOCKS_GROUP_BY:
            case LANGUAGE:
            case STATISTICS_GROUP_BY:
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
            case MONITORED_BLOCKS_LOCK:
            case MONITORED_BLOCKS_REMEMBER:
            case MONITORED_BLOCKS_SHOW_SELECTED_TASKS:
            case MONITORED_BLOCKS_SHOW_EMPTY_DAYS:
            case STATISTICS_SHOW_SELECTED_TASKS:
            case STATISTICS_SHOW_SELECTED_CATS:
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
        switch (settingPref.getString(
                MONITORED_TASKS_SORT_ORDER, Consts.UNSORTED)){
            case Consts.FREQUENCY:
                return Consts.FREQUENCY.toString();
            case Consts.TIME:
                return Consts.TIME.toString();
            default:
                return Consts.TIME.toString();
        }
    }

    /**
     * Get the overnight threshold
     * @return the threshold
     * TODO remove this method, use generic method above instead
     */
    public int getOvThreshold(){
        return settingPref.getInt(OVERNIGHT_THRESHOLD, DEFAULT_OVERNIGHT_THRESHOLD);
    }


    @Override
    public void updateV1toV2() {
        // do nothing here
    }
}

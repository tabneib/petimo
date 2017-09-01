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
    private final String LIVE_MONITOR_STATUS =
            "de.tud.nhd.petimo.model.PetimoSharedPref.LIVE_MONITOR_STATUS";

    private static PetimoSharedPref _instance;
    private Context context;

    private SharedPreferences settingPref;
    private SharedPreferences monitorPref;
    private SharedPreferences.Editor settingsEditor;
    private SharedPreferences.Editor monitorEditor;

    //<---------------------------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------------------------->

    private PetimoSharedPref(Context context){
        this.context = context;
        // TODO get monitor data from shared preferences
        this.settingPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_settings), Context.MODE_PRIVATE);
        this.monitorPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_monitor), Context.MODE_PRIVATE);
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

}

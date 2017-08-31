package de.tud.nhd.petimo.controller;

import android.util.Log;

/**
 * Created by nhd on 31.08.17.
 */

public class PetimoController {
    private static final String TAG = "PetimoController";
    private static PetimoController _instance;

    //<---------------------------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------------------------->

    private PetimoController(){
        super();
    }

    public static void initialize() throws Exception{
        if(_instance != null)
            throw new Exception("Cannot initialize multiple instances of PetimoController!");
        else {
            _instance = new PetimoController();
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
    //
    // -------------------------------------------------------------------------------------------->


    //<---------------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------------->


}

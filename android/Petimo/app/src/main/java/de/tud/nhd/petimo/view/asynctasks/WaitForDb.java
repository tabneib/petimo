package de.tud.nhd.petimo.view.asynctasks;

/**
 * Created by nhd on 03.09.17.
 */

import android.os.AsyncTask;

import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.view.fragments.OffModeFragment;

/**
 * Busy loop until the db wrapper is ready. This is only done when the app is starting up
 */
public class WaitForDb extends AsyncTask<Void, Void, Void> {
    OffModeFragment fragment;
    PetimoController controller;
    public WaitForDb(OffModeFragment fragment, PetimoController controller){
        this.fragment = fragment;
        this.controller = controller;
    }
    @Override
    protected Void doInBackground(Void... params) {
        while (this.controller.isDbReady()){
            try{
                Thread.sleep(20);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        this.fragment.setSpinners();
        return null;
    }
}
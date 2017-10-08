package de.tud.nhd.petimo.model.sharedpref;

import android.content.Context;


/**
 * Created by nhd on 08.10.17.
 */

public abstract class PetimoSharedPref {

    // To avoid memory leak, use app context here ;)
    static Context context;

    PetimoSharedPref(){
    }

    public static void initialize(Context kontext) throws RuntimeException{
        if(context != null)
            throw new RuntimeException("Cannot initialize multiple instances of PetimoSharedPref!");
        else
            context = kontext;
    }

    /**
     * Update the way Petimo stores data in SharedPreferences due to Database upgrade/update from
     * V.1 to V.2. This is to avoid user data loss
     * Hard-coded
     */
    public abstract void updateV1toV2();


    enum Sort {
        TIME("TIME"),
        FREQUENCY("FREQUENCY"),
        UNSORTED("NONE");

        private final String sortOpt;
        Sort(final String sortOpt){
            this.sortOpt = sortOpt;
        }

        @Override
        public final String toString(){
            return this.sortOpt;
        }
    }
}

package de.tud.nhd.petimo.model.sharedpref;

import android.content.Context;


/**
 * Created by nhd on 08.10.17.
 */

public abstract class PetimoSPref {

    // To avoid memory leak, use app context here ;)
    static Context context;

    PetimoSPref(){
    }

    public static void initialize(Context kontext) throws RuntimeException{
        if(context != null)
            throw new RuntimeException("Cannot initialize multiple instances of PetimoSPref!");
        else
            context = kontext;
    }

    /**
     * Update the way Petimo stores data in SharedPreferences due to Database upgrade/update from
     * V.1 to V.2. This is to avoid user data loss
     * Hard-coded
     */
    public abstract void updateV1toV2();

/*
    public enum Consts {

        TIME("TIME"),
        FREQUENCY("FREQUENCY"),
        UNSORTED("NONE"),

        GROUP_BY_TASK("GROUP_BY_TASK"),
        GROUP_BY_CAT("GROUP_BY_CAT"),
        NOT_GROUP("NOT_GROUP");

        private final String sortOpt;
        Consts(final String sortOpt){
            this.sortOpt = sortOpt;
        }

        @Override
        public final String toString(){
            return this.sortOpt;
        }
    }*/

    public static final class Consts {

        // Consts for sorting options
        public static final String TIME = "TIME";
        public static final String FREQUENCY = "FREQUENCY";
        public static final String UNSORTED = "UNSORTED";

        // Consts for grouping of monitored blocks for displaying
        public static final String GROUP_BY_TASK = "GROUP_BY_TASK";
        public static final String GROUP_BY_CAT = "GROUP_BY_CAT";
        public static final String NOT_GROUP = "NOT_GROUP";
    }
}

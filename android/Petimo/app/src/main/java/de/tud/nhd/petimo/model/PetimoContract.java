package de.tud.nhd.petimo.model;

import android.provider.BaseColumns;

/**
 * Created by nhd on 28.08.17.
 */

public final class PetimoContract {


    private PetimoContract(){}

    public static class Categories implements BaseColumns{
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PRIORITY = "priority";
    }

    public static class Tasks implements BaseColumns{
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_PRIORITY = "priority";
    }

    public static class Monitor implements BaseColumns{
        public static final String TABLE_NAME = "monitor";
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_START = "start";
        public static final String COLUMN_NAME_END = "end";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_WEEKDAY = "week_day";
        public static final String COLUMN_NAME_OVERNIGHT = "over_night";
    }

}



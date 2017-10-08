package de.tud.nhd.petimo.model.db;

import android.provider.BaseColumns;

import de.tud.nhd.petimo.utils.StringParsingException;

/**
 * Created by nhd on 28.08.17.
 */

public final class PetimoContract {




    private PetimoContract(){}

    public static class Categories implements BaseColumns{
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_DELETE_TIME = "delete_time";
        public static final String COLUMN_NAME_NOTE = "note";

        public static String[] getAllColumns(){
            return new String[] {_ID, COLUMN_NAME_NAME, COLUMN_NAME_PRIORITY,
                    COLUMN_NAME_STATUS, COLUMN_NAME_DELETE_TIME, COLUMN_NAME_NOTE};
        }
    }

    public static class Tasks implements BaseColumns{
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_DELETED_TIME = "deleted_time";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static String[] getAllColumns(){
            return new String[] {_ID, COLUMN_NAME_NAME, COLUMN_NAME_CATEGORY, COLUMN_NAME_PRIORITY,
                    COLUMN_NAME_STATUS, COLUMN_NAME_DELETED_TIME, COLUMN_NAME_NOTE,
                    COLUMN_NAME_CATEGORY_ID};
        }
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
        public static final String COLUMN_NAME_OV_THRESHOLD = "ov_threashold";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_TASK_ID = "task_id";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";

        public static String[] getAllColumns(){
            return new String[]{_ID, COLUMN_NAME_TASK, COLUMN_NAME_CATEGORY,
                    COLUMN_NAME_START, COLUMN_NAME_END, COLUMN_NAME_DURATION,
                    COLUMN_NAME_DATE, COLUMN_NAME_WEEKDAY, COLUMN_NAME_OVERNIGHT,
                    COLUMN_NAME_STATUS, COLUMN_NAME_NOTE, COLUMN_NAME_TASK_ID,
                    COLUMN_NAME_CATEGORY_ID, COLUMN_NAME_OV_THRESHOLD};
        }
    }
}

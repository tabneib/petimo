package de.tud.nhd.petimo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nhd on 28.08.17.
 */
public class PetimoDbHelper extends SQLiteOpenHelper {

    // SQL query to create the Categories table
    public static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE " + PetimoContract.Categories.TABLE_NAME + " (" +
                    PetimoContract.Categories._ID + " INTEGER PRIMARY KEY," +
                    PetimoContract.Categories.COLUMN_NAME_NAME + " TEXT NOT NULL UNIQUE," +
                    PetimoContract.Categories.COLUMN_NAME_PRIORITY + " INTEGER)";

    // SQL query to create the Tasks table
    public static final String SQL_CREATE_TASKS =
            "CREATE TABLE " + PetimoContract.Tasks.TABLE_NAME + " (" +
                    PetimoContract.Tasks._ID + " INTEGER PRIMARY KEY," +
                    PetimoContract.Tasks.COLUMN_NAME_NAME + " TEXT NOT NULL UNIQUE," +
                    PetimoContract.Tasks.COLUMN_NAME_CATEGORY + " TEXT NOT NULL," +
                    PetimoContract.Tasks.COLUMN_NAME_PRIORITY + ")";

    // SQL query to create the Monitor table
    public static final String SQL_CREATE_MONITOR =
            "CREATE TABLE " + PetimoContract.Monitor.TABLE_NAME + " (" +
                    PetimoContract.Monitor._ID + " INTEGER PRIMARY KEY," +
                    PetimoContract.Monitor.COLUMN_NAME_TASK + " TEXT NULLABLE," +
                    PetimoContract.Monitor.COLUMN_NAME_CATEGORY + " TEXT NOT NULL," +
                    PetimoContract.Monitor.COLUMN_NAME_START + " INTEGER," +
                    PetimoContract.Monitor.COLUMN_NAME_END + " INTEGER," +
                    PetimoContract.Monitor.COLUMN_NAME_DURATION + " INTEGER," +
                    PetimoContract.Monitor.COLUMN_NAME_DATE + " INTEGER," +
                    PetimoContract.Monitor.COLUMN_NAME_WEEKDAY + " INTEGER," +
                    PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT + " INTEGER)";



    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Petimo.db";

    public PetimoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Categories table
        db.execSQL(SQL_CREATE_CATEGORIES);
        db.execSQL(SQL_CREATE_TASKS);
        db.execSQL(SQL_CREATE_MONITOR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing

    }

}

package de.tud.nhd.petimo.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by nhd on 28.08.17.
 */
public class PetimoDbHelper extends SQLiteOpenHelper {

    public static final String TAG = "PetimoDbHelper";

    // SQL query to create the Categories table (V.1)
    public static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE " + PetimoContract.Categories.TABLE_NAME + " (" +
                    PetimoContract.Categories._ID + " INTEGER PRIMARY KEY," +
                    PetimoContract.Categories.COLUMN_NAME_NAME + " TEXT NOT NULL UNIQUE," +
                    PetimoContract.Categories.COLUMN_NAME_PRIORITY + " INTEGER)";

    // SQL query to create the Tasks table (V.1)
    public static final String SQL_CREATE_TASKS =
            "CREATE TABLE " + PetimoContract.Tasks.TABLE_NAME + " (" +
                    PetimoContract.Tasks._ID + " INTEGER PRIMARY KEY," +
                    PetimoContract.Tasks.COLUMN_NAME_NAME + " TEXT NOT NULL UNIQUE," +
                    PetimoContract.Tasks.COLUMN_NAME_CATEGORY + " TEXT NOT NULL," +
                    PetimoContract.Tasks.COLUMN_NAME_PRIORITY + ")";

    // SQL query to create the Monitor table (V.1)
    public static final String SQL_CREATE_MONITOR =
            "CREATE TABLE " + PetimoContract.Monitor.TABLE_NAME + " (" +
                    PetimoContract.Monitor._ID + " INTEGER PRIMARY KEY," +
                    PetimoContract.Monitor.COLUMN_NAME_TASK + " TEXT NULLABLE," +
                    PetimoContract.Monitor.COLUMN_NAME_CATEGORY + " TEXT NOT NULL," +
                    PetimoContract.Monitor.COLUMN_NAME_START + " LONG," +
                    PetimoContract.Monitor.COLUMN_NAME_END + " INTEGER," +
                    PetimoContract.Monitor.COLUMN_NAME_DURATION + " INTEGER," +
                    PetimoContract.Monitor.COLUMN_NAME_DATE + " INTEGER," +
                    PetimoContract.Monitor.COLUMN_NAME_WEEKDAY + " INTEGER," +
                    PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT + " INTEGER)";

    //public static final int INIT_DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Petimo.db";

    public PetimoDbHelper(Context context, int dbVersion) {
        super(context, DATABASE_NAME, null, dbVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        // Create Tables V.1
        db.execSQL(SQL_CREATE_CATEGORIES);
        db.execSQL(SQL_CREATE_TASKS);
        db.execSQL(SQL_CREATE_MONITOR);
        // upgrade V.1 -> V.2
        onUpgrade(db, 1, 2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: "+oldVersion+" ==> " + newVersion);
        if (newVersion < oldVersion)
            throw new IllegalStateException("DB version cannot be set to an old value");
        switch (oldVersion){
            case 1:
                // 25.09.2017
                // Update Categories Table
                db.beginTransaction();
                try{
                    addColumn(db, PetimoContract.Categories.TABLE_NAME,
                            PetimoContract.Categories.COLUMN_NAME_STATUS + " TEXT");
                    addColumn(db, PetimoContract.Categories.TABLE_NAME,
                            PetimoContract.Categories.COLUMN_NAME_DELETE_TIME + " LONG");
                    addColumn(db, PetimoContract.Categories.TABLE_NAME,
                            PetimoContract.Categories.COLUMN_NAME_NOTE + " TEXT");
                    // Update Tasks Table
                    addColumn(db, PetimoContract.Tasks.TABLE_NAME,
                            PetimoContract.Tasks.COLUMN_NAME_STATUS + " TEXT");
                    addColumn(db, PetimoContract.Tasks.TABLE_NAME,
                            PetimoContract.Tasks.COLUMN_NAME_DELETED_TIME + " LONG");
                    addColumn(db, PetimoContract.Tasks.TABLE_NAME,
                            PetimoContract.Tasks.COLUMN_NAME_NOTE + " TEXT");
                    addColumn(db, PetimoContract.Tasks.TABLE_NAME,
                            PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID + " INTEGER");
                    // Update Monitor Table
                    addColumn(db, PetimoContract.Monitor.TABLE_NAME,
                            PetimoContract.Monitor.COLUMN_NAME_OV_THRESHOLD + " INTEGER");
                    addColumn(db, PetimoContract.Monitor.TABLE_NAME,
                            PetimoContract.Monitor.COLUMN_NAME_STATUS + " TEXT");
                    addColumn(db, PetimoContract.Monitor.TABLE_NAME,
                            PetimoContract.Monitor.COLUMN_NAME_NOTE + " TEXT");
                    addColumn(db, PetimoContract.Monitor.TABLE_NAME,
                            PetimoContract.Monitor.COLUMN_NAME_TASK_ID + " INTEGER");
                    addColumn(db, PetimoContract.Monitor.TABLE_NAME,
                            PetimoContract.Monitor.COLUMN_NAME_CATEGORY_ID +
                                    " INTEGER NOT NULL DEFAULT -1");
                    db.setTransactionSuccessful();
                    Log.d(TAG, "Database upgraded from V.1 to V.2!");
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "Database cannot be upgraded from V.1 to V.2!");
                    break;
                }
                finally {
                    db.endTransaction();
                }
            case 2:
                // newest version, do nothing
                break;
            default:
                throw new IllegalStateException("Some error occurred, cannot upgrade db!");
        }

    }

    private void addColumn(SQLiteDatabase db, String tableName, String columnDef){
        db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnDef);
    }

}

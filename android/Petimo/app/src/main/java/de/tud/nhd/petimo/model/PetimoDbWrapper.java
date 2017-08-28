package de.tud.nhd.petimo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhd on 28.08.17.
 */

public class PetimoDbWrapper {

    private final String TAG = "PetimoDbWrapper";
    private static PetimoDbWrapper _instance = null;
    private SQLiteDatabase writableDb = null;
    private SQLiteDatabase readableDb = null;
    private SQLiteOpenHelper dbHelper = null;


    //<---------------------------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------------------------->

    private PetimoDbWrapper(Context context){
        dbHelper = new PetimoDbHelper(context);
        new GetReadableDbTask(dbHelper).execute((Void) null);
        new GetWritableDbTask(dbHelper).execute((Void) null);

    }

    public static void initalize(Context context) throws Exception{
        if(_instance != null)
            throw new Exception("Cannot initialize multiple instances of PetimoDbWrapper!");
        else
            _instance = new PetimoDbWrapper(context);
    }

    public static PetimoDbWrapper getInstance() throws Exception{
        if (_instance == null)
            throw new Exception("PetimoDbWrapper is not yet initialized!");
        else
            return _instance;
    }


    /**
     * TODO comment em
     */
    private class GetReadableDbTask extends AsyncTask<Void, Void, SQLiteDatabase>{

        private SQLiteOpenHelper dbHelper;

        public GetReadableDbTask(SQLiteOpenHelper dbHelper) {
            this.dbHelper = dbHelper;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected SQLiteDatabase doInBackground(Void... params) {
            return dbHelper.getReadableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase d) {
            super.onPostExecute(d);
            readableDb = d;
        }
    }


    /**
     * TODO comment em
     */
    private class GetWritableDbTask extends AsyncTask<Void, Void, SQLiteDatabase>{

        private SQLiteOpenHelper dbHelper;

        public GetWritableDbTask(SQLiteOpenHelper dbHelper) {
            this.dbHelper = dbHelper;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected SQLiteDatabase doInBackground(Void... params) {
            return dbHelper.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase d) {
            super.onPostExecute(d);
            writableDb = d;
        }
    }

    //<---------------------------------------------------------------------------------------------
    // Core - Writing
    //--------------------------------------------------------------------------------------------->


    /**
     *
     * @param name
     * @param priority
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertCategory(String name, int priority){
        ContentValues values = new ContentValues();
        values.put(PetimoContract.Categories.COLUMN_NAME_NAME, name);
        values.put(PetimoContract.Categories.COLUMN_NAME_PRIORITY, priority);
        return writableDb.insert(PetimoContract.Categories.TABLE_NAME, null, values);
    }

    /**
     *
     * @param name
     * @param category
     * @param priority
     * @return the row ID of the newly inserted row, -2 if category does not exist,
     * or -1 if an error occurred
     */
    public long insertTask(String name, String category, int priority){
        if (!checkCategoryExist(category)){
            Log.d(TAG, "Cannot insert new task: Category doesn't exist!");
            return -2;
        }
        else{
            ContentValues values = new ContentValues();
            values.put(PetimoContract.Tasks.COLUMN_NAME_NAME, name);
            values.put(PetimoContract.Tasks.COLUMN_NAME_CATEGORY, category);
            values.put(PetimoContract.Tasks.COLUMN_NAME_PRIORITY, priority);
            return writableDb.insert(PetimoContract.Tasks.TABLE_NAME, null, values);
        }
    }

    /**
     *
     * @param task
     * @param category
     * @param start
     * @param end
     * @param duration
     * @param date
     * @param weekDay
     * @param overNight
     * @return the row ID of the newly inserted row,-3 if task does not exist,
     * -2 if category does not exist, or -1 if an error occurred
     */
    public long insertMonitorBlock(String task, String category, int start, int end,
                                   int duration, int date, int weekDay, int overNight){
        if (!checkCategoryExist(category)){
            Log.d(TAG, "Cannot insert new monitor block: Category doesn't exist!");
            return -2;
        }
        else if (task != null && !checkTaskExist(task)){
            return -3;
        }
        else{
            ContentValues values = new ContentValues();
            values.put(PetimoContract.Monitor.COLUMN_NAME_TASK, task);
            values.put(PetimoContract.Monitor.COLUMN_NAME_CATEGORY, category);
            values.put(PetimoContract.Monitor.COLUMN_NAME_START, start);
            values.put(PetimoContract.Monitor.COLUMN_NAME_END, end);
            values.put(PetimoContract.Monitor.COLUMN_NAME_DURATION, duration);
            values.put(PetimoContract.Monitor.COLUMN_NAME_DATE, date);
            values.put(PetimoContract.Monitor.COLUMN_NAME_WEEKDAY, weekDay);
            values.put(PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT, overNight);
            return writableDb.insert(PetimoContract.Monitor.TABLE_NAME, null, values);
        }
    }

    //<---------------------------------------------------------------------------------------------
    // Core - Reading
    //--------------------------------------------------------------------------------------------->

    public MonitorDay getDay(int date){
        String[] projection = {
                PetimoContract.Monitor._ID,
                PetimoContract.Monitor.COLUMN_NAME_TASK,
                PetimoContract.Monitor.COLUMN_NAME_CATEGORY,
                PetimoContract.Monitor.COLUMN_NAME_START,
                PetimoContract.Monitor.COLUMN_NAME_END,
                PetimoContract.Monitor.COLUMN_NAME_DURATION,
                PetimoContract.Monitor.COLUMN_NAME_DATE,
                PetimoContract.Monitor.COLUMN_NAME_WEEKDAY,
                PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT
        };

        String selection = PetimoContract.Monitor.COLUMN_NAME_DATE + " = " + date;
        String sortOrder = PetimoContract.Monitor.COLUMN_NAME_START + " ASC";

        Cursor cursor = readableDb.query(
                PetimoContract.Monitor.TABLE_NAME,
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );

        List<MonitorBlock> monitorBlocks = new ArrayList<>();

        // Iterate the cursor and extract the monitor blocks
        while (cursor.moveToNext()){
            monitorBlocks.add(
                    new MonitorBlock(
                            cursor.getInt(cursor.getColumnIndexOrThrow(PetimoContract.Monitor._ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    PetimoContract.Monitor.COLUMN_NAME_TASK)),
                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    PetimoContract.Monitor.COLUMN_NAME_CATEGORY)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(
                                    PetimoContract.Monitor.COLUMN_NAME_START)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(
                                    PetimoContract.Monitor.COLUMN_NAME_END)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(
                                    PetimoContract.Monitor.COLUMN_NAME_DURATION)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(
                                    PetimoContract.Monitor.COLUMN_NAME_DATE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(
                                    PetimoContract.Monitor.COLUMN_NAME_WEEKDAY)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(
                                    PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT))
                    )
            );
        }
        cursor.close();

        return new MonitorDay(date, monitorBlocks);
    }


    //<---------------------------------------------------------------------------------------------
    // Auxiliary
    //--------------------------------------------------------------------------------------------->

    /**
     *
     * @param category
     * @return
     */
    public boolean checkCategoryExist(String category){
        // TODO: implement em
        return true;
    }

    public boolean checkTaskExist(String task){
        // TODO: implement me
        return true;
    }
}

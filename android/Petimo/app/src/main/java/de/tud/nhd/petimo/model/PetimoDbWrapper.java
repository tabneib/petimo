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

    private static final String TAG = "PetimoDbWrapper";
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

    public static void initialize(Context context) throws Exception{
        if(_instance != null)
            throw new Exception("Cannot initialize multiple instances of PetimoDbWrapper!");
        else {
            _instance = new PetimoDbWrapper(context);
            Log.d(TAG, "Initialized!");
        }
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
    // Core - Database - Inserting Data
    //--------------------------------------------------------------------------------------------->


    /**
     *
     * @param name
     * @param priority
     * @return the row ID of the newly inserted row, -1 if an database error occurred, -2 if  the
     * category already exists, -3 if the given name is invalid
     */
    public long insertCategory(String name, int priority){
        if (checkCatExists(name))
            return -2;
        if (!checkName(name))
            return -3;
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
     * @return the row ID of the newly inserted row, -1 if a database error occurred, -2 if the
     * category does not exist, -3 if the task already exists, or -4 if the name is invalid
     */
    public long insertTask(String name, String category, int priority){
        if (!checkCatExists(category))
            return -2;
        if (checkTaskExists(name, category))
            return -3;
        if(!checkName(name))
            return -4;
        else{
            ContentValues values = new ContentValues();
            values.put(PetimoContract.Tasks.COLUMN_NAME_NAME, name);
            values.put(PetimoContract.Tasks.COLUMN_NAME_CATEGORY, category);
            values.put(PetimoContract.Tasks.COLUMN_NAME_PRIORITY, priority);
            return writableDb.insert(PetimoContract.Tasks.TABLE_NAME, null, values);
        }
    }

    /**
     * TODO comment me
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
        if (!checkCatExists(category))
            return -2;
        if (task != null && !checkTaskExists(task, category))
            return -3;
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

    //<---------------------------------------------------------------------------------------------
    // Core - Database - Fetching Data
    //--------------------------------------------------------------------------------------------->

    /**
     * TODO comment em
     * @param date
     * @return
     */
    public MonitorDay getDay(int date){
        String selection = PetimoContract.Monitor.COLUMN_NAME_DATE + " = " + date;
        String sortOrder = PetimoContract.Monitor.COLUMN_NAME_START + " ASC";

        Cursor cursor = readableDb.query(
                PetimoContract.Monitor.TABLE_NAME,
                PetimoContract.Monitor.getAllColumns(),
                selection, null, null, null, sortOrder);

        List<MonitorBlock> monitorBlocks = new ArrayList<>();
        while (cursor.moveToNext())
            monitorBlocks.add(getBlockFromCursor(cursor));
        cursor.close();
        return new MonitorDay(date, monitorBlocks);
    }

    /**
     * TODO comment me
     * TODO throw this into an async task !
     * @param startDate
     * @param endDate
     * @return
     */
    public List<MonitorDay> getDaysByRange(int startDate, int endDate){
        String selection = PetimoContract.Monitor.COLUMN_NAME_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {Integer.toString(startDate), Integer.toString(endDate)};
        String sortOrder = PetimoContract.Monitor.COLUMN_NAME_DATE + " ASC";
        Cursor cursor = readableDb.query(PetimoContract.Monitor.TABLE_NAME,
                PetimoContract.Monitor.getAllColumns(), selection,
                selectionArgs, null, null, sortOrder);

        List<MonitorDay> days = new ArrayList<>();
        List<MonitorBlock> tmpBlocks = new ArrayList<>();
        int tmpDay = 0;
        while (cursor.moveToNext()){
            if (tmpDay != 0){
                // Check if the cursor moves to the next date
                if (cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_DATE)) != tmpDay){
                    days.add(new MonitorDay(tmpDay, tmpBlocks));
                    // Update the tmp date and empty the
                    tmpDay = cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Monitor.COLUMN_NAME_DATE));
                    tmpBlocks.clear();
                }
            }
            else
                tmpDay = cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_DATE));

            tmpBlocks.add(getBlockFromCursor(cursor));
        }
        cursor.close();
        return days;
    }

    /**
     * TODO comment me
     * @param catName
     * @return
     */
    public List<MonitorTask> getTasksByCat(String catName){
        String selection = PetimoContract.Tasks.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {catName};
        List<MonitorTask> tasks = new ArrayList<>();
        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                PetimoContract.Tasks.getAllColumns(), selection,
                selectionArgs, null, null, null);

        while (cursor.moveToNext())
            tasks.add(getTaskFromCursor(cursor));
        cursor.close();
        return tasks;
    }

    /**
     * TODO comment me
     * @param catName
     * @return
     */
    public List<String> getTaskNamesByCat(String catName){
        String selection = PetimoContract.Tasks.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {catName};
        List<String> taskNames = new ArrayList<>();
        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                PetimoContract.Tasks.getAllColumns(), selection,
                selectionArgs, null, null, null);

        while (cursor.moveToNext())
            taskNames.add(cursor.getString(cursor.getColumnIndexOrThrow(
                    PetimoContract.Tasks.COLUMN_NAME_NAME)));
        cursor.close();
        return taskNames;
    }

    /**
     * TODO comment em
     * @return
     */
    public List<MonitorCategory> getAllCategories(){
        List<MonitorCategory> categories = new ArrayList<>();
        Cursor cursor = readableDb.query(PetimoContract.Categories.TABLE_NAME,
                PetimoContract.Categories.getAllColumns(), null, null, null, null, null, null);

        while(cursor.moveToNext()){
            Log.d(TAG, "pre-fetching categories: " + cursor.getString(cursor.getColumnIndexOrThrow(
                    PetimoContract.Categories.COLUMN_NAME_NAME)));

            categories.add(new MonitorCategory(
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_PRIORITY))));
        }
        cursor.close();
        return categories;
    }

    /**
     * TODO comment me
     * @return
     */
    public List<String> getAllCatNames(){
        List<String> catNames = new ArrayList<>();
        Cursor cursor = readableDb.query(PetimoContract.Categories.TABLE_NAME,
                PetimoContract.Categories.getAllColumns(), null, null, null, null, null, null);

        while(cursor.moveToNext())
            catNames.add(cursor.getString(cursor.getColumnIndexOrThrow(
                    PetimoContract.Categories.COLUMN_NAME_NAME)));
        cursor.close();
        return catNames;
    }

    /**
     * TODO comment me
     * @return
     */
    public List<MonitorTask> getAllTasks(){
        List<MonitorTask> tasks = new ArrayList<>();

        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                PetimoContract.Tasks.getAllColumns(), null, null, null, null, null, null);

        while(cursor.moveToNext())
            tasks.add(getTaskFromCursor(cursor));
        cursor.close();
        return tasks;
    }

    /**
     * TODO comment em
     * @return
     */
    public List<String> getAllTaskName(){
        List<String> taskNames = new ArrayList<>();

        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                PetimoContract.Tasks.getAllColumns(), null, null, null, null, null, null);

        while(cursor.moveToNext())
            taskNames.add(cursor.getString(cursor.getColumnIndexOrThrow(
                    PetimoContract.Tasks.COLUMN_NAME_NAME)));
        cursor.close();
        return taskNames;
    }

    //<---------------------------------------------------------------------------------------------
    // Core - Database - Deleting Data
    //--------------------------------------------------------------------------------------------->

    /**
     * TODO comment em
     * @param date
     * @return
     */
    public int removeDay(int date){
        String selection = PetimoContract.Monitor.COLUMN_NAME_DATE + " = ?";
        String[] selectionArgs = {Integer.toString(date)};
        return writableDb.delete(PetimoContract.Monitor.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * TODO comment me
     * @param id
     * @return
     */
    public int removeBlockById(int id){
        String selection = PetimoContract.Monitor._ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        return writableDb.delete(PetimoContract.Monitor.TABLE_NAME, selection, selectionArgs);
    }


    //<---------------------------------------------------------------------------------------------
    // Core - Database - Tables
    //--------------------------------------------------------------------------------------------->

    public void dropCategories(){
        String query = "DROP TABLE " + PetimoContract.Categories.TABLE_NAME;
        writableDb.execSQL(query);
    }

    public void dropTasks(){
        String query = "DROP TABLE " + PetimoContract.Tasks.TABLE_NAME;
        writableDb.execSQL(query);
    }


    public void dropMonitor(){
        String query = "DROP TABLE " + PetimoContract.Monitor.TABLE_NAME;
        writableDb.execSQL(query);
    }


    public void dropAll(){
        dropCategories();
        dropMonitor();
        dropTasks();
    }

    public void createCategories(){
        writableDb.execSQL(PetimoDbHelper.SQL_CREATE_CATEGORIES);
    }

    public void createTasks(){
        writableDb.execSQL(PetimoDbHelper.SQL_CREATE_TASKS);
    }

    public void createMonitor(){
        writableDb.execSQL(PetimoDbHelper.SQL_CREATE_MONITOR);
    }

    public void createAll(){
        createCategories();
        createTasks();
        createMonitor();
    }



    //<---------------------------------------------------------------------------------------------
    // Core - Database - Auxiliary
    //--------------------------------------------------------------------------------------------->

    /**
     * Check if the wrapper is ready to read and write database
     * @return true if ready, otherwise false
     */
    public boolean isReady(){
        // Wrapper is ready iff both readable and writable databases are not null
        // TODO verify this approach
        return (readableDb!=null && writableDb!=null);
    }

    /**
     * Check if a category with the given name exists
     * @param name
     * @return
     */
    public boolean checkCatExists(String name){
        return this.getAllCatNames().contains(name);
    }

    /**
     * TODO comment em
     * @param task
     * @param category
     * @return
     */
    public boolean checkTaskExists(String task, String category){
        return this.getTaskNamesByCat(category).contains(task);
    }

    /**
     * TODO comment me
     */
    public void generateXml(){
        new ToXmlTask().execute((Void) null);
    }

    private class ToXmlTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            String s = "<petimo>\n";
            // Categories
            s = s + "\t<categories>\n";
            for (MonitorCategory cat : getAllCategories())
                s = s + cat.toXml(2) + "\n";
            s = s + "\t<categories>\n\n";

            // Tasks
            s = s + "\t<tasks>\n";
            for (MonitorTask task : getAllTasks())
                s = s + task.toXml(2) + "\n";
            s = s + "\t<tasks>\n\n";

            // Blocks
            s = s + "\t<blocks>\n";
            Cursor cursor = readableDb.query(PetimoContract.Monitor.TABLE_NAME,
                    PetimoContract.Monitor.getAllColumns(),
                    null, null, null, null, null);

            while (cursor.moveToNext()){
                s = s + "\t\t<block id='" +
                cursor.getInt(cursor.getColumnIndexOrThrow(PetimoContract.Monitor._ID)) + "' task='" +
                        cursor.getString(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_TASK)) + "' category='" +
                        cursor.getString(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_CATEGORY)) + "' start='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_START)) + "' end='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_END)) + "' duration='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_DURATION)) + "' date='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_DATE)) + "' weekday='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_WEEKDAY)) + "' overnight='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT)) + "' />\n";
            }
            s = s + "\t</blocks>\n";
            s = s + "</petimo>\n";
            return s;
        }

        @Override
        protected void onPostExecute(String xml) {
            super.onPostExecute(xml);
            System.out.println(xml);
        }
    }



    //<---------------------------------------------------------------------------------------------
    // Auxiliary
    //--------------------------------------------------------------------------------------------->

    /**
     * Return a monitor block from the given cursor. The cursor must have moved to the corresponding
     * position
     * @param cursor the given cursor
     * @return a monitor block from the given cursor
     */
    private MonitorBlock getBlockFromCursor(Cursor cursor){
        return new MonitorBlock(
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
                        PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT)));
    }

    /**
     * Return a monitor task from the given cursor. The cursor must have moved to the corresponding
     * position
     * @param cursor the given cursor
     * @return a monitor task from the given cursor
     */
    private MonitorTask getTaskFromCursor(Cursor cursor){
        return new MonitorTask(
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks.COLUMN_NAME_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks.COLUMN_NAME_CATEGORY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks.COLUMN_NAME_PRIORITY)));
    }

    /**
     * TODO comment me
     * @param name
     * @return
     */
    private boolean checkName(String name){
        boolean result = true;
        if (name.isEmpty())
            result = false;
        // TODO: Check if the name does not contain only backspaces

        return result;
    }
}

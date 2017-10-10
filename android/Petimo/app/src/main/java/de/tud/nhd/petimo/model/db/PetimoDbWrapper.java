package de.tud.nhd.petimo.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.tud.nhd.petimo.controller.ResponseCode;
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.controller.exception.InvalidInputNameException;
import de.tud.nhd.petimo.model.sharedpref.SharedPref;

/**
 * Created by nhd on 28.08.17.
 */

public class PetimoDbWrapper {

    private static final String TAG = "PetimoDbWrapper";
    private static PetimoDbWrapper _instance = null;
    private SQLiteDatabase writableDb = null;
    private SQLiteDatabase readableDb = null;
    private SQLiteOpenHelper dbHelper = null;
    //public static final int dbVersion = 1;
    // 25.09.2017
    public static final int dbVersion = 2;


    private static Context context;


    //<---------------------------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------------------------->

    // TODO: Memory leaks! Use Application context here?
    private PetimoDbWrapper(Context context){
        dbHelper = new PetimoDbHelper(context, dbVersion);

        new GetReadableDbTask(dbHelper).execute((Void) null);
        new GetWritableDbTask(dbHelper).execute((Void) null);

    }

    public static void setContext(Context kontext) {
        context = kontext;
    }

    public static PetimoDbWrapper getInstance() {
        if (_instance == null){
            _instance = new PetimoDbWrapper(context);
            return _instance;
        }
        else
            return _instance;
    }


    /**
     * Async task to get database to read. This task must be executed when the dbWrapper is init
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
     * Async task to get the database to write. This task must be  executed when dbWrapper is innit
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
     * Write a new category into the database
     * Database version: V.2
     * @param name  name of the new category
     * @param priority  The priority of the the category
     * @return The corresponding response code
     */
    public ResponseCode insertCategory(String name, int priority, String status,
                                       long deleteTime, String note)
            throws DbErrorException, InvalidInputNameException, InvalidCategoryException {
        if (!checkName(name))
            throw new InvalidInputNameException(
                    "Invalid category name: " + name);
        if (checkCatExists(name))
            throw new InvalidCategoryException("Category already exists: " + name);
        ContentValues values = new ContentValues();
        values.put(PetimoContract.Categories.COLUMN_NAME_NAME, name);
        values.put(PetimoContract.Categories.COLUMN_NAME_PRIORITY, priority);
        values.put(PetimoContract.Categories.COLUMN_NAME_STATUS, status);
        values.put(PetimoContract.Categories.COLUMN_NAME_DELETE_TIME, deleteTime);
        values.put(PetimoContract.Categories.COLUMN_NAME_NOTE, note);
        if(writableDb.insert(PetimoContract.Categories.TABLE_NAME, null, values) == -1)
            throw new DbErrorException("Some DB error has occured, please try again");
        else
            return ResponseCode.OK;
    }


    /**
     * Write a new task into the database
     * @param taskName  The name of the task
     * @param catId  ID of the corresponding category
     * @param priority  The priority of the new task
     * @return a response code according to the result of the insertion
     */
    public ResponseCode insertTask(String taskName, int catId, int priority, String status,
                                   long deleteTime, String note)
            throws DbErrorException, InvalidInputNameException, InvalidCategoryException {
        if(!checkName(taskName))
            throw new InvalidInputNameException("Task name is invalid: " + taskName);
        if (!checkCatExists(catId))
            throw new InvalidCategoryException("Category does not exist: " + catId);
        if (checkTaskNameExists(catId, taskName))
            throw new InvalidCategoryException("Task already exists for the given category ID: "
                    + catId + " / " + taskName);
        else{
            ContentValues values = new ContentValues();
            values.put(PetimoContract.Tasks.COLUMN_NAME_NAME, taskName);
            //TODO Category column is NOT NULL. Remove this line in db V.3
            values.put(PetimoContract.Tasks.COLUMN_NAME_CATEGORY, getCatNameById(catId));
            values.put(PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID, catId);
            values.put(PetimoContract.Tasks.COLUMN_NAME_PRIORITY, priority);
            values.put(PetimoContract.Tasks.COLUMN_NAME_STATUS, status);
            values.put(PetimoContract.Tasks.COLUMN_NAME_DELETED_TIME, deleteTime);
            values.put(PetimoContract.Tasks.COLUMN_NAME_NOTE, note);
            if(writableDb.insert(PetimoContract.Tasks.TABLE_NAME, null, values) == -1)
                throw new DbErrorException("Some DB error has occured, please try again");
            else {
                return ResponseCode.OK;
            }
        }
    }

    /**
     * Write a new monitor block into the database
     * @param taskId
     * @param catId
     * @param start
     * @param end
     * @param duration
     * @param date
     * @param weekDay
     * @param overNight
     * @param ovThreshold
     * @param status
     * @param note
     * @return
     * @throws DbErrorException
     * @throws InvalidCategoryException
     */
    public ResponseCode insertMonitorBlock(
            int taskId, int catId, long start, long end,
            long duration, int date, int weekDay, int overNight,
            int ovThreshold, String status, String note)
            throws DbErrorException, InvalidCategoryException {

        if (!checkCatExists(catId)) {
            throw new InvalidCategoryException("Category already exists: " + catId);
        }

        ContentValues values = new ContentValues();
        values.put(PetimoContract.Monitor.COLUMN_NAME_TASK_ID, taskId);
        values.put(PetimoContract.Monitor.COLUMN_NAME_CATEGORY_ID, catId);
        //TODO Category column is NOT NULL. Remove this line in db V.3 when Cat column is removed
        values.put(PetimoContract.Monitor.COLUMN_NAME_CATEGORY, getCatNameById(catId));
        values.put(PetimoContract.Monitor.COLUMN_NAME_START, start);
        values.put(PetimoContract.Monitor.COLUMN_NAME_END, end);
        values.put(PetimoContract.Monitor.COLUMN_NAME_DURATION, duration);
        values.put(PetimoContract.Monitor.COLUMN_NAME_DATE, date);
        values.put(PetimoContract.Monitor.COLUMN_NAME_WEEKDAY, weekDay);
        values.put(PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT, overNight);
        values.put(PetimoContract.Monitor.COLUMN_NAME_OV_THRESHOLD, ovThreshold);
        values.put(PetimoContract.Monitor.COLUMN_NAME_STATUS, status);
        values.put(PetimoContract.Monitor.COLUMN_NAME_NOTE, note);

        if (writableDb.insert(PetimoContract.Monitor.TABLE_NAME, null, values) == -1)
            throw new DbErrorException("Some DB error has occured, please try again");
        else
            return ResponseCode.OK;
    }

    //<---------------------------------------------------------------------------------------------
    // Core - Database - Fetching Data
    //--------------------------------------------------------------------------------------------->

    /**
     * Generate a MonitorDay object that represents the given date
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
     * Generate a list of MonitorDay objects represent the given date range
     * Database version: V.2
     * @param startDate
     * @param endDate
     * @return
     */
    public ArrayList<MonitorDay> getDaysByRange(
            int startDate, int endDate, ArrayList<Integer> selectedTasks, String sorting){
        String selection = PetimoContract.Monitor.COLUMN_NAME_DATE + " BETWEEN ? AND ?";

        // If only selected tasks should be fetched
        if (selectedTasks!=null){
            // If there is no task selected, return an empty list
            if (selectedTasks.isEmpty())
                return new ArrayList<>();
            selection = selection + " AND (";
            for (int taskId : selectedTasks){
                selection = selection + "(" + PetimoContract.Monitor.COLUMN_NAME_TASK_ID +
                        " = " + Integer.toString(taskId) + ") OR ";
            }
            // remove the last " OR "
            selection = selection.substring(0, selection.length()-4);
            selection = selection + ")";
        }

        String[] selectionArgs = {Integer.toString(startDate), Integer.toString(endDate)};

        String sortOrder = PetimoContract.Monitor.COLUMN_NAME_DATE + " " + sorting + ", " +
                PetimoContract.Monitor.COLUMN_NAME_START + " " + sorting;
        Cursor cursor = readableDb.query(PetimoContract.Monitor.TABLE_NAME,
                PetimoContract.Monitor.getAllColumns(), selection,
                selectionArgs, null, null, sortOrder);

        ArrayList<MonitorDay> days = new ArrayList<>();
        ArrayList<MonitorBlock> tmpBlocks = new ArrayList<>();
        int tmpDay = 0;
        boolean cursorMoved = false;
        while (cursor.moveToNext()){
            cursorMoved = true;
            if (tmpDay == 0)
                // The very first iteration
                tmpDay = cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_DATE));

            // Check if the cursor moves to the next date
            if (cursor.getInt(cursor.getColumnIndexOrThrow(
                    PetimoContract.Monitor.COLUMN_NAME_DATE)) != tmpDay){
                // Moved to the next date, so
                // bug! -> pass by value instead of pass by reference by default
                days.add(new MonitorDay(tmpDay, new ArrayList<>(tmpBlocks)));
                // Update the tmp date and empty the tmpBlocks
                tmpDay = cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_DATE));
                tmpBlocks.clear();
            }
            // else <- bug
            tmpBlocks.add(getBlockFromCursor(cursor));
        }

        if (cursorMoved){
            // Case there was some data fetched, so we have to add the last fetched day which is
            // "skipped" in the while loop above
            days.add(new MonitorDay(tmpDay, tmpBlocks));
        }
        cursor.close();

        return days;
    }

    /**
     * Generate a list of MonitorBlock objects that belong to the given date range
     * Database version: V.2
     * @param startDate
     * @param endDate
     * @return
     */
    public List<MonitorBlock> getBlocksByRange(int startDate, int endDate){
        String selection = PetimoContract.Monitor.COLUMN_NAME_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {Integer.toString(startDate), Integer.toString(endDate)};
        String sortOrder = PetimoContract.Monitor.COLUMN_NAME_DATE + " DESC , " +
                            PetimoContract.Monitor.COLUMN_NAME_START + " DESC";
        Cursor cursor = readableDb.query(PetimoContract.Monitor.TABLE_NAME,
                PetimoContract.Monitor.getAllColumns(), selection,
                selectionArgs, null, null, sortOrder);

        List<MonitorBlock> blocks = new ArrayList<>();
        while (cursor.moveToNext()) {
            blocks.add(getBlockFromCursor(cursor));
        }
        cursor.close();
        return blocks;
    }


    /**
     * Get the category object from the given category name
     * Database version: V.2
     * @param catId the ID of the category
     * @return the category object
     */
    public MonitorCategory getCatById(int catId){
        MonitorCategory category = null;
        String selection = PetimoContract.Categories._ID + " = ?";
        String[] selectionArgs = {Integer.toString(catId)};
        Cursor cursor = readableDb.query(PetimoContract.Categories.TABLE_NAME,
                PetimoContract.Categories.getAllColumns(),
                selection, selectionArgs, null, null, null, null);

        while(cursor.moveToNext())
            category = new MonitorCategory(
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_PRIORITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_STATUS)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_DELETE_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_NOTE)));
        cursor.close();
        return category;
    }

    /**
     * Fetch the name of the given category
     * Database version: V.2
     * @param catId ID of the given category
     * @return name of the given category
     */
    public String getCatNameById(int catId){
        String selection = PetimoContract.Categories._ID + " = ?";
        String[] selectionArgs = {Integer.toString(catId)};
        Cursor cursor = readableDb.query(PetimoContract.Categories.TABLE_NAME,
                new String[]{PetimoContract.Categories.COLUMN_NAME_NAME}, selection, selectionArgs,
                null, null,null);
        String catName = "";
        while (cursor.moveToNext())
            catName = cursor.getString(cursor.getColumnIndexOrThrow(
                    PetimoContract.Categories.COLUMN_NAME_NAME));
        cursor.close();
        return catName;
    }


    /**
     * Fetch the name of the given task
     * Database version: V.2
     * @param taskId ID of the given task
     * @return name of the given task
     */
    public String getTaskNameById(int taskId){
        String selection = PetimoContract.Tasks._ID + " = ?";
        String[] selectionArgs = {Integer.toString(taskId)};
        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                new String[]{PetimoContract.Tasks.COLUMN_NAME_NAME}, selection, selectionArgs,
                null, null,null);
        String taskName = "";
        while (cursor.moveToNext())
            taskName = cursor.getString(cursor.getColumnIndexOrThrow(
                    PetimoContract.Tasks.COLUMN_NAME_NAME));
        cursor.close();
        return taskName;
    }

    /**
     * Get a task object
     * @param taskName
     * @param catName
     * @return
     */
    @Deprecated
    public MonitorTask getTaskByName(String taskName, String catName){
        MonitorTask task = null;
        String selection = PetimoContract.Tasks.COLUMN_NAME_NAME + " = ? AND " +
                PetimoContract.Tasks.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {taskName, catName};
        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                PetimoContract.Tasks.getAllColumns(),
                selection, selectionArgs, null, null, null, null);
        while(cursor.moveToNext()){
            task = new MonitorTask(
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_NAME)),
                    getCatNameById(
                            cursor.getInt(cursor.getColumnIndexOrThrow(
                                    PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID))),
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_PRIORITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_STATUS)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_DELETED_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_NOTE)));
        }
        cursor.close();
        return task;
    }

    /**
     * Get a MonitorTask object from the given ID
     * Database version: V.2
     * @param taskId the ID of the task to fetch
     * @return
     */
    public MonitorTask getTaskById(int taskId){
        MonitorTask task = null;
        String selection = PetimoContract.Tasks._ID + " = ? ";
        String[] selectionArgs = {Integer.toString(taskId)};
        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                PetimoContract.Tasks.getAllColumns(),
                selection, selectionArgs, null, null, null, null);
        while(cursor.moveToNext()){
            task = new MonitorTask(
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_NAME)),
                    getCatNameById(
                            cursor.getInt(cursor.getColumnIndexOrThrow(
                                    PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID))),
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_PRIORITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_STATUS)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_DELETED_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_NOTE)));
        }
        cursor.close();
        return task;
    }

    /**
     * Return all tasks the belong to the given category
     * @param catName name of the category
     * @return  the list of all corresponding tasks
     */
    @Deprecated
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
     * Return all tasks the belong to the given category
     * Database version: V.2
     * @param catId ID of the corresponding category
     * @return  the list of all corresponding tasks
     */
    public List<MonitorTask> getTasksByCat(int catId){
        String selection = PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID + " = ?";
        String[] selectionArgs = {Integer.toString(catId)};
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
     * Return names of all the tasks that belong to the given cat
     * @param catName
     * @return
     */
    @Deprecated
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
     * Return names of all the tasks that belong to the given category.
     * @param catId the ID of the given category
     * @return
     */
    public List<String> getTaskNamesByCat(int catId){
        String selection = PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID + " = ?";
        String[] selectionArgs = {Integer.toString(catId)};
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
     * Return IDs of all the tasks that belong to the given category
     * Database version: V.2
     * @param catId the ID of the corresponding category
     * @return
     */
    public List<Integer> getTaskIdsByCat(int catId){
        String selection = PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID + " = ?";
        String[] selectionArgs = {Integer.toString(catId)};
        List<Integer> taskIds = new ArrayList<>();
        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                new String[]{PetimoContract.Tasks._ID}, selection,
                selectionArgs, null, null, null);

        while (cursor.moveToNext())
            taskIds.add(cursor.getInt(cursor.getColumnIndexOrThrow(
                    PetimoContract.Tasks._ID)));
        cursor.close();
        return taskIds;
    }


    /**
     * Find the category that contains the given task and return its ID
     * @param taskId
     * @return
     */
    public int getCatIdFromTask(int taskId){
        String selection = PetimoContract.Tasks._ID + " = ?";
        String[] selectionArgs = {Integer.toString(taskId)};
        int catId = -1;
        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                new String[]{PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID}, selection,
                selectionArgs, null, null, null);

        while (cursor.moveToNext())
            catId = cursor.getInt(cursor.getColumnIndexOrThrow(
                    PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID));
        cursor.close();
        return catId;
    }


    /**
     * Return a list of MonitorCategory objects that present all categories stored in the db
     * Database version: V.2
     * @return
     */
    public List<MonitorCategory> getAllCategories(){
        List<MonitorCategory> categories = new ArrayList<>();
        Cursor cursor = readableDb.query(PetimoContract.Categories.TABLE_NAME,
                PetimoContract.Categories.getAllColumns(), null, null, null, null, null, null);

        while(cursor.moveToNext())
            categories.add(new MonitorCategory(
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_PRIORITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_STATUS)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_DELETE_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_NOTE))));
        cursor.close();
        return categories;
    }

    /**
     * Generate a list of names of all the categories
     * @return
     */
    @Deprecated
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
     * Generate a list of IDs of all the categories
     * @return
     */
    public List<Integer> getAllCatIds(){
        List<Integer> catIds = new ArrayList<>();
        Cursor cursor = readableDb.query(PetimoContract.Categories.TABLE_NAME,
                new String[]{PetimoContract.Categories._ID}, null, null, null, null,
                PetimoContract.Categories._ID + " ASC", null);
        while(cursor.moveToNext())
            catIds.add(cursor.getInt(cursor.getColumnIndexOrThrow(
                    PetimoContract.Categories._ID)));
        cursor.close();
        return catIds;
    }

    /**
     * Generate a list of MonitorTask objects that represent all tasks stored in the db
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
     * Generate a list of IDs of all the tasks
     * @return
     */
    public ArrayList<Integer> getAllTaskIds(){
        ArrayList<Integer> taskIds = new ArrayList<>();
        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                new String[]{PetimoContract.Tasks._ID}, null, null, null, null,
                PetimoContract.Tasks._ID + " ASC", null);

        while(cursor.moveToNext())
            taskIds.add(cursor.getInt(cursor.getColumnIndexOrThrow(
                    PetimoContract.Tasks._ID)));
        cursor.close();
        return taskIds;
    }

    public int getCatIdFromName(String catName){
        String selection = PetimoContract.Categories.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {catName};
        int catId = -1;
        Cursor cursor = readableDb.query(PetimoContract.Categories.TABLE_NAME,
                new String[]{PetimoContract.Categories._ID}, selection,
                selectionArgs, null, null, null);

        while (cursor.moveToNext()){
            catId = cursor.getInt(cursor.getColumnIndexOrThrow(PetimoContract.Categories._ID));
            break;
        }
        cursor.close();
        return catId;
    }

    /**
     *
     * @param taskName
     * @param catId
     * @return
     */
    public int getTaskIdFromName(String taskName, int catId){
        String selection = PetimoContract.Tasks.COLUMN_NAME_NAME + " = ? AND " +
                PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID + " = ?";
        String[] selectionArgs = {taskName, Integer.toString(catId)};
        int taskId = -1;
        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                new String[]{PetimoContract.Tasks._ID}, selection,
                selectionArgs, null, null, null);

        while (cursor.moveToNext())
            taskId = cursor.getInt(cursor.getColumnIndexOrThrow(PetimoContract.Tasks._ID));
        cursor.close();
        return taskId;
    }

    //<---------------------------------------------------------------------------------------------
    // Core - Database - Deleting Data
    //--------------------------------------------------------------------------------------------->

    /**
     * Remove all monitor blocks that belong to the given date
     * @param date
     * @return
     */
    // unused
    public int removeDay(int date){
        String selection = PetimoContract.Monitor.COLUMN_NAME_DATE + " = ?";
        String[] selectionArgs = {Integer.toString(date)};
        return writableDb.delete(PetimoContract.Monitor.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Remove the monitor block which corresponds to the given ID
     * @param id
     * @return
     */
    public int removeBlockById(int id){
        String selection = PetimoContract.Monitor._ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        return writableDb.delete(PetimoContract.Monitor.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Remove a monitor task
     * Database version: V.2
     * @param taskId
     * @return
     */
    public int removeTask(int taskId){
        String selection = PetimoContract.Tasks._ID+ " = ? ";
        String[] selectionArgs = {Integer.toString(taskId)};
        return writableDb.delete(PetimoContract.Tasks.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Remove a monitor category
     * @param catId
     * @return
     */
    public int removeCategory(int catId){
        String selection = PetimoContract.Tasks._ID + " = ? ";

        String[] selectionArgs = {Integer.toString(catId)};
        return writableDb.delete(PetimoContract.Categories.TABLE_NAME, selection, selectionArgs);
    }

    //<---------------------------------------------------------------------------------------------
    // Core - Database - Tables
    //--------------------------------------------------------------------------------------------->


    /**
     * This will delete all stored data! Only use after backing up data!
     */
    public void resetDb(){
        writableDb.delete(PetimoContract.Categories.TABLE_NAME, null, null);
        writableDb.delete(PetimoContract.Tasks.TABLE_NAME, null, null);
        writableDb.delete(PetimoContract.Monitor.TABLE_NAME, null, null);
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
        // Verify this approach <= OK
        return (readableDb!=null && writableDb!=null);
    }

    /**
     * Check if a category with the given name exists
     * @param name
     * @return
     */
    @Deprecated
    public boolean checkCatExists(String name){
        return this.getAllCatNames().contains(name);
    }

    /**
     * Check if a category with the given ID exists
     * @param catId
     * @return true if the category exists, otherwise false
     */
    public boolean checkCatExists(int catId){
        return this.getAllCatIds().contains(catId);
    }

    /**
     * Check if there is a task with the given name and belongs to the given category.
     * @param catId
     * @param taskName
     * @return
     */
    public boolean checkTaskNameExists(int catId, String taskName){
        return this.getTaskNamesByCat(catId).contains(taskName);
    }

    /**
     * Check if there is a task with the given id and belongs to the given category.
     * @param catId
     * @param taskId
     * @return
     */
    public boolean checkTaskExists(int catId, int taskId){
        return this.getTaskIdsByCat(catId).contains(taskId);
    }

    /**
     * Check if there is a task with the given id
     * @param taskId
     * @return
     */
    public boolean checkTaskExists(int taskId){
        return this.getAllTaskIds().contains(taskId);
    }

    /**
     * TODO: -> DB V.2 : now we have to get name from the returned ids, adapt to this
     * Generate a xml representation of the whole databse
     */
    @Deprecated
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
                                PetimoContract.Monitor.COLUMN_NAME_CATEGORY)) + "' task_id='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_TASK_ID)) + "' category_id='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_CATEGORY_ID)) + "' start='" +
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
                                PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT)) + "' ovThreshold='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_OV_THRESHOLD)) + "' status='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_STATUS)) + "' note='" +
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_NOTE)) + "' />\n";
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

    private int findCatIdByName(String catName){
        String selection = PetimoContract.Categories.COLUMN_NAME_NAME + " = ? ";
        String selectionArgs[] = {catName};
        Cursor cursor = readableDb.query(PetimoContract.Categories.TABLE_NAME,
                new String[]{PetimoContract.Categories._ID}, selection, selectionArgs,
                null, null, null);
        int catId = -1;
        while (cursor.moveToNext())
            catId = cursor.getInt(cursor.getColumnIndexOrThrow(PetimoContract.Categories._ID));
        return catId;
    }


    private int findTaskIdByName(String taskName){
        String selection = PetimoContract.Tasks.COLUMN_NAME_NAME + " = ? ";
        String selectionArgs[] = {taskName};
        Cursor cursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                new String[]{PetimoContract.Tasks._ID}, selection, selectionArgs,
                null, null, null);
        int taskId = -1;
        while (cursor.moveToNext())
            taskId = cursor.getInt(cursor.getColumnIndexOrThrow(PetimoContract.Tasks._ID));
        return taskId;
    }

    //<---------------------------------------------------------------------------------------------
    // Core - Upgrade Database
    //--------------------------------------------------------------------------------------------->

    /**
     * Fill the newly inserted columns with the corresponding values
     */
    // unused
    public void updateV1toV2(){

        // Update Cats Table
        Cursor catsCursor = readableDb.query(PetimoContract.Categories.TABLE_NAME,
                PetimoContract.Categories.getAllColumns(), "1 = 1", null, null, null, null);
        while (catsCursor.moveToNext()){
            ContentValues values = new ContentValues();
            values.put(PetimoContract.Categories.COLUMN_NAME_STATUS, MonitorCategory.ACTIVE);
            Log.d(TAG, "updateV1toV2: Gonna update cat id ===> " +
                    catsCursor.getInt(catsCursor.getColumnIndexOrThrow(PetimoContract.Categories._ID)));

            int affectedRows = writableDb.update(PetimoContract.Categories.TABLE_NAME, values,
                    PetimoContract.Categories._ID + " = " + Integer.toString(
                            catsCursor.getInt(catsCursor.getColumnIndexOrThrow(
                                    PetimoContract.Categories._ID))),
                    null);
            Log.d(TAG, "done, affectedRows =====> " + affectedRows);
        }
        catsCursor.close();

        // Update Tasks Table
        Cursor tasksCursor = readableDb.query(PetimoContract.Tasks.TABLE_NAME,
                PetimoContract.Tasks.getAllColumns(), "1 = 1", null, null, null, null);
        while (tasksCursor.moveToNext()){
            ContentValues values = new ContentValues();
            values.put(PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID,
                    findCatIdByName(tasksCursor.getString(tasksCursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_CATEGORY))));
            values.put(PetimoContract.Tasks.COLUMN_NAME_STATUS, MonitorTask.ACTIVE);
            Log.d(TAG, "updateV1toV2: Gonna update task id ===> " +
                    tasksCursor.getInt(tasksCursor.getColumnIndexOrThrow(PetimoContract.Tasks._ID)));

            int affectedRows = writableDb.update(PetimoContract.Tasks.TABLE_NAME, values,
                    PetimoContract.Tasks._ID + " = " + Integer.toString(
                            tasksCursor.getInt(tasksCursor.getColumnIndexOrThrow(
                                    PetimoContract.Tasks._ID))),
                    null);
            Log.d(TAG, "done, affectedRows =====> " + affectedRows);
        }
        tasksCursor.close();

        // Update Monitor Table
        // Select everything from Monitor table, can also do this with raw query
        Cursor monitorsCursor = readableDb.query(PetimoContract.Monitor.TABLE_NAME,
                PetimoContract.Monitor.getAllColumns(), "1 = 1", null, null, null, null);
        while (monitorsCursor.moveToNext()){
            ContentValues values = new ContentValues();
            values.put(PetimoContract.Monitor.COLUMN_NAME_CATEGORY_ID,
                    findCatIdByName(monitorsCursor.getString(monitorsCursor.getColumnIndexOrThrow(
                            PetimoContract.Monitor.COLUMN_NAME_CATEGORY))));
            values.put(PetimoContract.Monitor.COLUMN_NAME_TASK_ID,
                    findTaskIdByName(monitorsCursor.getString(monitorsCursor.getColumnIndexOrThrow(
                            PetimoContract.Monitor.COLUMN_NAME_TASK))));
            // Set to default
            values.put(PetimoContract.Monitor.COLUMN_NAME_STATUS, MonitorBlock.ACTIVE);
            values.put(PetimoContract.Monitor.COLUMN_NAME_OV_THRESHOLD, 6);
            // Update the row
            Log.d(TAG, "updateV1toV2: Gonna update monitor id ===> " +
                    monitorsCursor.getInt(monitorsCursor.getColumnIndexOrThrow(PetimoContract.Monitor._ID)));

            int affectedRows = writableDb.update(PetimoContract.Monitor.TABLE_NAME, values,
                    PetimoContract.Monitor._ID + " = " + Integer.toString(
                            monitorsCursor.getInt(monitorsCursor.getColumnIndexOrThrow(PetimoContract.Monitor._ID))),
                    null);
            Log.d(TAG, "done, affectedRows =====> " + affectedRows);
        }
        monitorsCursor.close();

    }

    //<---------------------------------------------------------------------------------------------
    // Auxiliary
    //--------------------------------------------------------------------------------------------->

    /**
     * Return a monitor block from the given cursor. The cursor must have moved to the corresponding
     * position.
     * Database version: V.2
     * @param cursor the given cursor
     * @return a monitor block from the given cursor
     */
    private MonitorBlock getBlockFromCursor(Cursor cursor){
        return new MonitorBlock(
                cursor.getInt(cursor.getColumnIndexOrThrow(PetimoContract.Monitor._ID)),
                getTaskNameById(
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_TASK_ID))),
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_TASK_ID)),
                getCatNameById(
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                PetimoContract.Monitor.COLUMN_NAME_CATEGORY_ID))),
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_CATEGORY_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_START)),
                cursor.getLong(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_END)),
                cursor.getLong(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_DURATION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_DATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_WEEKDAY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_OV_THRESHOLD)),
                cursor.getString(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_STATUS)),
                cursor.getString(cursor.getColumnIndexOrThrow(
                        PetimoContract.Monitor.COLUMN_NAME_NOTE)));
    }

    /**
     * Return a monitor task from the given cursor. The cursor must have moved to the corresponding
     * position.
     * Database version: V.2
     * @param cursor the given cursor
     * @return a monitor task from the given cursor
     */
    private MonitorTask getTaskFromCursor(Cursor cursor){
        return new MonitorTask(
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks.COLUMN_NAME_NAME)),
                getCatNameById(
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID))),
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks.COLUMN_NAME_CATEGORY_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks.COLUMN_NAME_PRIORITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks.COLUMN_NAME_STATUS)),
                cursor.getLong(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks.COLUMN_NAME_DELETED_TIME)),
                cursor.getString(cursor.getColumnIndexOrThrow(
                        PetimoContract.Tasks.COLUMN_NAME_NOTE)));
    }

    /**
     * TODO: implement & consider to move it to somewhere else
     * Check if the given category/task name is valid
     * @param name
     * @return
     */
    private boolean checkName(String name){
        boolean result = true;
        if (name.isEmpty())
            result = false;
        // Check if the name does not contain only whitespaces

        return result;
    }

    public static final class Sort {
        public static final String DESC = "DESC";
        public static final String ASC = "ASC";
    }
}

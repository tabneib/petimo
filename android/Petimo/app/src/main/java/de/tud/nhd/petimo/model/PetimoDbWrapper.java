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

import de.tud.nhd.petimo.controller.ResponseCode;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.controller.exception.InvalidInputNameException;

/**
 * Created by nhd on 28.08.17.
 */

public class PetimoDbWrapper {

    private static final String TAG = "PetimoDbWrapper";
    private static PetimoDbWrapper _instance = null;
    private SQLiteDatabase writableDb = null;
    private SQLiteDatabase readableDb = null;
    private SQLiteOpenHelper dbHelper = null;

    private static Context context;

    //<---------------------------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------------------------->

    private PetimoDbWrapper(Context context){
        dbHelper = new PetimoDbHelper(context);
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
     * @return The corresponding response code
     */
    public ResponseCode insertCategory(String name, int priority)
            throws DbErrorException, InvalidInputNameException, InvalidCategoryException {
        if (!checkName(name))
            throw new InvalidInputNameException(
                    "Invalid input category name: " + name + " - please use other name");
        if (checkCatExists(name))
            throw new InvalidCategoryException("Category already exists: " + name);
        ContentValues values = new ContentValues();
        values.put(PetimoContract.Categories.COLUMN_NAME_NAME, name);
        values.put(PetimoContract.Categories.COLUMN_NAME_PRIORITY, priority);
        if(writableDb.insert(PetimoContract.Categories.TABLE_NAME, null, values) == -1)
            throw new DbErrorException("Some DB error has occured, please try again");
        else
            return ResponseCode.OK;
    }

    /**
     *
     * @param task
     * @param category
     * @param priority
     * @return the corresponding response code
     */
    public ResponseCode insertTask(String task, String category, int priority)
            throws DbErrorException, InvalidInputNameException, InvalidCategoryException {
        if(!checkName(task))
            throw new InvalidInputNameException("The input task name is invalid: " + task +
                    " - please use other task name");
        if (!checkCatExists(category))
            throw new InvalidCategoryException("Category does not exist: " + category);
        if (checkTaskExists(category, task))
            throw new InvalidCategoryException("Task already exists for the given category: "
                    + category + " / " + task);
        else{
            ContentValues values = new ContentValues();
            values.put(PetimoContract.Tasks.COLUMN_NAME_NAME, task);
            values.put(PetimoContract.Tasks.COLUMN_NAME_CATEGORY, category);
            values.put(PetimoContract.Tasks.COLUMN_NAME_PRIORITY, priority);
            if(writableDb.insert(PetimoContract.Tasks.TABLE_NAME, null, values) == -1)
                throw new DbErrorException("Some DB error has occured, please try again");
            else
                return ResponseCode.OK;
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
     * @return The corresponding response code
     */
    public ResponseCode insertMonitorBlock(
            String task, String category, long start, long end,
            long duration, int date, int weekDay, int overNight)
            throws DbErrorException, InvalidCategoryException {

        if (!checkCatExists(category)) {
            throw new InvalidCategoryException("Category already exists: " + category);
        }

        if (task != null && !checkTaskExists(category, task)){
            throw new InvalidCategoryException("Task already exists for the given category: "
                    + category + " / " + task);
        }
        ContentValues values = new ContentValues();
        values.put(PetimoContract.Monitor.COLUMN_NAME_TASK, task);
        values.put(PetimoContract.Monitor.COLUMN_NAME_CATEGORY, category);
        values.put(PetimoContract.Monitor.COLUMN_NAME_START, start);
        values.put(PetimoContract.Monitor.COLUMN_NAME_END, end);
        values.put(PetimoContract.Monitor.COLUMN_NAME_DURATION, duration);
        values.put(PetimoContract.Monitor.COLUMN_NAME_DATE, date);
        values.put(PetimoContract.Monitor.COLUMN_NAME_WEEKDAY, weekDay);
        values.put(PetimoContract.Monitor.COLUMN_NAME_OVERNIGHT, overNight);
        if (writableDb.insert(PetimoContract.Monitor.TABLE_NAME, null, values) == -1)
            throw new DbErrorException("Some DB error has occured, please try again");
        else {
            Log.d(TAG, " Inserted Block: \nDate: " + date + "\nstart: " + start + "\nend: " +
                    end + "\ncat: " + category + "\ntask: " + task + "\nduration: " +
                    PetimoTimeUtils.getTimeFromMs(duration));
            return ResponseCode.OK;
        }

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
    public ArrayList<MonitorDay> getDaysByRange(int startDate, int endDate, boolean selectedTasks){
        String selection = PetimoContract.Monitor.COLUMN_NAME_DATE + " BETWEEN ? AND ?";

        // If only selected taks should be fetched
        if (selectedTasks){
            // If there is no task selected, return an empty list
            if (PetimoSharedPref.getInstance().getSelectedTasks().isEmpty())
                return new ArrayList<>();
            selection = selection + " AND ";
            for (String[] catTask : PetimoSharedPref.getInstance().getSelectedTasks()){
                selection = selection + "((" + PetimoContract.Monitor.COLUMN_NAME_CATEGORY +
                        " = '" + catTask[0] + "') AND (" + PetimoContract.Monitor.COLUMN_NAME_TASK +
                        " = '" + catTask[1] + "')) OR ";
            }
            // remove the last " OR "
            selection = selection.substring(0, selection.length()-4);
        }

        String[] selectionArgs = {Integer.toString(startDate), Integer.toString(endDate)};
        String sortOrder = PetimoContract.Monitor.COLUMN_NAME_DATE + " DESC";
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
     *
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
     * @param catName the name of the category
     * @return the category object
     */
    public MonitorCategory getCatByName(String catName){
        MonitorCategory category = null;
        String selection = PetimoContract.Categories.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = {catName};
        Cursor cursor = readableDb.query(PetimoContract.Categories.TABLE_NAME,
                PetimoContract.Categories.getAllColumns(),
                selection, selectionArgs, null, null, null, null);

        while(cursor.moveToNext()){
            category = new MonitorCategory(
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Categories.COLUMN_NAME_PRIORITY)));
        }
        cursor.close();
        return category;
    }

    /**
     * Get a task object
     * @param taskName
     * @param catName
     * @return
     */
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
                    cursor.getString(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_CATEGORY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(
                            PetimoContract.Tasks.COLUMN_NAME_PRIORITY)));
        }
        cursor.close();
        return task;
    }

    /**
     * Return all tasks the belong to the given category
     * @param catName name of the category
     * @return  the list of all corresponding tasks
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

    /**
     * Remove a monitor task
     * @param taskName
     * @param catName
     * @return
     */
    public int removeTask(String taskName, String catName){
        String selection = PetimoContract.Tasks.COLUMN_NAME_NAME + " = ? AND " +
                PetimoContract.Tasks.COLUMN_NAME_CATEGORY + " = ? ";
        String[] selectionArgs = {taskName, catName};
        return writableDb.delete(PetimoContract.Tasks.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Remove a monitor category
     * @param catName
     * @return
     */
    public int removeCategory(String catName){
        String selection = PetimoContract.Tasks.COLUMN_NAME_NAME + " = ? ";

        String[] selectionArgs = {catName};
        return writableDb.delete(PetimoContract.Categories.TABLE_NAME, selection, selectionArgs);
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
    public boolean checkTaskExists(String category, String task){
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

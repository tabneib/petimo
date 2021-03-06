package de.tud.nhd.petimo.model.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.SettingsException;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.utils.StringParsingException;
import de.tud.nhd.petimo.utils.PetimoStringUtils;

/**
 * Created by nhd on 01.09.17.
 */
@Deprecated
public class SharedPref {

    private static final String TAG = "SharedPref";
    private final String MONITOR_LIVE_DATE =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITOR_LIVE_DATE";
    private final String MONITOR_LIVE_START =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITOR_LIVE_START";
    private final String MONITOR_LIVE_CAT =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITOR_LIVE_CAT";
    private final String MONITOR_LIVE_TASK =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITOR_LIVE_TASK";
    private final String MONITOR_MONITORED_TASKS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITOR_MONITORED_TASKS";
    private final String MONITOR_LAST_MONITORED_CATEGORY =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITOR_LAST_MONITORED_CATEGORY";
    private final String MONITOR_LAST_MONITORED_TASK =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITOR_LAST_MONITORED_TASK";

    //------------------------------- User's choices ---------------------------------------------->
    public static final String SETTINGS_MONITORED_TASKS_SORT_ORDER =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_TASKS_SORT_ORDER";

    public static final String SETTINGS_MONITORED_BLOCKS_REMEMBER =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_BLOCKS_REMEMBER";
    public static final String SETTINGS_MONITORED_BLOCKS_LOCK =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_BLOCKS_LOCK";
    public static final String SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_BLOCKS_SHOW_SELECTED_TASKS";
    public static final String SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.MONITORED_BLOCKS_SHOW_EMPTY_DAYS";
    public static final String SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS";

    public static final String SETTINGS_LANGUAGE =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.LANGUAGE";
    public static final String LANG_VI = "vi";
    public static final String LANG_EN = "en";
    public static final String LANG_DE = "de";
    // This order is fixed
    public static final ArrayList<String> LANGUAGES =
            new ArrayList(Arrays.asList(new String[]{LANG_EN, LANG_DE, LANG_VI}));

    public static final String SETTINGS_OVERNIGHT_THRESHOLD =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.OVERNIGHT_THRESHOLD";

    private final int DEFAULT_OVERNIGHT_THRESHOLD = 6;


    private static SharedPref _instance;
    private static Context context;

    private SharedPreferences settingPref;
    private SharedPreferences monitorPref;
    private SharedPreferences.Editor settingsEditor;
    private SharedPreferences.Editor monitorEditor;

    public static final String TIME = "time";
    public static final String FREQUENCY = "frequency";
    public static final String UNSORTED = "none";
    //<---------------------------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------------------------->

    private SharedPref(Context c){
        this.context = c;
        this.settingPref = this.context.getSharedPreferences(
                this.context.getString(R.string.preference_file_settings), Context.MODE_PRIVATE);
        this.monitorPref = this.context.getSharedPreferences(
                this.context.getString(R.string.preference_file_monitor), Context.MODE_PRIVATE);
        this.settingsEditor = settingPref.edit();
        this.monitorEditor = monitorPref.edit();
    }

    public static void initialize(Context kontext) throws Exception{
        if(_instance != null)
            throw new Exception("Cannot initialize multiple instances of SharedPref!");
        else {
            context = kontext;
        }
    }

    public static SharedPref getInstance() {
        if (_instance == null) {
            _instance = new SharedPref(context);
            return _instance;
        }
        else
            return _instance;
    }

    //<---------------------------------------------------------------------------------------------
    // Monitor Preferences
    // -------------------------------------------------------------------------------------------->

    //------------------------------------------ Write -------------------------------------------->

    /**
     * Save information about the ongoing monitor into the preferences
     * Database version: V.2
     * @param catId     category of the ongoing monitor
     * @param taskId    task of the ongoing monitor
     * @param date      date of the ongoing monitor
     * @param start     start of the ongoing monitor
     */
    public void setLiveMonitor(int catId, int taskId, int date, long start){
        monitorEditor.putInt(MONITOR_LIVE_CAT, catId);
        monitorEditor.putInt(MONITOR_LIVE_TASK, taskId);
        monitorEditor.putInt(MONITOR_LIVE_DATE, date);
        monitorEditor.putLong(MONITOR_LIVE_START, start);
        monitorEditor.apply();
    }

    /**
     * Clear all saved preferences about the ongoing live monitor
     */
    public void clearLiveMonitor(){
        monitorEditor.remove(MONITOR_LIVE_CAT);
        monitorEditor.remove(MONITOR_LIVE_TASK);
        monitorEditor.remove(MONITOR_LIVE_DATE);
        monitorEditor.remove(MONITOR_LIVE_START);
        monitorEditor.apply();
    }

    /**
     * Save or update information about monitored task
     * Database version: V.2
     * @param catId  the category of the given monitored task
     * @param taskId  the task of the given monitored task
     * @param monitorTime the time point the monitor stopped
     */
    public void updateMonitoredTask(int catId, int taskId, long monitorTime){
        ArrayList<String[]> monitoredTasks = this.getMonitored(this.UNSORTED);
        boolean notExist = true;
        if (monitoredTasks!=null){
            for (String[] item : monitoredTasks){
                if(item[0].equals(Integer.toString(catId)) &&
                        item[1].equals(Integer.toString(taskId))){
                    // update time
                    item[2] = Long.toHexString(monitorTime);
                    // update frequency;
                    item[3] = Integer.toString(Integer.parseInt(item[3]) + 1);
                    notExist = false;
                    break;
                }
            }
            if (notExist){
                // Add a new item to the monitored task list
                monitoredTasks.add(
                        new String[]{Integer.toString(catId), Integer.toString(taskId),
                                Long.toHexString(monitorTime), Integer.toString(1)});
            }
        }
        else{
            // initialize
            monitoredTasks = new ArrayList<>();
            monitoredTasks.add(
                    new String[]{Integer.toString(catId), Integer.toString(taskId),
                            Long.toHexString(monitorTime), Integer.toString(1)});
        }

        monitorEditor.putString(
                MONITOR_MONITORED_TASKS, PetimoStringUtils.encode(monitoredTasks, 4));
        monitorEditor.apply();
    }


    /**
     * Remove all saved monitored tasks
     */
    public void clearMonitoredTasks(){
        monitorEditor.remove(MONITOR_MONITORED_TASKS);
        monitorEditor.apply();
    }

    /**
     * Save the sort order chosen by user to display monitored task.
     * If the given value is UNSORTED or invalid, the sort order is set to UNSORTED
     * @param sortOrder the given sort order
     */
    public void setUsrMonitoredSortOrder(String sortOrder){
        switch (sortOrder){
            case FREQUENCY:
                settingsEditor.putString(SETTINGS_MONITORED_TASKS_SORT_ORDER, FREQUENCY);
                break;
            case TIME:
                settingsEditor.putString(SETTINGS_MONITORED_TASKS_SORT_ORDER, TIME);
                break;
            default:
                settingsEditor.putString(SETTINGS_MONITORED_TASKS_SORT_ORDER, UNSORTED);
        }
        settingsEditor.apply();
    }

    /**
     * Stored the last monitored task
     * @param cat   category of the task
     * @param task  the task
     */
    /*@Deprecated
    public void setLastMonitored(String cat, String task){
        monitorEditor.putString(MONITOR_LAST_MONITORED_CATEGORY, cat);
        monitorEditor.putString(MONITOR_LAST_MONITORED_TASK, task);
        monitorEditor.apply();
    }*/

    /**
     * Stored the last monitored task
     * Database version: V.2
     * @param catId   ID of the category of the task
     * @param taskId  ID of the task
     */
    public void setLastMonitored(int catId, int taskId){
        monitorEditor.putInt(MONITOR_LAST_MONITORED_CATEGORY, catId);
        monitorEditor.putInt(MONITOR_LAST_MONITORED_TASK, taskId);
        monitorEditor.apply();
    }


    /**
     * Database version: V.2
     * @param taskId
     */
    public void addSelectedTask(int taskId){
        ArrayList<Integer> selectedTasks = this.getSelectedTasks();
        if (selectedTasks!=null){
            // If the cat/task is already there then do nothing
            if(selectedTasks.contains(taskId))
                return;
            // Add a new item to the selected task list
            selectedTasks.add(taskId);
        }
        else{
            // initialize
            selectedTasks = new ArrayList<>();
            selectedTasks.add(taskId);
        }
        settingsEditor.putString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS,
                PetimoStringUtils.encodeIntList(selectedTasks, 1));
        settingsEditor.apply();
    }

    /**
     * Database version: V.2
     * @param taskId
     */
    public void removeSelectedTask(int taskId){
        ArrayList<Integer> selectedTasks = this.getSelectedTasks();
        if (selectedTasks!=null){
            boolean contained = false;
            Iterator<Integer> selectedTasksIterator = selectedTasks.iterator();
            while(selectedTasksIterator.hasNext()){
                int item = selectedTasksIterator.next();
                if(item == taskId){
                    contained = true;
                    selectedTasksIterator.remove();
                }
            }
            if (contained){
                if (selectedTasks.isEmpty())
                    // The last selected Task is removed, clear sharedPref entry
                    settingsEditor.putString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS, null);
                else
                    settingsEditor.putString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS,
                            PetimoStringUtils.encodeIntList(selectedTasks, 1));
                settingsEditor.apply();
            }
            else
                // If the task is not there then do nothing
                return;
        }
        return;

    }

    //------------------------------------------- Read -------------------------------------------->

    /**
     * Return the category id of the ongoing monitor
     * Database version: V.2
     * @return the category id, or -1 if there is no ongoing monitor
     */
    public int getMonitorCatId(){
        return monitorPref.getInt(MONITOR_LIVE_CAT, -1);
    }

    /**
     * Database version: V.2
     * Return the task id of the ongoing monitor
     * @return the task id, or -1 if there is no ongoing monitor
     */
    public int getMonitorTaskId(){
        return monitorPref.getInt(MONITOR_LIVE_TASK, -1);
    }

    /**
     * Return the date of the ongoing monitor
     * @return the date, or 0 if there is no ongoing monitor
     */
    public int getMonitorDate(){
        return monitorPref.getInt(MONITOR_LIVE_DATE, 0);
    }

    /**
     * Return the start time of the ongoing monitor
     * @return the start time, or 0 if there is no ongoing monitor
     */
    public long getMonitorStart(){
        return monitorPref.getLong(MONITOR_LIVE_START, 0);
    }

    /**
     * Check if there is an ongoing live monitor
     * @return true if there is an ongoing live monitor, false otherwise
     */
    public boolean isMonitoring(){
        return !(monitorPref.getLong(MONITOR_LIVE_START, -1) == -1);
    }

    /**
     * Return a list of all monitored tasks
     * Database version: V.2
     * @return  the list of monitored tasks, or null if there is no saved monitored task or
     *          some error occurs.
     *          Each item contains the category ID, the task ID, last monitored time, and the
     *          monitor frequency
     */
    public ArrayList<String[]> getMonitored(String sortOpt){

        try{
            ArrayList<String[]> monitoredTaskList = PetimoStringUtils.parse(
                    monitorPref.getString(MONITOR_MONITORED_TASKS, null), 4);
            // Remove all cat / task that don't exist anymore
            Iterator<String[]> monitoredTaskIterator = monitoredTaskList.iterator();
            while (monitoredTaskIterator.hasNext()) {
                String[] catTask = monitoredTaskIterator.next();

                // Check if the cat/task is not yet removed (by the user)
                if (!PetimoDbWrapper.getInstance().checkCatExists(Integer.parseInt(catTask[0])) ||
                        !PetimoDbWrapper.getInstance().checkTaskExists(
                                Integer.parseInt(catTask[0]), Integer.parseInt(catTask[1]))) {
                    monitoredTaskIterator.remove();
                    //removeMonitoredTask(Integer.parseInt(catTask[0]), Integer.parseInt(catTask[1]));
                }
            }
            switch (sortOpt){
                case TIME:
                    // DESC sort by time
                    Collections.sort(monitoredTaskList,
                            Collections.reverseOrder(new Comparator<String[]>() {
                        @Override
                        public int compare(String[] o1, String[] o2) {
                            if (Long.parseLong(o1[2],16) > Long.parseLong(o2[2], 16))
                                return 1;
                            else if (Long.parseLong(o1[2],16) < Long.parseLong(o2[2],16))
                                return -1;
                            else
                                return 0;
                        }
                    }));
                    return monitoredTaskList;

                case FREQUENCY:
                    // DESC sort by frequency
                    Collections.sort(monitoredTaskList,
                            Collections.reverseOrder(new Comparator<String[]>() {
                                @Override
                                public int compare(String[] o1, String[] o2) {
                                    if (Integer.parseInt(o1[3]) > Integer.parseInt(o2[3]))
                                        return 1;
                                    else if (Integer.parseInt(o1[3]) < Integer.parseInt(o2[3]))
                                        return -1;
                                    else
                                        return 0;
                                }
                            }));
                    return monitoredTaskList;
                default:
                    return monitoredTaskList;
            }
        }
        catch (StringParsingException e){
            return null;
        }
    }


    /**
     * Return a list of all selected cat/task to display on EditBlockFragment
     * Database version: V.2
     * @return
     */
    public ArrayList<Integer> getSelectedTasks(){

        try{
            ArrayList<String[]> selectedTasksTmp = PetimoStringUtils.parse(
                    settingPref.getString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS, null), 1);

            // Convert to int list
            ArrayList<Integer> selectedTasks = new ArrayList<>();
            for (String[] item : selectedTasksTmp)
                    selectedTasks.add(Integer.parseInt(item[0]));

            // Remove all tasks that don't exist anymore
            Iterator<Integer> selectedTaskIterator = selectedTasks.iterator();
            while (selectedTaskIterator.hasNext()) {
                int taskId = selectedTaskIterator.next();
                // Check if the task is not yet removed (by the user)
                if (!PetimoDbWrapper.getInstance().checkTaskExists(taskId))
                    selectedTaskIterator.remove();
                    //removeMonitoredTask(catTask[0], catTask[1]);
            }
            return selectedTasks;
        }
        catch (StringParsingException e){
            return new ArrayList<>();
        }
    }

    /**
     * Get the last monitored task
     * Database version: V.2
     * @return a Integer array containing the category and task Ids,
     *          null if no category/task is stored
     */
    public int[] getLastMonitoredTask(){
        int catId = monitorPref.getInt(MONITOR_LAST_MONITORED_CATEGORY, -1);
        int taskId = monitorPref.getInt(MONITOR_LAST_MONITORED_TASK, -1);

        if (catId != -1 && taskId != -1 && PetimoDbWrapper.getInstance().checkCatExists(catId) &&
                PetimoDbWrapper.getInstance().checkTaskExists(catId, taskId))
                return new int[]{catId, taskId};
        return new int[]{-1, -1};
    }


    /**
    * Get the sort order chosen by user for displaying monitored tasks
    * @return the sort order
    */
    public String getUsrMonitoredSortOrder(){
        switch (settingPref.getString(SETTINGS_MONITORED_TASKS_SORT_ORDER, UNSORTED)){
            case FREQUENCY:
                return FREQUENCY;
            case TIME:
                return TIME;
            default:
                return TIME;
        }
    }

    //<---------------------------------------------------------------------------------------------
    // Settings Preferences
    // -------------------------------------------------------------------------------------------->

    //------------------------------------------- Write -------------------------------------------->

    /**
     *
     * @param tag
     * @param content
     */
    public void setSettingsInt(String tag, int content){
        switch (tag){
            case SETTINGS_OVERNIGHT_THRESHOLD:
                settingsEditor.putInt(tag, content);
                settingsEditor.apply();
                break;
            default:
                throw new SettingsException("putInt: Unknown settings tag ==> " + tag);
        }
    }

    /**
     *
     * @param tag
     * @param content
     */
    public void setSettingsString(String tag, String content){
        switch (tag){
            case SETTINGS_MONITORED_TASKS_SORT_ORDER:
                settingsEditor.putString(tag, content);
                settingsEditor.apply();
                break;
            case SETTINGS_LANGUAGE:
                switch (content){
                    case LANG_VI:
                    case LANG_DE:
                        settingsEditor.putString(tag, content);
                        break;
                    default:
                        // Default is English
                        settingsEditor.putString(tag, LANG_EN);
                }
                settingsEditor.apply();
                break;
            default:
                throw new SettingsException("putString: Unknown settings tag ==> " + tag);
        }
    }

    /**
     *
     * @param tag
     * @param content
     */
    public void setSettingsBoolean(String tag, boolean content){
        switch (tag){
            case SETTINGS_MONITORED_BLOCKS_LOCK:
            case SETTINGS_MONITORED_BLOCKS_REMEMBER:
            case SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS:
            case SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS:
                settingsEditor.putBoolean(tag, content);
                settingsEditor.apply();
                break;
            default:
                throw new SettingsException("putBoolean: Unknown settings tag ==> " + tag);
        }
    }

    //------------------------------------------- Read -------------------------------------------->

    /**
     *
     * @param tag
     * @param defaultValue
     * @return
     */
    public int getSettingsInt(String tag, int defaultValue){
        switch (tag){
            case SETTINGS_OVERNIGHT_THRESHOLD:
                return settingPref.getInt(tag, defaultValue);
            default:
                throw new SettingsException("getInt: Unknown settings tag ==> " + tag);
        }
    }

    /**
     *
     * @param tag
     * @param defaultValue TODO passing null to use app default value
     * @return
     */
    public String getSettingsString(String tag, String defaultValue){
        switch (tag){
            case SETTINGS_MONITORED_TASKS_SORT_ORDER:
            case SETTINGS_LANGUAGE:
                return settingPref.getString(tag, defaultValue);
            default:
                throw new SettingsException("getString: Unknown settings tag ==> " + tag);
        }
    }

    /**
     *
     * @param tag
     * @param defaultValue
     * @return
     */
    public boolean getSettingsBoolean(String tag, boolean defaultValue){
        switch (tag){
            case SETTINGS_MONITORED_BLOCKS_LOCK:
            case SETTINGS_MONITORED_BLOCKS_REMEMBER:
            case SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS:
            case SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS:
                return settingPref.getBoolean(tag, defaultValue);
            default:
                throw new SettingsException("getBoolean: Unknown settings tag ==> " + tag);
        }
    }

    /**
     * Get the overnight threshold
     * @return the threshold
     * TODO remove this method, use generic method above instead
     */
    public int getOvThreshold(){
        return settingPref.getInt(SETTINGS_OVERNIGHT_THRESHOLD, DEFAULT_OVERNIGHT_THRESHOLD);
    }

    //<---------------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------------->

    /**
     * Update the way Petimo stores data in SharedPreferences due to Database upgrade/update from
     * V.1 to V.2. This is to avoid user data loss
     * Hard-coded
     */
    public void updateV1toV2(){

        // Update saved last monitored task
        /*try{
            String catName = monitorPref.getString(MONITOR_LAST_MONITORED_CATEGORY, null);
            String taskName = monitorPref.getString(MONITOR_LAST_MONITORED_TASK, null);
            if (catName != null && taskName != null) {
                int catId = PetimoDbWrapper.getInstance().getCatIdFromName(catName);
                int taskId = PetimoDbWrapper.getInstance().getTaskIdFromName(taskName, catId);

                monitorEditor.putInt(MONITOR_LAST_MONITORED_CATEGORY, catId);
                monitorEditor.putInt(MONITOR_LAST_MONITORED_TASK, taskId);
                monitorEditor.apply();
            }
            Log.d(TAG, "updateV1toV2: lastMonitoredTask is updated:");
            Log.d(TAG, "Last cat/task ====> " +
                    monitorPref.getInt(MONITOR_LAST_MONITORED_CATEGORY, -1) + " / " +
                    monitorPref.getInt(MONITOR_LAST_MONITORED_TASK, -1));
        }
        catch(ClassCastException e){
            e.printStackTrace();
            Log.d(TAG, "updateV1toV2: lastMonitoredTask is already up-to-date");
        }*/

        // Update saved selected tasks
        /*
        try{
            ArrayList<String[]> oldSelectedTasks = PetimoStringUtils.parse(
                    settingPref.getString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS, null), 2);

            ArrayList<Integer> newSelectedTasks = new ArrayList<>();
            for (String[] catTask: oldSelectedTasks)
                newSelectedTasks.add(PetimoDbWrapper.getInstance().getTaskIdFromName(
                        catTask[1], PetimoDbWrapper.getInstance().getCatIdFromName(catTask[0])));
            monitorEditor.putString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS,
                    PetimoStringUtils.encodeIntList(newSelectedTasks, 1));
            monitorEditor.apply();

            Log.d(TAG, "updateV1toV2: saved selected tasks is updated:");
            for (String[] item: PetimoStringUtils.parse(
                    settingPref.getString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS, null), 2))
                Log.d(TAG, Arrays.toString(item));

        }
        catch (StringParsingException e){
            e.printStackTrace();
            Log.d(TAG, "updateV1toV2: lastMonitoredTask is probably already up-to-date");
        }*/

        // Update saved monitored tasks
        /*try{
            ArrayList<String[]> oldMonitoredTaskList = PetimoStringUtils.parse(
                    monitorPref.getString(MONITOR_MONITORED_TASKS, null), 4);
            ArrayList<String[]> newMonitoredTaskList = new ArrayList<>();
            for (String[] monitoredTask: oldMonitoredTaskList){
                int catId = PetimoDbWrapper.getInstance().getCatIdFromName(monitoredTask[0]);
                int taskId =
                        PetimoDbWrapper.getInstance().getTaskIdFromName(monitoredTask[0], catId);
                newMonitoredTaskList.add(new String[]{
                        Integer.toString(catId), Integer.toString(taskId),
                        monitoredTask[2], monitoredTask[3]});
            }
            monitorEditor.putString(MONITOR_MONITORED_TASKS,
                    PetimoStringUtils.encode(newMonitoredTaskList, 4));
            monitorEditor.apply();
            Log.d(TAG, "updateV1toV2: Monitored tasks is updated:");
            for (String[] item : PetimoStringUtils.parse(
                    monitorPref.getString(MONITOR_MONITORED_TASKS, null), 4))
                Log.d(TAG, Arrays.toString(item));
        }
        catch (StringParsingException e){
            e.printStackTrace();
            Log.d(TAG, "updateV1toV2: lastMonitoredTask is probably already up-to-date");
        }*/

    }
}

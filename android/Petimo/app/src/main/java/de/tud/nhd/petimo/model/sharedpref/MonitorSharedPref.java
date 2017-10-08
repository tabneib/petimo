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
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.utils.PetimoStringUtils;
import de.tud.nhd.petimo.utils.StringParsingException;

/**
 * Created by nhd on 08.10.17.
 */

public class MonitorSharedPref extends PetimoSharedPref {

    private static final String TAG = "MonitorSharedPref";
    MonitorSharedPref _instance = null;

    private SharedPreferences monitorPref;
    private SharedPreferences.Editor monitorEditor;


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


    public MonitorSharedPref(Context context){
        super();
        this.monitorPref = this.context.getSharedPreferences(
                this.context.getString(R.string.preference_file_monitor), Context.MODE_PRIVATE);
        this.monitorEditor = monitorPref.edit();
    }

    @Override
    public PetimoSharedPref getInstance() throws Exception {
        if (_instance == null)
            if (context == null)
                throw new Exception("PetimoSharedPref must be initialized first!");
            else
                _instance = new MonitorSharedPref(context);
        return _instance;
    }


    //<---------------------------------------------------------------------------------------------
    // Write
    // -------------------------------------------------------------------------------------------->

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
        ArrayList<String[]> monitoredTasks = this.getMonitored(Sort.FREQUENCY);
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


    //<---------------------------------------------------------------------------------------------
    // Read
    // -------------------------------------------------------------------------------------------->

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
    public ArrayList<String[]> getMonitored(Sort sortOpt){

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


    @Override
    public void updateV1toV2() {

        // Update saved last monitored task
        try{
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
        }

        // Update saved monitored tasks
        try{
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
        }

    }


}

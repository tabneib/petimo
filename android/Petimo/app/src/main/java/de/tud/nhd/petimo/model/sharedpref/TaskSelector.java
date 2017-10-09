package de.tud.nhd.petimo.model.sharedpref;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.utils.PetimoStringUtils;
import de.tud.nhd.petimo.utils.StringParsingException;

/**
 * Task selector is implemented in form of transaction
 */
public class TaskSelector extends PetimoSettingsSPref {

    private static final String TAG = "TaskSelector";
    static TaskSelector _instance = null;

    private Mode activeMode = null;

    public static final String SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS =
            "de.tud.nhd.petimo.model.sharedpref.SharedPref.SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS";

    /**
     * Multiple modules of the app let the user select tasks. The selected tasks should be stored
     * in this temporal shared pref first. Afterwards they will be copied to the corresponding
     * permanent shared pref. This is realized in form of SelectedTaskTransaction
     */
    private ArrayList<Integer> tmpSelectedTask;

    TaskSelector(Context context) {
        super();
    }

    public static TaskSelector getInstance() throws RuntimeException{
        if (_instance == null)
            if (context == null)
                throw new RuntimeException("PetimoSPref must be initialized first!");
            else
                _instance = new TaskSelector(context);
        return _instance;
    }


    /**
     * Return a list of all selected cat/task to display on EditBlockFragment
     * Database version: V.2
     * @return
     */
    public ArrayList<Integer> getSelectedTasks(Mode mode) throws IllegalStateException {

        // If there is ongoing transaction, so we only work on the copy of selected task list
        if (activeMode != null){
            if (activeMode != mode)
                throw new IllegalStateException("Wrong mode!");
            else
                return tmpSelectedTask;
        }

        // If there is no on-going transaction, then return the original list
        try{
            ArrayList<String[]> selectedTasksStr;
            switch (mode) {
                case MONITOR_HISTORY:
                    selectedTasksStr = PetimoStringUtils.parse(settingPref.getString(
                            SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS, null), 1);
                    break;
                case STATISTICS:
                    // TODO change this
                    selectedTasksStr = PetimoStringUtils.parse(settingPref.getString(
                            SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS, null), 1);
                    break;
                default:
                    throw new IllegalStateException("Unknown mode!");
            }

            // Convert to int list
            ArrayList<Integer> selectedTasks = new ArrayList<>();
            for (String[] item : selectedTasksStr)
                selectedTasks.add(Integer.parseInt(item[0]));

            // Remove tasks that don't exist anymore
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
        catch (StringParsingException e) {
            return new ArrayList<>();
        }
    }


    /**
     *
     * @param mode
     * @throws IllegalStateException
     */
    public void startTransaction(Mode mode) throws IllegalStateException {
        if (activeMode == null) {
            // Make a copy of the corresponding Shared Pref
            tmpSelectedTask = this.getSelectedTasks(mode);
            this.activeMode = mode;
        }
        else
            throw new IllegalStateException(
                    "Cannot start a new transaction. There is an ongoing transaction!");
    }

    /**
     *
     * @throws IllegalStateException
     */
    public void commit() throws IllegalStateException {
        if (activeMode != null){
            // Store the tmp data to sharedPref permanently
            switch (activeMode){
                case MONITOR_HISTORY:
                    settingsEditor.putString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS,
                            PetimoStringUtils.encodeIntList(tmpSelectedTask, 1));
                    settingsEditor.apply();
                    break;
                case STATISTICS:
                    // TODO
                    break;
                default:
                    throw new IllegalStateException(
                            "Cannot start a new transaction. Unknown mode!");
            }
            this.activeMode = null;

        }
        else
            throw new IllegalStateException(
                    "Cannot commit. There is no ongoing transaction!");
    }

    /**
     *
     * @throws IllegalStateException
     */
    public void abort() throws IllegalStateException {
        if (activeMode != null){
            // Just forget the transaction
            tmpSelectedTask = null;
            activeMode = null;
        }
        else
            throw new IllegalStateException(
                    "Cannot abort. There is no ongoing transaction!");
    }

    /**
     *
     * @return
     */
    public boolean isTransactionOpen(){
        return activeMode != null;
    }


    // <--------------------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------------------->

    /**
     * Database version: V.2
     * @param taskId
     */
    public void add(int taskId) throws IllegalStateException{
        if (activeMode != null){
            //ArrayList<Integer> selectedTasks = this.getSelectedTasks();
            if (tmpSelectedTask != null){
                // If the task is already there then do nothing
                if(tmpSelectedTask.contains(taskId))
                    return;
                // Add a new item to the selected task list
                tmpSelectedTask.add(taskId);
                return;
            }
            else{
                // initialize
                tmpSelectedTask = new ArrayList<>();
                tmpSelectedTask.add(taskId);
                return;
            }
        }
        else
            throw new IllegalStateException(
                    "Cannot add selected task. There is no ongoing transaction!");

    }

    /**
     * Database version: V.2
     * @param taskId
     */
    public void remove(int taskId){
        if (tmpSelectedTask != null){
            Iterator<Integer> iterator = tmpSelectedTask.iterator();
            while (iterator.hasNext())
                if (iterator.next() == taskId)
                    iterator.remove();
        }
    }


    @Override
    public void updateV1toV2() {
        // Update saved selected tasks
        try{
            ArrayList<String[]> oldSelectedTasks = PetimoStringUtils.parse(
                    settingPref.getString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS, null), 2);

            ArrayList<Integer> newSelectedTasks = new ArrayList<>();
            for (String[] catTask: oldSelectedTasks)
                newSelectedTasks.add(PetimoDbWrapper.getInstance().getTaskIdFromName(
                        catTask[1], PetimoDbWrapper.getInstance().getCatIdFromName(catTask[0])));
            settingsEditor.putString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS,
                    PetimoStringUtils.encodeIntList(newSelectedTasks, 1));
            settingsEditor.apply();

            Log.d(TAG, "updateV1toV2: saved selected tasks is updated:");
            for (String[] item: PetimoStringUtils.parse(
                    settingPref.getString(SETTINGS_MONITORED_BLOCKS_SELECTED_TASKS, null), 2))
                Log.d(TAG, Arrays.toString(item));

        }
        catch (StringParsingException e){
            e.printStackTrace();
            Log.d(TAG, "updateV1toV2: lastMonitoredTask is probably already up-to-date");
        }
    }

    public enum Mode {
        MONITOR_HISTORY("MONITOR_HISTORY"),
        STATISTICS("STATISTICS");

        private String description;
        Mode(String description){
            this.description = description;
        }

        @Override
        public String toString(){
            return this.description;
        }
    }

}

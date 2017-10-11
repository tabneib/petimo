package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.db.MonitorTask;
import de.tud.nhd.petimo.model.sharedpref.PetimoSPref;
import de.tud.nhd.petimo.model.sharedpref.TaskSelector;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MonitorTask}
 */
public class TaskRecyclerViewAdapter extends
        RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = "TaskAdapter";
    private String mode;
    private String selectorMode;
    // Select mode attributes
    private CategoryRecyclerViewAdapter catAdapter;
    private int catPosition;
    public List<MonitorTask> taskList;
    private boolean onBind = false;

    public TaskRecyclerViewAdapter(List<MonitorTask> items, String mode, String selectorMode,
                                   CategoryRecyclerViewAdapter catAdapter, int catPosition) {
        this.taskList = items;
        this.catAdapter = catAdapter;
        this.catPosition = catPosition;
        this.mode = mode;
        this.selectorMode = selectorMode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (mode){
            case CategoryListFragment.EDIT_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_task_edit, parent, false);
                break;
            case CategoryListFragment.SELECT_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_task_select, parent, false);

                break;
            default:
                throw new RuntimeException("Display mode is not set.");
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        switch (mode){
            case CategoryListFragment.EDIT_MODE:
                holder.task = taskList.get(position);
                holder.taskNameTextView.setText(taskList.get(position).getName());
                break;
            case CategoryListFragment.SELECT_MODE:
                holder.task = taskList.get(position);
                holder.taskCheckBox.setText(taskList.get(position).getName());

                boolean notFound = true;

                // Check if the task is already selected
                if (TaskSelector.getInstance().
                        getSelectedTasks(selectorMode).
                        contains(holder.task.getId())){
                    onBind = true;
                    holder.taskCheckBox.setChecked(true);
                    onBind = false;
                    notFound = false;
                    break;
                }

                if (notFound) {
                    onBind = true;
                    holder.taskCheckBox.setChecked(false);
                    onBind = false;
                }
                break;
            default:
                throw new RuntimeException("Display mode is not set.");
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * BlockListViewHolder that holds a task item to display
     * TODO adapt according to display mode!
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        // Edit mode
        public TextView taskNameTextView;

        // Select mode
        public CheckBox taskCheckBox;

        // Common
        public MonitorTask task;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            // Edit Mode
            taskNameTextView = (TextView) view.findViewById(R.id.textViewTaskName);
            // Select Mode
            taskCheckBox = (CheckBox) view.findViewById(R.id.checkboxTask);

            switch (mode){
                case CategoryListFragment.EDIT_MODE:
                    break;
                case CategoryListFragment.SELECT_MODE:

                    taskCheckBox.setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(
                                        CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked)
                                        TaskSelector.getInstance().add(task.getId());
                                    else
                                        TaskSelector.getInstance().remove(task.getId());
                                    // Rebind the corresponding category
                                    //catAdapter.onBindViewHolder(catViewHolder, catPosition);
                                    // Notify the corresponding cat viewHolder to update its check box
                                    if (!onBind)
                                        catAdapter.notifyItemChanged(catPosition);
                                }
                            });
                    break;
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + taskNameTextView.getText() + "'";
        }
    }
}

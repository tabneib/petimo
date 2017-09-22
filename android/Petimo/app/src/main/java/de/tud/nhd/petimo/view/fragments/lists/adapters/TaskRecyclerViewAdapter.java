package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.MonitorTask;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link de.tud.nhd.petimo.model.MonitorTask}
 */
public class TaskRecyclerViewAdapter extends
        RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = "TaskAdapter";
    private String mode;
    public List<MonitorTask> taskList;

    public TaskRecyclerViewAdapter(List<MonitorTask> items, String mode) {
        taskList = items;
        this.mode = mode;
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
        public final TextView taskNameTextView;
        public MonitorTask task;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            taskNameTextView = (TextView) view.findViewById(R.id.textViewTaskName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + taskNameTextView.getText() + "'";
        }
    }
}

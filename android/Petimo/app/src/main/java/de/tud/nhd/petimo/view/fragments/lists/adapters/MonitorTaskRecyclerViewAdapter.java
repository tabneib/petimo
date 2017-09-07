package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.MonitorTask;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link de.tud.nhd.petimo.model.MonitorTask}
 */
public class MonitorTaskRecyclerViewAdapter extends
        RecyclerView.Adapter<MonitorTaskRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "TaskAdapter";
    private final List<MonitorTask> taskList;

    public MonitorTaskRecyclerViewAdapter(List<MonitorTask> items) {
        taskList = items;
        Log.d(TAG, "tagList size ====> " + taskList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_monitortask, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "I am gonna binding position =====> " + position);
        holder.task = taskList.get(position);
        holder.taskNameTextView.setText(taskList.get(position).getName());


        Log.d(TAG, "Done binding, position =====> " + position);
        // TODO setup listener to support deleting task by swiping
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * ViewHolder that holds a task item to display
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

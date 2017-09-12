package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a String[] and makes a call to the
 * specified {@link OnModeFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MonitoredTaskRecyclerViewAdapter extends RecyclerView.Adapter<MonitoredTaskRecyclerViewAdapter.ViewHolder> {

    public List<String[]> monitoredTaskList;
    private final OnModeFragmentInteractionListener mListener;

    public MonitoredTaskRecyclerViewAdapter(
            List<String[]> items, OnModeFragmentInteractionListener listener) {
        monitoredTaskList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_monitoredtask, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = monitoredTaskList.get(position);
        holder.textViewCatTask.setText(
                monitoredTaskList.get(position)[0] + " / " + monitoredTaskList.get(position)[1]);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            taskSelected(position);
            }
        });
    }

    /**
     *
     * @param position
     */
    private void taskSelected(int position){
        if (null != mListener) {
            mListener.onLastMonitoredTaskSelected(monitoredTaskList.get(position)[0],
                    monitoredTaskList.get(position)[1]);
        }
    }

    @Override
    public int getItemCount() {
        return monitoredTaskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView textViewCatTask;
        public String[] mItem;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textViewCatTask = (TextView) view.findViewById(R.id.textViewCatTask);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textViewCatTask.getText() + "'";
        }
    }
}

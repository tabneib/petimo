package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.utils.ColorUtils;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a String[] and makes a call to the
 * specified {@link OnModeFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MonitoredTaskRecyclerViewAdapter
        extends RecyclerView.Adapter<MonitoredTaskRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = "MonitoredTaskAdapter";

    public List<String[]> monitoredTaskList;
    private final OnModeFragmentInteractionListener mListener;
    // Default frequency step is 5
    private int freqStep = 5;
    private Context context;

    private int[] bgColors = {
            R.color.monitoredTask_bg_50,
            R.color.monitoredTask_bg_100,
            R.color.monitoredTask_bg_200,
            R.color.monitoredTask_bg_300,
            R.color.monitoredTask_bg_400,
            R.color.monitoredTask_bg_500,
            R.color.monitoredTask_bg_600,
            R.color.monitoredTask_bg_700,
            R.color.monitoredTask_bg_800,
            R.color.monitoredTask_bg_900,
            R.color.monitoredTask_bg_1000,
    };


    public MonitoredTaskRecyclerViewAdapter(Context context,
            List<String[]> items, OnModeFragmentInteractionListener listener) {
        monitoredTaskList = items;
        mListener = listener;
        this.context = context;
        freqStep = context.getResources().getInteger(R.integer.monitored_task_freq_step);
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

        int freqLevel = Integer.parseInt(holder.mItem[3]) / freqStep;
        freqLevel = freqLevel >= bgColors.length ? bgColors.length : freqLevel;
        holder.itemContainer.setBackgroundColor(
                ContextCompat.getColor(context, bgColors[freqLevel - 1]));

        if (ColorUtils.isDarkColor(ContextCompat.getColor(context, bgColors[freqLevel - 1])))
            holder.textViewCatTask.setTextColor(
                    ContextCompat.getColor(context, R.color.textColorPrimary));


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
        // Category | Task | Time | Frequency
        public String[] mItem;
        public FrameLayout itemContainer;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            textViewCatTask = (TextView) view.findViewById(R.id.textViewCatTask);
            itemContainer = (FrameLayout) view.findViewById(R.id.item_container);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textViewCatTask.getText() + "'";
        }
    }
}

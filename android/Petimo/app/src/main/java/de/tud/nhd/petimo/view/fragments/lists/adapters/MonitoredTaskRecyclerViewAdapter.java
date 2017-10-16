package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.utils.PetimoColorUtils;
import de.tud.nhd.petimo.view.fragments.TaskSelectorBottomSheet;

import java.util.List;


public class MonitoredTaskRecyclerViewAdapter
        extends RecyclerView.Adapter<MonitoredTaskRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = "MonitoredTaskAdapter";

    public List<String[]> monitoredTaskList;
    private final TaskSelectorBottomSheet.Listener mListener;
    // Default frequency step is 5
    private int freqStep = 5;
    private Context context;

    /*
    private int[] bgColors = {
            R.color.orange50,
            R.color.orange100,
            R.color.orange200,
            R.color.orange300,
            R.color.orange400,
            R.color.orange500,
            R.color.orange600,
            R.color.orange700,
            R.color.orange800,
            R.color.orange900,
            R.color.orange1000,
    };
    */

    private int[] bgColors = {
            R.color.grey700,
            R.color.grey600,
            R.color.grey500,
            R.color.grey400,
            R.color.grey300,
            R.color.grey200,
            R.color.grey100,
            R.color.grey50,
    };

    public MonitoredTaskRecyclerViewAdapter(Context context,
            List<String[]> items, TaskSelectorBottomSheet.Listener listener) {
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
                        PetimoDbWrapper.getInstance().getTaskNameById(
                        Integer.parseInt(monitoredTaskList.get(position)[1])));

        int freqLevel = Integer.parseInt(holder.mItem[3]) / freqStep;
        // Notice : If freqStep is 1 then freqLevel will never be 0 :)
        freqLevel = freqLevel >= bgColors.length ? bgColors.length - 1 : freqLevel;
        holder.itemContainer.setBackgroundColor(
                ContextCompat.getColor(context, bgColors[freqLevel]));

        if (PetimoColorUtils.isDarkColor(ContextCompat.getColor(context, bgColors[freqLevel])))
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
            mListener.onTaskSelected(
                    Integer.parseInt(monitoredTaskList.get(position)[0]),
                    Integer.parseInt(monitoredTaskList.get(position)[1]));
        }
    }

    @Override
    public int getItemCount() {
        return monitoredTaskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView textViewCatTask;
        // CatId | TaskId | Time | Frequency
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

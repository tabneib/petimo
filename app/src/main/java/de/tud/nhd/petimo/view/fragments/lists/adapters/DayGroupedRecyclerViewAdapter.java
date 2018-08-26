package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.db.MonitorBlock;
import de.tud.nhd.petimo.model.db.MonitorDayGrouped;
import de.tud.nhd.petimo.utils.PetimoColorUtils;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MonitorBlock}
 */
public class DayGroupedRecyclerViewAdapter extends
        RecyclerView.Adapter<DayGroupedRecyclerViewAdapter.DayGroupedViewHolder> {

    private static final String TAG = "BlockAdapter";
    public List<MonitorDayGrouped> itemList;
    private Context context;

    // Default duration step is 30 min
    private int durationStep = 30;

    private int[] bgColors = {
            R.color.background_primary,
            R.color.orange50,
            R.color.orange75,
            R.color.orange100,
            R.color.orange150,
            R.color.orange200,
            R.color.orange250,
            R.color.orange300,
            R.color.orange350,
            R.color.orange400,
    };

    /**
     * Construct the dayAdapter.
     * @param itemList
     */
    public DayGroupedRecyclerViewAdapter(Context context, List<MonitorDayGrouped> itemList) {
        this.itemList = itemList;
        this.context = context;
        durationStep = context.getResources().getInteger(R.integer.monitor_block_duration_step);

    }

    @Override
    public DayGroupedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_day_grouped, parent, false);
        return new DayGroupedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DayGroupedViewHolder holder, int position) {

        holder.monitorDayGrouped = itemList.get(position);

        String catTask =  itemList.get(position).getDescriptiveName();
        holder.textViewCatTask.setText(catTask);

        holder.textViewDuration.setText(
                PetimoTimeUtils.getTimeFromMs(itemList.get(position).getDuration()));
        // TODO: display percentage beautifully !
        //holder.progressBar.setProgress();
        holder.textViewPercentage.setText(itemList.get(position).getPercentage() + "%");
        int durationLevel = (int)
                holder.monitorDayGrouped.getDuration() / (durationStep * 60000);
        // This is a hard-coded fix for any unwanted bug that makes durationLevel a negative int
        durationLevel = Math.abs(durationLevel);

        durationLevel = durationLevel >= bgColors.length ? bgColors.length - 1 : durationLevel;
        holder.itemContainer.setBackgroundColor(
                ContextCompat.getColor(context, bgColors[durationLevel]));

        if (PetimoColorUtils.isDarkColor(ContextCompat.getColor(context, bgColors[durationLevel]))) {
            holder.textViewDuration.setTextColor(
                    ContextCompat.getColor(context, R.color.textColorPrimary));
            holder.textViewCatTask.setTextColor(
                    ContextCompat.getColor(context, R.color.textColorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class DayGroupedViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView textViewCatTask;
        public TextView textViewPercentage;
        public TextView textViewDuration;
        public MonitorDayGrouped monitorDayGrouped;
        public FrameLayout itemContainer;


        public DayGroupedViewHolder(View view) {
            super(view);
            mView = view;
            textViewCatTask = (TextView) view.findViewById(R.id.textView_catTask);
            textViewPercentage = (TextView) view.findViewById(R.id.textView_percentage);
            textViewDuration = (TextView) view.findViewById(R.id.textView_duration);
            itemContainer = (FrameLayout) view.findViewById(R.id.item_container);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textViewDuration.getText() +
                    " ==> " + textViewCatTask.getText() + "'";
        }
    }
}

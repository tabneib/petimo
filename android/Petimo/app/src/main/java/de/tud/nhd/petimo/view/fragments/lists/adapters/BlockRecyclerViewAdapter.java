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
import de.tud.nhd.petimo.utils.PetimoColorUtils;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.model.MonitorBlock;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MonitorBlock}
 */
public class BlockRecyclerViewAdapter extends
        RecyclerView.Adapter<BlockRecyclerViewAdapter.BlockListViewHolder> {

    private static final String TAG = "BlockAdapter";
    public List<MonitorBlock> blockList;
    private Context context;

    // Default duration step is 30 min
    private int durationStep = 30;

    private int[] bgColors = {
            R.color.background_primary,
            R.color.monitoredTask_bg_50,
            R.color.monitoredTask_bg_75,
            R.color.monitoredTask_bg_100,
            R.color.monitoredTask_bg_150,
            R.color.monitoredTask_bg_200,
            R.color.monitoredTask_bg_250,
            R.color.monitoredTask_bg_300,
            R.color.monitoredTask_bg_350,
            R.color.monitoredTask_bg_400,
    };

    /**
     * Construct the adapter. Is the given block list is null then the adapter will query the
     * database by every view holder binding.
     * @param blockList
     */
    public BlockRecyclerViewAdapter(Context context, List<MonitorBlock> blockList) {
        this.blockList = blockList;
        this.context = context;
        durationStep = context.getResources().getInteger(R.integer.monitor_block_duration_step);

    }

    @Override
    public BlockListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_block, parent, false);
        return new BlockListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BlockListViewHolder holder, int position) {
        holder.monitorBlock = blockList.get(position);
        String timeInfo = "► " +
               PetimoTimeUtils.getDayTimeFromMsTime(blockList.get(position).getStart()) +
                " -> " + PetimoTimeUtils.getDayTimeFromMsTime(blockList.get(position).getEnd()) +
                " : " + PetimoTimeUtils.getTimeFromMs(blockList.get(position).getDuration());
        //Log.d(TAG, timeInfo);

        holder.textViewTime.setText(timeInfo);

        String monitorInfo = "    " + blockList.get(position).getCatName() + " / " +
                blockList.get(position).getTaskName();
        holder.textViewData.setText(monitorInfo);


        int durationLevel = (int)
                holder.monitorBlock.getDuration() / (durationStep * 60000);
        // This is a hard-coded fix for any unwanted bug that makes durationLevel a negative int
        durationLevel = Math.abs(durationLevel);

        durationLevel = durationLevel >= bgColors.length ? bgColors.length - 1 : durationLevel;
        holder.itemContainer.setBackgroundColor(
                ContextCompat.getColor(context, bgColors[durationLevel]));

        if (PetimoColorUtils.isDarkColor(ContextCompat.getColor(context, bgColors[durationLevel]))) {
            holder.textViewTime.setTextColor(
                    ContextCompat.getColor(context, R.color.textColorPrimary));
            holder.textViewData.setTextColor(
                    ContextCompat.getColor(context, R.color.textColorPrimary));
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });



    }

    @Override
    public int getItemCount() {
        return blockList.size();
    }


    public class BlockListViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView textViewTime;
        public TextView textViewData;
        public MonitorBlock monitorBlock;
        public FrameLayout itemContainer;


        public BlockListViewHolder(View view) {
            super(view);
            mView = view;
            textViewTime = (TextView) view.findViewById(R.id.fragment_monitorblock_time);
            textViewData = (TextView) view.findViewById(R.id.fragment_monitorblock_monitor_data);
            itemContainer = (FrameLayout) view.findViewById(R.id.item_container);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textViewTime.getText() +
                    " ==> " + textViewData.getText() + "'";
        }
    }
}

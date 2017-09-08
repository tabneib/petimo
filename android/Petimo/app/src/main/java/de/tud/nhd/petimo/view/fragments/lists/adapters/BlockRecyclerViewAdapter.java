package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.TimeUtils;
import de.tud.nhd.petimo.model.MonitorBlock;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MonitorBlock}
 */
public class BlockRecyclerViewAdapter extends
        RecyclerView.Adapter<BlockRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "BlockAdapter";
    public List<MonitorBlock> blockList;

    /**
     * Construct the adapter. Is the given block list is null then the adapter will query the
     * database by every view holder binding.
     * @param blockList
     */
    public BlockRecyclerViewAdapter(List<MonitorBlock> blockList) {
        Log.d(TAG, "New Block Adapter ! blockList size =====> " + blockList.size());
        this.blockList = blockList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_block, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "Binding ViewHolder at position ====> " + position );
        holder.mItem = blockList.get(position);
        String timeInfo = "► " +
               TimeUtils.getDayTimeFromMsTime(blockList.get(position).getStart()) +
                " -> " + TimeUtils.getDayTimeFromMsTime(blockList.get(position).getEnd()) +
                " : " + TimeUtils.getTimeFromMs(blockList.get(position).getDuration());
        Log.d(TAG, timeInfo);

        holder.mTimeView.setText(timeInfo);

        String monitorInfo = "    " + blockList.get(position).getCategory() + " / " +
                blockList.get(position).getTask();
        Log.d(TAG, monitorInfo);
        holder.mDataView.setText(monitorInfo);

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


    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mTimeView;
        public TextView mDataView;
        public MonitorBlock mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTimeView = (TextView) view.findViewById(R.id.fragment_monitorblock_time);
            mDataView = (TextView) view.findViewById(R.id.fragment_monitorblock_monitor_data);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTimeView.getText() +
                    " ==> " + mDataView.getText() + "'";
        }
    }
}

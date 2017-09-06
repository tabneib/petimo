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
import de.tud.nhd.petimo.view.fragments.lists.MonitorBlockListFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MonitorBlock}
 */
public class MonitorBlockRecyclerViewAdapter extends RecyclerView.Adapter<MonitorBlockRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private final List<MonitorBlock> inputBlockList;

    /**
     * Construct the adapter. Is the given block list is null then the adapter will query the
     * database by every view holder binding.
     * @param inputBlockList
     */
    public MonitorBlockRecyclerViewAdapter(List<MonitorBlock> inputBlockList) {
        this.inputBlockList = inputBlockList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_monitorblock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "Binding ViewHolder at position ====> " + position );
        holder.mItem = inputBlockList.get(position);
        String timeInfo = "[+] " +
                TimeUtils.getDateStrFromInt(inputBlockList.get(position).getDate()) +
                " " + TimeUtils.getDayTimeFromMsTime(inputBlockList.get(position).getStart()) +
                " -> " + TimeUtils.getDayTimeFromMsTime(inputBlockList.get(position).getEnd()) +
                " : " + TimeUtils.getTimeFromMs(inputBlockList.get(position).getDuration());
        Log.d(TAG, timeInfo);

        holder.mTimeView.setText(timeInfo);

        String monitorInfo = "    " + inputBlockList.get(position).getCategory() + " / " +
                inputBlockList.get(position).getTask();
        Log.d(TAG, monitorInfo);
        holder.mDataView.setText(monitorInfo);

        // work around the "final" issue
        final MonitorBlock thisItem = inputBlockList.get(position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });
    }


    @Override
    public int getItemCount() {
        return inputBlockList.size();
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

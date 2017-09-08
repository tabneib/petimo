package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.controller.TimeUtils;
import de.tud.nhd.petimo.model.MonitorDay;
import de.tud.nhd.petimo.model.PetimoContract;
import de.tud.nhd.petimo.view.fragments.listener.OnEditDayFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MonitorDay} and makes a call to the
 * specified {@link OnEditDayFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DayRecyclerViewAdapter extends
        RecyclerView.Adapter<DayRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "DayAdapter";
    private final List<MonitorDay> dayList;
    private final OnEditDayFragmentInteractionListener mListener;
    private Fragment fragment;

    public DayRecyclerViewAdapter(Fragment fragment, List<MonitorDay> items,
                                  OnEditDayFragmentInteractionListener listener) {
        this.fragment = fragment;
        dayList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "Gonna bind position ====> " + position);
        Log.d(TAG, "Holder.textViewInfo ID ====> " + holder.textViewInfo.getId());
        holder.monitorDay = dayList.get(position);
        holder.textViewDate.setText(TimeUtils.getDateStrFromInt(dayList.get(position).getDate()));
        holder.textViewInfo.setText(dayList.get(position).getInfo());

        // The adapter for the recyclerView that displays the given list of monitor blocks
        Log.d(TAG, "Day/Input BlockList size =====> " + dayList.get(position).getDate() + "/" +
                dayList.get(position).getMonitorBlocks().size());
        holder.blockAdapter =
                new BlockRecyclerViewAdapter(dayList.get(position).getMonitorBlocks());
        // Set adapter for the recyclerview displaying block list
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                // Do nothing
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Remove the block from the database
                PetimoController.getInstance().removeBlock(holder.monitorDay.getMonitorBlocks().
                                get(viewHolder.getLayoutPosition()).getId());
                holder.blockAdapter.blockList.remove(viewHolder.getLayoutPosition());
                holder.blockAdapter.notifyItemRemoved(viewHolder.getLayoutPosition());
                //Update the displayed info of the corresponding day
                holder.textViewInfo.setText(holder.monitorDay.getInfo());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(holder.recyclerView);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
        holder.recyclerView.setAdapter(holder.blockAdapter);
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView textViewDate;
        public TextView textViewInfo;
        public Button buttonAddBlock;
        public RecyclerView recyclerView;

        public MonitorDay monitorDay;
        public BlockRecyclerViewAdapter blockAdapter;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            textViewDate = (TextView) view.findViewById(R.id.textView_date);
            textViewInfo = (TextView) view.findViewById(R.id.textView_info);
            buttonAddBlock = (Button) view.findViewById(R.id.button_add_block);
            recyclerView = (RecyclerView) view.findViewById(R.id.block_list_recyclerview);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + textViewDate.getText() + "'";
        }
    }
}

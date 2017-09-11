package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.utils.TimeUtils;
import de.tud.nhd.petimo.model.MonitorDay;
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
    public List<MonitorDay> dayList;
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
        holder.monitorDay = dayList.get(position);
        holder.textViewDate.setText(TimeUtils.getDateStrFromInt(dayList.get(position).getDate()));
        holder.textViewInfo.setText(dayList.get(position).getInfo());

        // The adapter for the recyclerView that displays the given list of monitor blocks
        holder.blockAdapter =
                new BlockRecyclerViewAdapter(dayList.get(position).getMonitorBlocks());
        // Set adapter for the recyclerView displaying block list
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
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(fragment.getActivity(),
                            android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(fragment.getActivity());
                }
                final BlockRecyclerViewAdapter.BlockListViewHolder vHolder =
                        (BlockRecyclerViewAdapter.BlockListViewHolder) viewHolder;
                builder.setTitle(
                        fragment.getActivity().getString(R.string.title_remove_block))
                        .setMessage(fragment.getActivity().
                                getString(R.string.message_confirm_remove) + "\n"
                                + vHolder.textViewData.getText() + "?")
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        // Remove the block from the database
                                        PetimoController.getInstance().
                                                removeBlock(holder.monitorDay.getMonitorBlocks().
                                                get(vHolder.getLayoutPosition()).getId());
                                        holder.blockAdapter.blockList.remove(
                                                vHolder.getLayoutPosition());
                                        holder.blockAdapter.notifyItemRemoved(
                                                vHolder.getLayoutPosition());
                                        //Update the displayed info of the corresponding day
                                        holder.textViewInfo.setText(holder.monitorDay.getInfo());
                                    }
                                })
                        .setNegativeButton(
                                android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        holder.blockAdapter.notifyDataSetChanged();
                                        // do nothing
                                    }
                                })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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

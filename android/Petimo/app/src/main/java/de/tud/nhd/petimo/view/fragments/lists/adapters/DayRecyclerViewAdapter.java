package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.controller.exception.InvalidInputTimeException;
import de.tud.nhd.petimo.controller.exception.InvalidTimeException;
import de.tud.nhd.petimo.model.db.MonitorDayGrouped;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.model.sharedpref.PetimoSPref;
import de.tud.nhd.petimo.model.sharedpref.PetimoSettingsSPref;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.model.db.MonitorDay;
import de.tud.nhd.petimo.view.fragments.dialogs.AddBlockDialogFragment;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.lists.DayListFragment;
import de.tud.nhd.petimo.view.fragments.lists.DayListFragment.OnEditDayFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;


public class DayRecyclerViewAdapter extends
        RecyclerView.Adapter<DayRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "DayAdapter";
    public List<MonitorDay> dayList;
    private final OnEditDayFragmentInteractionListener mListener;
    private DayListFragment fragment;

    public DayRecyclerViewAdapter(DayListFragment fragment, List<MonitorDay> items,
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
        holder.textViewDate.setText(
                PetimoTimeUtils.getDateStrFromInt(dayList.get(position).getDate()));
        holder.textViewSum.setText(dayList.get(position).getDurationStr());
        // Listener for Add Button
        holder.buttonAddBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AddBlockDialogFragment addBlockFragment =
                        AddBlockDialogFragment.newInstance(holder.monitorDay.getDate());

                PetimoDialog addBlockDialog = PetimoDialog.newInstance(fragment.getActivity())
                        .setTitle(fragment.getActivity().
                                getString(R.string.title_new_monitor_block))
                        .setContentFragment(addBlockFragment)
                        .setPositiveButton(
                                fragment.getActivity().getString(R.string.button_add_block),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // If the time is set, then add new block
                                        if (addBlockFragment.manualTime[0] != 0 &&
                                                addBlockFragment.manualTime[1] != 0){
                                            try {
                                                PetimoController.getInstance().addBlockManually(
                                                    addBlockFragment.catSpinner.
                                                            getSelectedItem().toString(),
                                                        addBlockFragment.taskSpinner.
                                                                getSelectedItem().toString(),
                                                        addBlockFragment.manualTime[0],
                                                        addBlockFragment.manualTime[1],
                                                        holder.monitorDay.getDate(), "");
                                                Toast.makeText(fragment.getActivity(),
                                                        fragment.getActivity().getString(
                                                                R.string.message_block_added),
                                                        Toast.LENGTH_LONG).show();
                                                // Refresh the list
                                                fragment.refreshList();
                                            } catch (DbErrorException e) {
                                                e.printStackTrace();
                                            } catch (InvalidInputTimeException e) {
                                                e.printStackTrace();
                                            } catch (InvalidTimeException e) {
                                                e.printStackTrace();
                                            } catch (InvalidCategoryException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                })
                        .setNegativeButton(fragment.getActivity().getString(R.string.button_cancel),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Do nothing
                                    }
                                });
                addBlockDialog.show(fragment.getActivity().getSupportFragmentManager(), null);
            }
        });

        switch (PetimoSettingsSPref.getInstance().getString(
                PetimoSettingsSPref.MONITORED_BLOCKS_GROUP_BY, PetimoSPref.Consts.NOT_GROUP)){

            case (PetimoSPref.Consts.NOT_GROUP):

                // The dayAdapter for the recyclerView that displays the given list of monitor blocks
                holder.blockAdapter = new BlockRecyclerViewAdapter(
                        fragment.getActivity(), dayList.get(position).getMonitorBlocks());

                break;

            default:
                ArrayList<MonitorDayGrouped> groups;
                if (PetimoSettingsSPref.getInstance().getString(
                        PetimoSettingsSPref.MONITORED_BLOCKS_GROUP_BY,
                        PetimoSPref.Consts.GROUP_BY_TASK).equals(PetimoSPref.Consts.GROUP_BY_CAT))
                    groups = dayList.get(position).getGroupedByCat();
                else
                    groups = dayList.get(position).getGroupedByTask();

                holder.groupAdapter = new DayGroupedRecyclerViewAdapter(
                        fragment.getActivity(), groups);

                break;
        }
        // Set dayAdapter for the recyclerView displaying block list

        // Swipe to Delete is enable?
        if (PetimoSettingsSPref.getInstance().
                getBoolean(PetimoSettingsSPref.MONITORED_BLOCKS_LOCK, false))
            holder.itemTouchHelper.attachToRecyclerView(holder.recyclerView);
        else
            holder.itemTouchHelper.attachToRecyclerView(null);


        holder.recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
        if (holder.blockAdapter != null)
            holder.recyclerView.setAdapter(holder.blockAdapter);
        else if (holder.groupAdapter != null)
            holder.recyclerView.setAdapter(holder.groupAdapter);
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView textViewDate;
        public TextView textViewSum;
        public FrameLayout buttonAddBlock;
        public RecyclerView recyclerView;

        public MonitorDay monitorDay;
        public BlockRecyclerViewAdapter blockAdapter;
        public DayGroupedRecyclerViewAdapter groupAdapter;
        public ItemTouchHelper itemTouchHelper;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            textViewDate = (TextView) view.findViewById(R.id.textView_date);
            textViewSum = (TextView) view.findViewById(R.id.textView_sum);
            buttonAddBlock = (FrameLayout) view.findViewById(R.id.button_add_block);
            recyclerView = (RecyclerView) view.findViewById(R.id.block_list_recyclerview);

            ItemTouchHelper.SimpleCallback simpleCallback =
                    new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){

                        @Override
                        public boolean onMove(RecyclerView recyclerView,
                                              RecyclerView.ViewHolder viewHolder,
                                              RecyclerView.ViewHolder target) {
                            // Do nothing
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                            final BlockRecyclerViewAdapter.BlockListViewHolder vHolder =
                                    (BlockRecyclerViewAdapter.BlockListViewHolder) viewHolder;
                            PetimoDialog removeBlockDialog = new PetimoDialog()
                                    .setTitle(fragment.getActivity().getString(
                                            R.string.title_remove_block))
                                    .setMessage(fragment.getActivity().
                                            getString(R.string.message_confirm_remove) + "\n"
                                            + vHolder.textViewCatTask.getText() + "?")
                                    .setPositiveButton(fragment.getActivity().
                                            getString(R.string.button_yes),
                                            new PetimoDialog.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    // Remove the block from the database
                                                    PetimoDbWrapper.getInstance().
                                                            removeBlockById(monitorDay.
                                                                    getMonitorBlocks().get(vHolder.
                                                                    getLayoutPosition()).getId());
                                                    blockAdapter.blockList.remove(
                                                            vHolder.getLayoutPosition());
                                                    blockAdapter.notifyItemRemoved(
                                                            vHolder.getLayoutPosition());
                                                    //Update displayed info of the corresponding day
                                                    textViewSum.setText(monitorDay.getDurationStr());
                                                }
                                            })
                                    .setNegativeButton(fragment.getActivity().
                                            getString(R.string.button_cancel),
                                            new PetimoDialog.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    blockAdapter.notifyDataSetChanged();
                                                }
                                            });
                            removeBlockDialog.show(
                                    fragment.getActivity().getSupportFragmentManager(), null);
                        }
                    };
            itemTouchHelper = new ItemTouchHelper(simpleCallback);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textViewDate.getText() + "'";
        }
    }
}

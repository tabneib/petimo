package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.controller.exception.InvalidInputTimeException;
import de.tud.nhd.petimo.controller.exception.InvalidTimeException;
import de.tud.nhd.petimo.model.PetimoDbWrapper;
import de.tud.nhd.petimo.model.PetimoSharedPref;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.model.MonitorDay;
import de.tud.nhd.petimo.view.fragments.dialogs.AddBlockDialogFragment;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.lists.DayListFragment;
import de.tud.nhd.petimo.view.fragments.lists.DayListFragment.OnEditDayFragmentInteractionListener;

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
        holder.textViewInfo.setText(dayList.get(position).getInfo());

        // Listener for Add Button
        holder.buttonAddBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AddBlockDialogFragment addBlockFragment =
                        AddBlockDialogFragment.newInstance(holder.monitorDay.getDate());

                PetimoDialog addBlockDialog = PetimoDialog.newInstance(fragment.getActivity())
                        .setIcon(PetimoDialog.ICON_SAVE)
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


        // The adapter for the recyclerView that displays the given list of monitor blocks
        holder.blockAdapter = new BlockRecyclerViewAdapter(
                fragment.getActivity(), dayList.get(position).getMonitorBlocks());
        // Set adapter for the recyclerView displaying block list

        if (!PetimoSharedPref.getInstance().
                getSettingsBoolean(PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_LOCK, true))
            holder.itemTouchHelper.attachToRecyclerView(holder.recyclerView);
        else
            holder.itemTouchHelper.attachToRecyclerView(null);


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
        public ItemTouchHelper itemTouchHelper;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            textViewDate = (TextView) view.findViewById(R.id.textView_date);
            textViewInfo = (TextView) view.findViewById(R.id.textView_info);
            buttonAddBlock = (Button) view.findViewById(R.id.button_add_block);
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
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle(fragment.getActivity().getString(
                                            R.string.title_remove_block))
                                    .setMessage(fragment.getActivity().
                                            getString(R.string.message_confirm_remove) + "\n"
                                            + vHolder.textViewData.getText() + "?")
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
                                                    textViewInfo.setText(monitorDay.getInfo());
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

package de.tud.nhd.petimo.view.fragments.menu;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;

import java.util.ArrayList;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.PetimoDbWrapper;
import de.tud.nhd.petimo.model.PetimoSharedPref;
import de.tud.nhd.petimo.view.activities.MainActivity;
import de.tud.nhd.petimo.view.fragments.ModeOffFragment;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.MonitoredTaskListFragment;
import de.tud.nhd.petimo.view.fragments.lists.adapters.TaskRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnModeFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ModeOffMenuFragment extends Fragment {

    private static final String TAG = "ModeOffMenuFragment";

    private OnModeFragmentInteractionListener mListener;
    RadioButton radioButtonTime;
    RadioButton radioButtonFreq;
    ImageButton buttonClear;

    public ModeOffMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_off_mode_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioButtonFreq = (RadioButton) getView().findViewById(R.id.radioButtonFreq);
        radioButtonTime = (RadioButton) getView().findViewById(R.id.radioButtonTime);
        buttonClear = (ImageButton) getView().findViewById(R.id.buttonClear);

        // update the selection of radioButtons
        switch (PetimoSharedPref.getInstance().getUsrMonitoredSortOrder()){
            case PetimoSharedPref.FREQUENCY:
                radioButtonFreq.setChecked(true);
                break;
            default:
                radioButtonTime.setChecked(true);
        }
        radioButtonTime.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // save the chosen option
                if (isChecked) {
                    PetimoController.getInstance().
                            updateUsrMonitoredTasksSortOrder(PetimoSharedPref.TIME);
                    // Update the monitored Task List
                    updateMonitoredTaskList();
                }
            }
        });

        radioButtonFreq.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // save the chosen option
                if (isChecked) {
                    PetimoController.getInstance().
                            updateUsrMonitoredTasksSortOrder(PetimoSharedPref.FREQUENCY);
                    // Update the monitored Task List
                    updateMonitoredTaskList();
                }
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {


                PetimoDialog confirmClearDialog = PetimoDialog.newInstance(getActivity())
                        .setIcon(PetimoDialog.ICON_WARNING)
                        .setTitle(getActivity().getString(R.string.title_clear_monitored_task_list))
                        .setMessage(getActivity().
                                getString(R.string.message_confirm_clear_monitored_task_list))
                        .setPositiveButton(getActivity().getString(R.string.button_ok),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Delete the saved monitored task
                                        PetimoSharedPref.getInstance().clearMonitoredTasks();
                                        updateMonitoredTaskList();
                                    }
                                })
                        .setNegativeButton(getActivity().getString(R.string.button_cancel),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // do nothing
                                    }
                                });
                confirmClearDialog.show(getActivity().getSupportFragmentManager(), null);
            }
         });
    }


    /**
     * Update the monitored Task List upon a change of sort order
     */
    private void updateMonitoredTaskList(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ModeOffFragment modeOffFragment = (ModeOffFragment)
                fm.findFragmentByTag(MainActivity.MODE_OFF_FRAGMENT_TAG);
        if (modeOffFragment != null) {
            //modeOffFragment.updateTaskListFragment();
             MonitoredTaskListFragment taskListFragment =
                     (MonitoredTaskListFragment) modeOffFragment.getChildFragmentManager().
                             findFragmentByTag(ModeOffFragment.TASK_LIST_FRAGMENT_TAG);
            if (taskListFragment != null){
                taskListFragment.adapter.monitoredTaskList.clear();
                taskListFragment.adapter.monitoredTaskList.addAll(
                        PetimoController.getInstance().getMonitoredTasks());
                taskListFragment.adapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnModeFragmentInteractionListener) {
            mListener = (OnModeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDateRangeChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}

package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.PetimoSharedPref;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.listener.OnEditBlocksMenuFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.CatTaskListFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEditBlocksMenuFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EditBlocksMenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";

    private OnEditBlocksMenuFragmentInteractionListener mListener;
    private CheckBox checkBoxSelectedTasks;
    private CheckBox checkBoxEmptyDays;
    private CheckBox checkBoxRemember;
    private CheckBox checkBoxLock;
    private TextView textViewLock;

    public EditBlocksFragment parentFragment;

    // This tag is used to avoid updating checkbox & day list every time the user navigate to this
    // fragment (actually each time is gonna be a new instance of this fragment)
    private final String RESET_TO_DEFAULT_TAG = "RESET_TO_DEFAULT_TAG";

    public EditBlocksMenuFragment() {
        // Required empty public constructor
    }

    public static EditBlocksMenuFragment newInstance(EditBlocksFragment parentFragment){
        EditBlocksMenuFragment fragment = new EditBlocksMenuFragment();
        fragment.parentFragment = parentFragment;
        //fragment.container = container;
        //fragment.parentContainer = parentContainer;
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_blocks_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // I tried to make to menu drag down above the layout below, but failed
        // update the topMargin of the container of this fragment
        /*

        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateContainerTopMargin();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    vto.removeOnGlobalLayoutListener(this);
                else
                    vto.removeOnGlobalLayoutListener(this);
            }
        });
        */

        checkBoxSelectedTasks = (CheckBox) getView().findViewById(R.id.checkbox_selected_tasks);
        checkBoxEmptyDays = (CheckBox) getView().findViewById(R.id.checkbox_show_empty_days);
        checkBoxRemember = (CheckBox) getView().findViewById(R.id.checkbox_remember_options);
        checkBoxLock = (CheckBox) getView().findViewById(R.id.checkbox_lock);
        textViewLock = (TextView) getView().findViewById(R.id.textview_lock);

        // Update the checkboxes
        if (PetimoSharedPref.getInstance().getSettingsBoolean(
                PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_REMEMBER, false))
            checkBoxRemember.setChecked(true);

        // If Remember is not checked and not yet reset to default
        // => Set values of these option to default and update day List
        if (!checkBoxRemember.isChecked() &&
                !PetimoController.getInstance().getTag(RESET_TO_DEFAULT_TAG, false)) {
            PetimoController.getInstance().setTag(RESET_TO_DEFAULT_TAG, true);
            PetimoSharedPref.getInstance().setSettingsBoolean(
                    PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS, false);
            PetimoSharedPref.getInstance().setSettingsBoolean(
                    PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS, false);
            //update List
            parentFragment.updateDayList();
        }

        if (PetimoSharedPref.getInstance().getSettingsBoolean(
                PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS, false))
            checkBoxSelectedTasks.setChecked(true);
        if (PetimoSharedPref.getInstance().getSettingsBoolean(
                PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS, false))
            checkBoxEmptyDays.setChecked(true);


        if (PetimoSharedPref.getInstance().getSettingsBoolean(
                PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_LOCK, false)){
            checkBoxLock.setChecked(true);
            checkBoxLock.setButtonDrawable(R.drawable.ic_lock_outline_black_24dp);
        }
        else
            checkBoxLock.setButtonDrawable(R.drawable.ic_lock_open_black_24dp);

        // Set Listeners
        checkBoxSelectedTasks.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        PetimoSharedPref.getInstance().setSettingsBoolean(
                                PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS,
                                isChecked);

                        // Display task selector dialog if checked
                        if (isChecked){
                            CatTaskListFragment taskListFragment =
                                    CatTaskListFragment.newInstance();
                            PetimoDialog taskSelectorDialog =
                                    PetimoDialog.newInstance(parentFragment.getActivity(), true)
                                    .setIcon(PetimoDialog.ICON_SAVE)
                                    .setTitle(getString(R.string.title_select_tasks_to_display))
                                    .setContentFragment(taskListFragment)
                                    .setPositiveButton(getString(R.string.button_ok),
                                            new PetimoDialog.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    // Update Day List
                                                    parentFragment.updateDayList();
                                                }
                                            });
                            taskSelectorDialog.show(
                                    parentFragment.getActivity().getSupportFragmentManager(), null);
                        }
                        else
                            parentFragment.updateDayList();
                    }
                });

        checkBoxEmptyDays.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        PetimoSharedPref.getInstance().setSettingsBoolean(
                                PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS,
                                isChecked);
                        // Update Day List
                        parentFragment.updateDayList();
                    }
                });

        checkBoxRemember.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        PetimoSharedPref.getInstance().setSettingsBoolean(
                                PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_REMEMBER, isChecked);
                    }
                });

        checkBoxLock.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.d("EditBlocksFragment", "Lock isChecked ====> " + isChecked);
                        PetimoSharedPref.getInstance().setSettingsBoolean(
                                PetimoSharedPref.SETTINGS_MONITORED_BLOCKS_LOCK, isChecked);
                        checkBoxLock.setButtonDrawable(
                                isChecked ? R.drawable.ic_lock_outline_black_24dp :
                                        R.drawable.ic_lock_open_black_24dp);
                        // Update Day List & Adapter
                        parentFragment.reloadDayList();
                        //parentFragment.updateDayList();
                    }
                });

        textViewLock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                checkBoxLock.performClick();
            }
        });

    }

    /*private void updateContainerTopMargin(){
        Log.d(TAG, "My height is  =====> " + getView().getMeasuredHeight());
        RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams)
                container.getLayoutParams();
        containerParams.topMargin = -1 *getView().getMeasuredHeight();
        container.setLayoutParams(containerParams);
        //getView().bringToFront();
        parentContainer.bringChildToFront(getView());

    }
    */


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnEditBlocksMenuFragmentInteractionListener) {
            mListener = (OnEditBlocksMenuFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditBlocksMenuFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

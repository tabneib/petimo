package de.tud.nhd.petimo.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;
import de.tud.nhd.petimo.view.fragments.lists.adapters.CategoryRecyclerViewAdapter;
import de.tud.nhd.petimo.view.fragments.lists.adapters.MonitoredTaskRecyclerViewAdapter;

public class TaskSelectorBottomSheet extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private Listener mListener;
    public MonitoredTaskRecyclerViewAdapter adapter;
    private int mHistoryColumnCount = 2;

    private static final String ARG_FULL_EXPANDED = "ARG_FULL_EXPANDED";
    private static final String ARG_TRANSITION_STARTED = "ARG_TRANSITION_STARTED";

    RecyclerView recyclerView;
    LinearLayout container;
    CategoryRecyclerViewAdapter manualAdapter;
    MonitoredTaskRecyclerViewAdapter historyAdapter;



    // TODO: Customize parameters
    public static TaskSelectorBottomSheet newInstance() {
        final TaskSelectorBottomSheet fragment = new TaskSelectorBottomSheet();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_taskselectorbottomsheet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        container = (LinearLayout) view.findViewById(R.id.container);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        historyAdapter = new MonitoredTaskRecyclerViewAdapter(getActivity(),
                PetimoController.getInstance().getMonitoredTasks(), mListener);
        manualAdapter = new CategoryRecyclerViewAdapter(
                getActivity(),
                PetimoDbWrapper.getInstance().getAllCategories(),
                CategoryListFragment.VIEW_MODE, null, mListener);

        setupHistoryRecView(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        BottomSheetDialog dialog = (BottomSheetDialog)
                super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                BottomSheetDialog d = (BottomSheetDialog) dialog;

               // d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(
                        android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet)
                        .setState(BottomSheetBehavior.STATE_COLLAPSED);
                BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback
                        = new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet,
                                               @BottomSheetBehavior.State int newState) {
                        //Toast.makeText(getActivity(), "onStateChanged()", Toast.LENGTH_SHORT).show();

                        stateChanged(bottomSheet, newState,
                                BottomSheetBehavior.from(bottomSheet).getPeekHeight());
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    }
                };

                BottomSheetBehavior.from(bottomSheet).setBottomSheetCallback(mBottomSheetCallback);

            }
        });

        return dialog;
    }


    /**
     * @param bottomSheet
     * @param newState
     */
    private void stateChanged(@NonNull View bottomSheet,
                              @BottomSheetBehavior.State int newState, int peekHeight) {

        if (newState == BottomSheetBehavior.STATE_EXPANDED)
            setupManualRecView();
            //Toast.makeText(getActivity(), "STATE_EXPANDED", Toast.LENGTH_SHORT).show();
        else if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                setupHistoryRecView(false);
        else if (newState == BottomSheetBehavior.STATE_HIDDEN)
            // The user drag down to hide the bottomSheet
            dismiss();
    }

    /**
     *
     */
    private void setupHistoryRecView(boolean init){

        // By initialization of the bottomSheet, setup the history recyclerView but no transition
        if (init){
            recyclerView.setLayoutManager(new GridLayoutManager(
                    getActivity(), mHistoryColumnCount));

            recyclerView.setAdapter(historyAdapter);
        }

        // Only start transition if arrive this state from the fully expanded state !
        if (getArguments().getBoolean(ARG_FULL_EXPANDED) &&
                getArguments().getBoolean(ARG_TRANSITION_STARTED) ){
            TransitionDrawable transition =
                    (TransitionDrawable) container.getBackground();
            transition.reverseTransition(400);

            recyclerView.setLayoutManager(new GridLayoutManager(
                    getActivity(), mHistoryColumnCount));

            recyclerView.setAdapter(historyAdapter);
        }
        getArguments().putBoolean(ARG_TRANSITION_STARTED, true);
        getArguments().putBoolean(ARG_FULL_EXPANDED, false);


    }

    /**
     *
     */
    private void setupManualRecView(){


        // Only start transition if we arrive this state from the not fully expanded state !
        if (!getArguments().getBoolean(ARG_FULL_EXPANDED)){
            TransitionDrawable transition =
                    (TransitionDrawable) container.getBackground();
            transition.startTransition(200);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            recyclerView.setAdapter(manualAdapter);
        }
        getArguments().putBoolean(ARG_FULL_EXPANDED, true);

    }

    //TODO: remove this
    public interface Listener {
        void onTaskSelected(int catId, int taskId);
    }
}



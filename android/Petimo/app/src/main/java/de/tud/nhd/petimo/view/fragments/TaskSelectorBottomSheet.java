package de.tud.nhd.petimo.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
    RecyclerView recyclerView;
    View dummyView;
    CategoryRecyclerViewAdapter manualAdapter;
    MonitoredTaskRecyclerViewAdapter historyAdapter;



    // TODO: Customize parameters
    public static TaskSelectorBottomSheet newInstance() {
        final TaskSelectorBottomSheet fragment = new TaskSelectorBottomSheet();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_taskselectorbottomsheet_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        dummyView = view.findViewById(R.id.dummy_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        historyAdapter = new MonitoredTaskRecyclerViewAdapter(getActivity(),
                PetimoController.getInstance().getMonitoredTasks(), mListener);
        manualAdapter = new CategoryRecyclerViewAdapter(
                getActivity(),
                PetimoDbWrapper.getInstance().getAllCategories(),
                CategoryListFragment.VIEW_MODE, null, mListener);

        setupHistoryRecView();
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
                setupHistoryRecView();
        else if (newState == BottomSheetBehavior.STATE_HIDDEN)
            // The user drag down to hide the bottomSheet
            dismiss();
    }

    /**
     *
     */
    private void setupHistoryRecView(){
        recyclerView.setLayoutManager(new GridLayoutManager(
                getActivity(), mHistoryColumnCount));

        recyclerView.setAdapter(historyAdapter);
        recyclerView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
        dummyView.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
    }

    /**
     *
     */
    private void setupManualRecView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(manualAdapter);
        recyclerView.setBackgroundColor(getActivity().getResources().getColor(R.color.windowBackground));
        dummyView.setBackgroundColor(getActivity().getResources().getColor(R.color.windowBackground));
    }

    //TODO: remove this
    public interface Listener {
        void onTaskSelected(int catId, int taskId);
    }
}


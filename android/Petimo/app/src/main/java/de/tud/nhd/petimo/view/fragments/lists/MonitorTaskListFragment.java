package de.tud.nhd.petimo.view.fragments.lists;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.view.fragments.lists.adapters.MonitorTaskRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 */
public class MonitorTaskListFragment extends Fragment {

    private static final String TAG = "TaskListFragment";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_PARENT_CATEGORY = "parent-category";
    // default is linear layout
    private int mColumnCount = 1;
    private String parentCat;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MonitorTaskListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MonitorTaskListFragment newInstance(int columnCount, String parentCat) {
        MonitorTaskListFragment fragment = new MonitorTaskListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_PARENT_CATEGORY, parentCat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            parentCat = getArguments().getString(ARG_PARENT_CATEGORY);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_monitortask, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            Log.d(TAG, "gonna set my Task Adapter, parentCat =====> " + parentCat);
            recyclerView.setAdapter(new MonitorTaskRecyclerViewAdapter(
                    PetimoController.getInstance().getAllTasks(parentCat)));
            Log.d(TAG, "Adapter set ! parentCat =====> " + parentCat);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

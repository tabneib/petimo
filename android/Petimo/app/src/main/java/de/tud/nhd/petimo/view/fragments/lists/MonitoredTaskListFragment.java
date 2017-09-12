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
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.adapters.MonitoredTaskRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the
 * {@link OnModeFragmentInteractionListener}
 * interface.
 */
public class MonitoredTaskListFragment extends Fragment {

    private static final String TAG = "MonitoredTasksFragment";
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnModeFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MonitoredTaskListFragment() {
    }

    public static MonitoredTaskListFragment newInstance(int columnCount) {
        MonitoredTaskListFragment fragment = new MonitoredTaskListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate !");
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_monitoredtask, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MonitoredTaskRecyclerViewAdapter(
                    PetimoController.getInstance().getMonitoredTasks(), mListener));
            Log.d(TAG, "The Adapter set !");
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnModeFragmentInteractionListener) {
            mListener = (OnModeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMonitoredTaskFragmentInteractionListener");
        }

        Log.d(TAG, "onAttach!!!!!!");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume!!!!!!!!!!!!!!!");
    }
}

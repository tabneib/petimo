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
import de.tud.nhd.petimo.controller.TimeUtils;
import de.tud.nhd.petimo.model.MonitorBlock;
import de.tud.nhd.petimo.view.fragments.lists.adapters.MonitorBlockRecyclerViewAdapter;

/**
 * A fragment representing a list of Monitor blocks.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MonitorBlockListFragment extends Fragment {

    private static final String TAG = "BlockListFragment";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_START_DATE = "start-date";
    private static final String ARG_END_DATE = "end-date";
    // Use linear layout as default
    private int mColumnCount = 1;
    private int mStartDate = TimeUtils.getTodayDate();
    private int mEndDate = TimeUtils.getTodayDate();
    private OnListFragmentInteractionListener mListener;

    PetimoController controller;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MonitorBlockListFragment() {
    }

    // TODO: Customize parameter initialization
    public static MonitorBlockListFragment newInstance(int columnCount, int startDate, int endDate){
        MonitorBlockListFragment fragment = new MonitorBlockListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_START_DATE, startDate);
        args.putInt(ARG_END_DATE, endDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mStartDate = getArguments().getInt(ARG_START_DATE);
            mEndDate = getArguments().getInt(ARG_END_DATE);
        }
        try{
            controller = PetimoController.getInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitorblock_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            Log.d(TAG, "Date range: " + mStartDate + " -> " + mEndDate);
            recyclerView.setAdapter(new MonitorBlockRecyclerViewAdapter(
                    controller.getBlocksFromRange(mStartDate, mEndDate), mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MonitorBlock item);
    }
}

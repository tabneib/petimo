package de.tud.nhd.petimo.view.fragments.lists;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.utils.TimeUtils;
import de.tud.nhd.petimo.view.fragments.lists.adapters.BlockRecyclerViewAdapter;

/**
 * A fragment representing a list of Monitor blocks.
 * <p/>
 *
 */
public class BlockListFragment extends Fragment {

    private static final String TAG = "BlockListFragment";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_START_DATE = "start-date";
    private static final String ARG_END_DATE = "end-date";
    // Use linear layout as default
    private int mColumnCount = 1;
    // Default values for date range: today blocks
    private int mStartDate = TimeUtils.getTodayDate();
    private int mEndDate = TimeUtils.getTodayDate();


    PetimoController controller;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BlockListFragment() {
    }

    // TODO: Customize parameter initialization
    public static BlockListFragment newInstance(int columnCount, int startDate, int endDate) {
        BlockListFragment fragment = new BlockListFragment();
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
        try {
            controller = PetimoController.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment_monitorblock, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new BlockRecyclerViewAdapter(getActivity(),
                    controller.getBlocksFromRange(mStartDate, mEndDate)));
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

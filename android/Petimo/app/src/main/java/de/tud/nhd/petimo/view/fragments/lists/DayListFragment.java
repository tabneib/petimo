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
import de.tud.nhd.petimo.utils.TimeUtils;
import de.tud.nhd.petimo.view.fragments.listener.OnEditDayFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.adapters.DayRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnEditDayFragmentInteractionListener}
 * interface.
 */
public class DayListFragment extends Fragment {

    private static final String TAG = "DayListFragment";
    // Default is linear layout
    private static final String FROM_DATE = "from-date";
    private static final String TO_DATE = "to-date";


    // default is linear layout
    private int mColumnCount = 1;
    // default is the last 1 week
    private int fromDate = TimeUtils.getTodayDate()-6;
    private int toDate = TimeUtils.getTodayDate();

    private OnEditDayFragmentInteractionListener mListener;

    public DayRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DayListFragment() {
    }



    public static DayListFragment newInstance(int fromDate, int toDate) {
        DayListFragment fragment = new DayListFragment();
        Bundle args = new Bundle();
        args.putInt(FROM_DATE, fromDate);
        args.putInt(TO_DATE, toDate);
        return fragment;
    }

    public static DayListFragment newInstance() {
        return new DayListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null){
            fromDate = args.getInt(FROM_DATE);
            toDate = args.getInt(TO_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_monitorday, container, false);

        adapter = new DayRecyclerViewAdapter(
                this,
                PetimoController.getInstance().getDaysFromRange(fromDate, toDate, true), mListener);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            Log.d(TAG, "Setting the Adapter for the view !");
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditDayFragmentInteractionListener) {
            mListener = (OnEditDayFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditDayFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}

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

import java.util.List;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.MonitorCategory;
import de.tud.nhd.petimo.view.fragments.lists.adapters.MonitorCategoryRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 */
public class MonitorCategoryListFragment extends Fragment {

    private static final String TAG = "CatListFragment";
    private static MonitorCategoryListFragment _instance;
    private MonitorCategoryRecyclerViewAdapter adapter;
    private int mColumnCount = 1;
    private List<MonitorCategory> catList;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MonitorCategoryListFragment() {
    }

    /**
     * Get the (unique) instance of this fragment, if not yet exists, then initialize it
     * @return the unique instance
     */
    public static MonitorCategoryListFragment getInstance(){

        // TODO: I still cannot figure out the cause of this bug, so I comment out the code fragment
        return new MonitorCategoryListFragment();

        /*if (_instance == null) {
            _instance = new MonitorCategoryListFragment();
            return _instance;
        }
        else
            return _instance;*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_monitorcategory, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.catList = PetimoController.getInstance().getAllCats();
            this.adapter = new MonitorCategoryRecyclerViewAdapter(this, catList);
            recyclerView.setAdapter(adapter);
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

    /**
     * Update the recyclerView in case a new category is added into the data
     * @param newCatName Name of the newly added category
     */
    public void updateView(String newCatName){
        // Update the data stored in the adapter by adding the newly added category to the beginning
        // of the item list
        Log.d(TAG, "stored adapter is null ====> " + (adapter==null));
        Log.d(TAG, "stored catList size ====> " + catList.size());

        Log.d(TAG, "New cat object ====> " + PetimoController.getInstance().getCatByName(newCatName));
        //adapter.catList.add(0, PetimoController.getInstance().getCatByName(newCatName));
        // This is for logging purpose
        this.catList.add(0, PetimoController.getInstance().getCatByName(newCatName));
        // Then notify the adapter about the change to adapt the view
        adapter.notifyItemInserted(0);
        //adapter.notifyDataSetChanged();
    }

}
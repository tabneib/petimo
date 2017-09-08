package de.tud.nhd.petimo.view.fragments.lists;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.MonitorCategory;
import de.tud.nhd.petimo.view.fragments.lists.adapters.CategoryRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 */
public class CategoryListFragment extends Fragment {

    private static final String TAG = "CatListFragment";
    private static CategoryListFragment _instance;
    private CategoryRecyclerViewAdapter adapter;
    private int mColumnCount = 1;
    private List<MonitorCategory> catList;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CategoryListFragment() {
    }

    /**
     * Get the (unique) instance of this fragment, if not yet exists, then initialize it
     * @return the unique instance
     */
    public static CategoryListFragment getInstance(){

        // TODO: I still cannot figure out the cause of this bug, so I comment out the code fragment
        return new CategoryListFragment();

        /*if (_instance == null) {
            _instance = new CategoryListFragment();
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
        final View view = inflater.inflate(R.layout.list_monitorcategory, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                    new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                      RecyclerView.ViewHolder target) {
                    // Do nothing
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // Delete the category
                    PetimoController.getInstance().removeCategory(
                            adapter.catList.get(viewHolder.getLayoutPosition()).getName());
                    adapter.notifyItemRemoved(viewHolder.getLayoutPosition());
                    //adapter.notifyItemRangeRemoved(viewHolder.getOldPosition(),1);
                    adapter.catList.remove(viewHolder.getLayoutPosition());


                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

            this.catList = PetimoController.getInstance().getAllCats();
            this.adapter = new CategoryRecyclerViewAdapter(this, catList);
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

        Log.d(TAG, "New cat object ====> " +
                PetimoController.getInstance().getCatByName(newCatName).getName());
        //adapter.catList.add(0, PetimoController.getInstance().getCatByName(newCatName));
        // This is for logging purpose
        Log.d(TAG, "Before: catList in Fragment - size =====> " + catList.size());
        Log.d(TAG, "Before: catList in Adapter - size =====> " + this.adapter.catList.size());
        this.catList.add(0, PetimoController.getInstance().getCatByName(newCatName));

        //this.adapter.catList.add(0, PetimoController.getInstance().getCatByName(newCatName));
        Log.d(TAG, "After: catList in Fragment - size =====> " + catList.size());
        Log.d(TAG, "After: catList in Adapter - size =====> " + this.adapter.catList.size());
        // Then notify the adapter about the change to adapt the view
        this.adapter.notifyItemInserted(0);
        //adapter.notifyDataSetChanged();
    }

}

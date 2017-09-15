package de.tud.nhd.petimo.view.fragments.lists;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.MonitorCategory;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.lists.adapters.CategoryRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 */
public class CategoryListFragment extends Fragment {

    private static final String TAG = "CatListFragment";
    private static CategoryListFragment _instance;
    public CategoryRecyclerViewAdapter adapter;
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
        final View view = inflater.inflate(R.layout.list_fragment_monitorcategory, container, false);

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

                    final CategoryRecyclerViewAdapter.ViewHolder vHolder =
                            (CategoryRecyclerViewAdapter.ViewHolder) viewHolder;
                    final PetimoDialog removeCatDialog = PetimoDialog.newInstance()
                            .setTitle(getActivity().getString(R.string.title_remove_category))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage(getActivity().
                                    getString(R.string.message_confirm_remove) + vHolder.catName
                                    + "?")
                            .setPositiveButton(getString(R.string.button_yes),
                                    new PetimoDialog.OnClickListener(){
                                        @Override
                                        public void onClick(View v) {
                                            // Delete the category
                                            PetimoController.getInstance().removeCategory(
                                                    adapter.catList.get(
                                                            vHolder.getLayoutPosition()).getName());
                                            adapter.notifyItemRemoved(vHolder.getLayoutPosition());
                                            adapter.catList.remove(vHolder.getLayoutPosition());
                                        }
                                    })
                            .setNegativeButton(getString(R.string.button_cancel),
                                    new PetimoDialog.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                    removeCatDialog.show(getActivity().getSupportFragmentManager(), null);

                    /*
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(),
                                android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    final CategoryRecyclerViewAdapter.ViewHolder vHolder =
                            (CategoryRecyclerViewAdapter.ViewHolder) viewHolder;
                    builder.setTitle(
                            getActivity().getString(R.string.title_remove_category))
                            .setMessage(getActivity().
                                    getString(R.string.message_confirm_remove) +
                                        vHolder.catName + "?")
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            // Delete the category
                                            PetimoController.getInstance().removeCategory(
                                                    adapter.catList.get(
                                                            vHolder.getLayoutPosition()).getName());
                                            adapter.notifyItemRemoved(vHolder.getLayoutPosition());
                                            adapter.catList.remove(vHolder.getLayoutPosition());
                                        }
                                    })
                            .setNegativeButton(
                                    android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            adapter.notifyDataSetChanged();
                                            // do nothing
                                        }
                                    })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                            */
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
        // Update the category list of the recyclerView and force it to rebind all items
        this.catList.add(0, PetimoController.getInstance().getCatByName(newCatName));
        this.adapter.notifyItemInserted(0);
        this.adapter.notifyDataSetChanged();

        this.catList.clear();
        this.catList.addAll(PetimoController.getInstance().getAllCats());
        // bug: this.catList now points to other arrayList object, while adapter.catList still
        // points to the old object
        //this.catList = new ArrayList<>(PetimoController.getInstance().getAllCats());
        this.adapter.notifyDataSetChanged();

        // Old approach: add the new cat to the top of the recyclerView
        //this.catList.add(0, PetimoController.getInstance().getCatByName(newCatName));
        //this.adapter.notifyItemInserted(0);
    }

}

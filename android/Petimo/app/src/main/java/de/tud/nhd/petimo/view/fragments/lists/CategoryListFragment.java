package de.tud.nhd.petimo.view.fragments.lists;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.db.MonitorCategory;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.lists.adapters.CategoryRecyclerViewAdapter;
import de.tud.nhd.petimo.view.fragments.lists.adapters.TaskRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 */
public class CategoryListFragment extends Fragment {

    public static final String TAG = "CatListFragment";
    public static final String ARG_MODE = "ARG_MODE";
    public static final String ARG_SELECTOR_MODE = "ARG_SELECTOR_MODE";
    public static final String ARG_CAT_ID = "CAT_ID";

    public static final String EDIT_MODE = "Edit-mode";
    public static final String SELECT_MODE = "Select-mode";
    public static final String MODIFY_MODE = "Modify-mode";

    /**
     * This mode is used by @CategoryRecyclerViewAdapter to crate the CatAdapter, so has nothing
     * to do with this fragment. It is still placed here beside other modes.
     */
    public static final String VIEW_MODE = "View-mode";

    public String mode;
    public String selectorMode;

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
    public static CategoryListFragment getInstance(String mode, String selectorMode){

        // TODO: I still cannot figure out the cause of this bug, so I comment out the code fragment
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, mode);
        args.putString(ARG_SELECTOR_MODE, selectorMode);
        fragment.setArguments(args);

        return fragment;

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
        Bundle args = getArguments();
        if (args != null){
            this.mode = args.getString(ARG_MODE);
            this.selectorMode = args.getString(ARG_SELECTOR_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        switch (mode){
            case EDIT_MODE:
                return createEditModeView(inflater, container, savedInstanceState);
            case MODIFY_MODE:
                return createModifyModeView(inflater, container, savedInstanceState);
            case SELECT_MODE:
                return createSelectModeView(inflater, container, savedInstanceState);
            default:
                throw new RuntimeException("Display mode is not set.");
        }
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    private View createEditModeView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.list_fragment_category, container, false);

        // Set the dayAdapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            this.catList = PetimoDbWrapper.getInstance().getAllCategories();
            this.adapter = new CategoryRecyclerViewAdapter(this, catList, mode, selectorMode);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(150));

        }
        return view;
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    private View createModifyModeView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.list_fragment_category, container, false);

        // Set the dayAdapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                    new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder
                                viewHolder, RecyclerView.ViewHolder target) {
                            // Do nothing
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                            final CategoryRecyclerViewAdapter.ViewHolder vHolder =
                                    (CategoryRecyclerViewAdapter.ViewHolder) viewHolder;
                            final PetimoDialog removeCatDialog =
                                    PetimoDialog.newInstance(getActivity())
                                            .setTitle(getActivity().getString(
                                                    R.string.title_remove_category))
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setMessage(getActivity().
                                                    getString(R.string.message_confirm_remove) +
                                                    vHolder.category.getName() + "?")
                                            .setPositiveButton(getString(R.string.button_yes),
                                                    new PetimoDialog.OnClickListener(){
                                                        @Override
                                                        public void onClick(View v) {
                                                            // Delete the category
                                                            PetimoDbWrapper.getInstance().
                                                                    removeCategory(
                                                                            adapter.catList.get(vHolder.
                                                                                    getLayoutPosition()).
                                                                                    getId());
                                                            adapter.notifyItemRemoved(
                                                                    vHolder.getLayoutPosition());
                                                            adapter.catList.remove(
                                                                    vHolder.getLayoutPosition());
                                                            getActivity().onBackPressed();
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
                        }
                    };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

            // Only get the given Cat
            //this.catList = PetimoDbWrapper.getInstance().getAllCategories();
            this.catList = new ArrayList<>();
            this.catList.add(PetimoDbWrapper.getInstance().
                    getCatById(getArguments().getInt(CategoryListFragment.ARG_CAT_ID)));
            this.adapter = new CategoryRecyclerViewAdapter(this, catList, mode, selectorMode);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    private View createSelectModeView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState){
       final View view = inflater.inflate(R.layout.list_fragment_category, container, false);

        // Set the dayAdapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            this.catList = PetimoDbWrapper.getInstance().getAllCategories();
            this.adapter = new CategoryRecyclerViewAdapter(this, catList, mode, selectorMode);
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
     */
    public void updateView(){
        // Update the category list of the recyclerView and force it to rebind all items
        /*if (newCatName != null){
            this.catList.add(0, PetimoDbWrapper.getInstance().getCatById(
                    PetimoDbWrapper.getInstance().getCatIdFromName(newCatName)));
            this.adapter.notifyItemInserted(0);
        }

        this.adapter.notifyDataSetChanged();
        */
        this.catList.clear();
        if (getArguments().getInt(ARG_CAT_ID) != 0)
            this.catList.add(PetimoDbWrapper.getInstance().
                    getCatById(getArguments().getInt(CategoryListFragment.ARG_CAT_ID)));
        else
            this.catList.addAll(PetimoDbWrapper.getInstance().getAllCategories());
        // bug: this.catList now points to other arrayList object, while dayAdapter.catList still
        // points to the old object
        //this.catList = new ArrayList<>(PetimoController.getInstance().getAllCats());
        this.adapter.notifyDataSetChanged();

        // Old approach: add the new cat to the top of the recyclerView
        //this.catList.add(0, PetimoController.getInstance().getCatByName(newCatName));
        //this.dayAdapter.notifyItemInserted(0);
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = mVerticalSpaceHeight;
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnEditTaskListener {

        /**
         * Call-back method which is called when the user confirm adding a new task
         * @param viewHolder
         * @param catId
         * @param inputTaskName
         * @param priority
         */
        void onConfirmAddingTaskButtonClicked(
                CategoryRecyclerViewAdapter.ViewHolder viewHolder,
                int catId, String inputTaskName, int priority, String note);
  }

    public interface OnModifyTaskListener{
        void onConfirmEditingTaskButtonClicked(
                TaskRecyclerViewAdapter taskAdapter, int position,
                int taskId, String inputTaskName, int priority, String note);

        void onConfirmEditingCatButtonClicked(
                CategoryListFragment catListFragment,
                int catId, String newCatName, int priority, String note);

    }



}

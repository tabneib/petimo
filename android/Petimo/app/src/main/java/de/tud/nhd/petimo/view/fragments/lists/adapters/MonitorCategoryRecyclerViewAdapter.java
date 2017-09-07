package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.MonitorCategory;
import de.tud.nhd.petimo.view.fragments.lists.MonitorCategoryListFragment;
import de.tud.nhd.petimo.view.fragments.lists.MonitorTaskListFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link de.tud.nhd.petimo.model.MonitorCategory}
 */
public class MonitorCategoryRecyclerViewAdapter extends
        RecyclerView.Adapter<MonitorCategoryRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CatAdapter";
    public List<MonitorCategory> catList;
    private MonitorCategoryListFragment fragment;
    private int count = 0;
    private MonitorTaskListFragment taskListFragment;

    public MonitorCategoryRecyclerViewAdapter(
            MonitorCategoryListFragment fragment, List<MonitorCategory> items) {
        catList = items;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_monitorcategory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "gonna bind position ====> " + position +" ====> " + catList.get(position).getName());
        holder.category = catList.get(position);

        holder.catTextView.setText(catList.get(position).getName());

        // Setup the sub-fragment that displays the list of corresponding tasks

        fragment.getActivity().getSupportFragmentManager().beginTransaction().
                add(holder.taskListContainer.getId(), MonitorTaskListFragment.newInstance(
                        1, catList.get(position).getName())).commit();

        /*if (taskListFragment == null) {
            Log.d(TAG, "gonna setup the sub-fragment to display tasks");
            taskListFragment =
                    MonitorTaskListFragment.newInstance(1, catList.get(position).getName());
            fragment.getActivity().getSupportFragmentManager().beginTransaction().
                    add(holder.taskListContainer.getId(), taskListFragment).commit();
        }
        else
            Log.d(TAG, "sub-fragment already setup for this position !");*/

       // TODO setup listener for dragging to delete category
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }


    /**
     * ViewHolder that hold the view of a block displaying a category
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView catTextView;
        public FrameLayout taskListContainer;
        public MonitorCategory category;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.catTextView = (TextView) view.findViewById(R.id.textViewCatName);
            this.taskListContainer = (FrameLayout) view.findViewById(R.id.task_list_container);

            // Hard-coded for now by adding a large number (10000) to the id.
            // TODO: Solve the issue with ID varying in a more generic way !
            taskListContainer.setId(taskListContainer.getId() + 10000 + count);
            count++;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + catTextView.getText() + "'";
        }
    }
}

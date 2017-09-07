package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
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


    public MonitorCategoryRecyclerViewAdapter(
            MonitorCategoryListFragment fragment, List<MonitorCategory> items) {
        this.fragment = fragment;
        this.catList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_monitorcategory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "catList size ===> " + catList.size());
        Log.d(TAG, "gonna bind position ====> " + position +" ====> "
                + catList.get(position).getName());

        holder.catTextView.setText(catList.get(position).getName());


        // Nesting fragments inside RecyclerView is not recommended, so I use recyclerView directly

        holder.taskListRecyclerView.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
        holder.taskListRecyclerView.setAdapter(new MonitorTaskRecyclerViewAdapter(
                PetimoController.getInstance().getAllTasks(catList.get(position).getName())));

        // Setup the sub-fragment that displays the list of corresponding tasks
        /*MonitorTaskListFragment taskListFragment = (MonitorTaskListFragment) fragment.getActivity().
                getSupportFragmentManager().findFragmentByTag(
                TAG + "-" + catList.get(position).getName());*/


        /*
        if (taskListFragment == null) {
            // Generate an unique id for the container
            int genId = View.generateViewId();
            Log.d(TAG, "Generated ID  =====> " + genId);
            holder.taskListRecyclerView.setId(genId);
            Log.d(TAG, "TaskListFragment for this postition not yet created, so create, id ===> "+
            holder.taskListRecyclerView.getId());
            fragment.getActivity().getSupportFragmentManager().beginTransaction().
                    add(holder.taskListRecyclerView.getId(), MonitorTaskListFragment.newInstance(
                            1, catList.get(position).getName()),
                            TAG + "-" + catList.get(position).getName()).commit();
        }
        else{
            Log.d(TAG, "TaskListFragment for this postition already created, id ===> "+
                    holder.taskListRecyclerView.getId());
            fragment.getActivity().getSupportFragmentManager().beginTransaction().
                    replace(holder.taskListRecyclerView.getId(), taskListFragment).commit();
        }*/

        /*if (taskListFragment == null) {
            Log.d(TAG, "gonna setup the sub-fragment to display tasks");
            taskListFragment =
                    MonitorTaskListFragment.newInstance(1, catList.get(position).getName());
            fragment.getActivity().getSupportFragmentManager().beginTransaction().
                    add(holder.taskListRecyclerView.getId(), taskListFragment).commit();
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
        public RecyclerView taskListRecyclerView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.catTextView = (TextView) view.findViewById(R.id.textViewCatName);
            this.taskListRecyclerView = (RecyclerView) view.findViewById(R.id.task_list_recycler_view);


        }

        @Override
        public String toString() {
            return super.toString() + " '" + catTextView.getText() + "'";
        }
    }
}

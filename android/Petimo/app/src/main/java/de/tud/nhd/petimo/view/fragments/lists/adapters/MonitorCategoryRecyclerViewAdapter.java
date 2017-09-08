package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.MonitorCategory;
import de.tud.nhd.petimo.view.fragments.dialogs.AddTaskDialogFragment;
import de.tud.nhd.petimo.view.fragments.lists.MonitorCategoryListFragment;

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "gonna bind position ====> " + position +" ====> "
                + catList.get(position).getName());
        Log.d(TAG, "Adapter's catList size ====> " + this.catList.size());

        holder.catName = catList.get(position).getName();
        holder.catTextView.setText(catList.get(position).getName());
        holder.newTaskButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                AddTaskDialogFragment dialogFragment = new AddTaskDialogFragment();
                dialogFragment.catListFragment = fragment;
                dialogFragment.viewHolder = holder;
                // This is a bug! The value of position will be fixed at this point,
                // Hence it is nor updated upon adding new categor => yield wrong category name
                //dialogFragment.category = catList.get(position).getName();
                dialogFragment.category = holder.catName;
                dialogFragment.show(fragment.getActivity().getSupportFragmentManager(), null);
            }
        });

        // Nesting fragments inside RecyclerView is not recommended, so I use recyclerView directly

        holder.taskAdapter = new MonitorTaskRecyclerViewAdapter(
                PetimoController.getInstance().getAllTasks(catList.get(position).getName()));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        // Do nothing
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        // Delete the task
                        PetimoController.getInstance().removeTask(
                                holder.taskAdapter.taskList.get(viewHolder.getLayoutPosition()).getName(),
                                holder.taskAdapter.taskList.get(
                                        viewHolder.getLayoutPosition()).getCategory());

                        Log.d(TAG, "taskList size ===> " + holder.taskAdapter.taskList.size());
                        holder.taskAdapter.notifyItemRemoved(viewHolder.getLayoutPosition());
                        //adapter.notifyItemRangeRemoved(viewHolder.getOldPosition(),1);
                        holder.taskAdapter.taskList.remove(viewHolder.getLayoutPosition());

                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(holder.taskListRecyclerView);

        holder.taskListRecyclerView.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
        holder.taskListRecyclerView.setAdapter(holder.taskAdapter);







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
        public Button newTaskButton;
        public RecyclerView taskListRecyclerView;
        public String catName;
        // Each ViewHolder must have its own MonitorTaskRecyclerViewAdapter
        public MonitorTaskRecyclerViewAdapter taskAdapter;

        public ViewHolder(View view) {
            super(view);

            this.view = view;
            this.catTextView = (TextView) view.findViewById(R.id.textViewCatName);
            this.newTaskButton = (Button) view.findViewById(R.id.button_add_task);
            this.taskListRecyclerView = (RecyclerView) view.findViewById(R.id.task_list_recycler_view);
        }

        /**
         * Update the view in case there is some item newly added
         * @param taskName
         */
        public void updateView(String taskName, String catName){
            Log.d(TAG, "ViewHolder is gonna update with (" + taskName + ", " + catName + ")");
            Log.d(TAG, "ViewHolder updated the taskList by adding ====> "
                    + PetimoController.getInstance().getTaskByName(taskName, catName));
            this.taskAdapter.taskList.add(0,
                    PetimoController.getInstance().getTaskByName(taskName, catName));
            taskAdapter.notifyItemInserted(0);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + catTextView.getText() + "'";
        }
    }
}

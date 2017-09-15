package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
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
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link de.tud.nhd.petimo.model.MonitorCategory}
 */
public class CategoryRecyclerViewAdapter extends
        RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CatAdapter";
    public List<MonitorCategory> catList;
    private CategoryListFragment fragment;


    public CategoryRecyclerViewAdapter(
            CategoryListFragment fragment, List<MonitorCategory> items) {
        this.fragment = fragment;
        this.catList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);
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

        holder.taskAdapter = new TaskRecyclerViewAdapter(
                PetimoController.getInstance().getAllTasks(catList.get(position).getName()));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        // Do nothing
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                        final TaskRecyclerViewAdapter.ViewHolder vHolder =
                                (TaskRecyclerViewAdapter.ViewHolder) viewHolder;
                        PetimoDialog removeTaskDialog = new PetimoDialog()
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(fragment.getActivity().
                                        getString(R.string.title_remove_task))
                                .setMessage(fragment.getActivity().
                                        getString(R.string.message_confirm_remove)
                                        + vHolder.taskNameTextView.getText() + "?")
                                .setPositiveButton(
                                        fragment.getActivity().getString(R.string.button_yes),
                                        new PetimoDialog.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                // Delete the task
                                                PetimoController.getInstance().removeTask(
                                                        holder.taskAdapter.taskList.get(
                                                                vHolder.getLayoutPosition()).
                                                                getName(),
                                                        holder.taskAdapter.taskList.get(
                                                                vHolder.getLayoutPosition()).
                                                                getCategory());

                                                holder.taskAdapter.
                                                        notifyItemRemoved(vHolder.
                                                                getLayoutPosition());
                                                holder.taskAdapter.
                                                        taskList.remove(vHolder.
                                                        getLayoutPosition());
                                            }
                                        })
                                .setNegativeButton(
                                        fragment.getActivity().getString(R.string.button_cancel),
                                        new PetimoDialog.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                holder.taskAdapter.notifyDataSetChanged();
                                            }
                                        }
                                );
                        removeTaskDialog.show(
                                fragment.getActivity().getSupportFragmentManager(), null);
                        /*
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(fragment.getActivity(),
                                    android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(fragment.getActivity());
                        }
                        final TaskRecyclerViewAdapter.ViewHolder vHolder =
                                (TaskRecyclerViewAdapter.ViewHolder) viewHolder;
                        builder.setTitle(
                                fragment.getActivity().getString(R.string.title_remove_task))
                                .setMessage(fragment.getActivity().
                                        getString(R.string.message_confirm_remove)
                                        + vHolder.taskNameTextView.getText() + "?")
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        // Delete the task
                                        PetimoController.getInstance().removeTask(
                                                holder.taskAdapter.taskList.get(
                                                        vHolder.getLayoutPosition()).getName(),
                                                holder.taskAdapter.taskList.get(
                                                        vHolder.getLayoutPosition()).getCategory());

                                        holder.taskAdapter.
                                                notifyItemRemoved(vHolder.getLayoutPosition());
                                        holder.taskAdapter.
                                                taskList.remove(vHolder.getLayoutPosition());
                                    }
                                })
                                .setNegativeButton(
                                        android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        holder.taskAdapter.notifyDataSetChanged();
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                         */
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
     * BlockListViewHolder that hold the view of a block displaying a category
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView catTextView;
        public Button newTaskButton;
        public RecyclerView taskListRecyclerView;
        public String catName;
        // Each BlockListViewHolder must have its own TaskRecyclerViewAdapter
        public TaskRecyclerViewAdapter taskAdapter;

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
            this.taskAdapter.taskList.add(0,
                    PetimoController.getInstance().getTaskByName(taskName, catName));
            taskAdapter.notifyItemInserted(0);
            this.taskAdapter.taskList.clear();
            this.taskAdapter.taskList.addAll(PetimoController.getInstance().getAllTasks(catName));
            this.taskAdapter.notifyDataSetChanged();
        }

        @Override
        public String toString() {
            return super.toString() + " '" + catTextView.getText() + "'";
        }
    }
}

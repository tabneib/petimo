package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.db.MonitorCategory;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.model.sharedpref.SharedPref;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.listener.OnEditTaskFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MonitorCategory}
 */
public class CategoryRecyclerViewAdapter extends
        RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CatAdapter";
    public List<MonitorCategory> catList;
    private CategoryListFragment fragment;
    private String mode;
    private boolean onBind = false;


    public CategoryRecyclerViewAdapter(
            CategoryListFragment fragment, List<MonitorCategory> items, String mode) {
        this.fragment = fragment;
        this.catList = items;
        this.mode = mode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (mode){
            case CategoryListFragment.EDIT_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_category_edit, parent, false);
                break;
            case CategoryListFragment.SELECT_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_category_select, parent, false);
                break;
            default:
                throw new RuntimeException("Display mode is not set.");
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        switch (mode){
            case CategoryListFragment.EDIT_MODE:
                onBindViewHolderEditMode(holder, position);
                break;
            case CategoryListFragment.SELECT_MODE:
                onBindViewHolderSelectMode(holder, position);
                break;
            default:
                throw new RuntimeException("Display mode is not set.");
        }
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    /**
     *
     * @param holder
     * @param position
     */
    private void onBindViewHolderEditMode(final ViewHolder holder, final int position) {

        holder.category = catList.get(position);
        holder.catTextView.setText(catList.get(position).getName());
        holder.newTaskButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                PetimoDialog newTaskDialog = new PetimoDialog()
                        .setIcon(PetimoDialog.ICON_SAVE)
                        .setTitle(fragment.getActivity().getString(R.string.title_new_task))
                        .setContentLayout(R.layout.dialog_add_task)
                        .setPositiveButton(fragment.getActivity().getString(R.string.button_create),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        OnEditTaskFragmentInteractionListener mListener;
                                        EditText taskInput = (EditText)
                                                view.findViewById(R.id.editTextTaskName);
                                        Spinner prioritySpinner = (Spinner)
                                                view.findViewById(R.id.spinnerPriorities);;
                                        try {
                                            mListener = (OnEditTaskFragmentInteractionListener)
                                                    fragment.getActivity();
                                        } catch (ClassCastException e) {
                                            throw new ClassCastException(
                                                    fragment.getActivity().toString()
                                                            + " must implement " +
                                                            "OnEditTaskFragmentInteractionListener");
                                        }
                                        mListener.onConfirmAddingTaskButtonClicked(
                                                holder,
                                                // This is a bug! The value of position will be
                                                // fixed at this point, Hence it is nor updated
                                                // upon adding new category => yield wrong category
                                                // name
                                                //catList.get(position).getName();
                                                holder.category.getId(),
                                                taskInput.getText().toString(),
                                                prioritySpinner.getSelectedItemPosition(),
                                                "");
                                    }
                                })
                        .setNegativeButton(fragment.getActivity().getString(R.string.button_cancel),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // do nothing
                                    }
                                });
                newTaskDialog.show(fragment.getActivity().getSupportFragmentManager(), null);
            }
        });

        // Nesting fragments inside RecyclerView is not recommended, so I use recyclerView directly
        holder.taskAdapter = new TaskRecyclerViewAdapter(
                PetimoDbWrapper.getInstance().getTasksByCat(catList.get(position).getId()),
                mode, this, position);

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
                                .setIcon(PetimoDialog.ICON_WARNING)
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
                                                PetimoDbWrapper.getInstance().removeTask(
                                                        holder.taskAdapter.taskList.get(
                                                                vHolder.getLayoutPosition()).
                                                                getId());

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
                        // Old approach: use Dialog builder, replaced with customized PetimoDialog ;)
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
                                                        vHolder.getLayoutPosition()).getCatName());

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

    }


    /**
     *
     * @param holder
     * @param position
     */
    private void onBindViewHolderSelectMode(final ViewHolder holder, final int position) {

        holder.category = catList.get(position);
        holder.catCheckBox.setText(catList.get(position).getName());
        holder.taskAdapter = new TaskRecyclerViewAdapter(
                PetimoDbWrapper.getInstance().getTasksByCat(catList.get(position).getId()), mode,
                this, position);
        holder.taskListRecyclerView.setLayoutManager(
                new LinearLayoutManager(fragment.getActivity()));
        holder.taskListRecyclerView.setAdapter(holder.taskAdapter);

        ArrayList<Integer> tasks = SharedPref.getInstance().getSelectedTasks();
        int selectedTaskNum = 0;
        for (int taskId: tasks)
            if (PetimoDbWrapper.getInstance().getCatIdFromTask(taskId) ==
                    catList.get(position).getId())
                selectedTaskNum++;
        // If all tasks belonging to this category are selected => checked
        if (selectedTaskNum == PetimoDbWrapper.getInstance().
                getTaskIdsByCat(catList.get(position).getId()).size()) {
            onBind = true;
            holder.catCheckBox.setChecked(true);
            onBind = false;
        }
        else{
            onBind = true;
            holder.catCheckBox.setChecked(false);
            onBind = false;
        }
    }



        /**
         * BlockListViewHolder that hold the view of a block displaying a category
         */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        // Edit Mode Attributes
        public TextView catTextView;
        public Button newTaskButton;

        // Select Mode Attributes
        public CheckBox catCheckBox;

        // Common Attributes
        public RecyclerView taskListRecyclerView;
        public MonitorCategory category;
        // Each BlockListViewHolder must have its own TaskRecyclerViewAdapter
        public TaskRecyclerViewAdapter taskAdapter;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            // Edit Mode
            this.catTextView = (TextView) view.findViewById(R.id.textViewCatName);
            this.newTaskButton = (Button) view.findViewById(R.id.button_add_task);

            // Select Mode
            this.catCheckBox = (CheckBox) view.findViewById(R.id.checkboxCat);

            // Common
            this.taskListRecyclerView = (RecyclerView)
                    view.findViewById(R.id.task_list_recycler_view);

            switch (mode){
                case CategoryListFragment.EDIT_MODE:
                    break;
                case CategoryListFragment.SELECT_MODE:
                    catCheckBox.setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(
                                        CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked)
                                        // Add all tasks that belong to this cat
                                        for (int taskId :
                                                PetimoDbWrapper.getInstance().
                                                        getTaskIdsByCat(category.getId()))
                                            SharedPref.getInstance().
                                                    addSelectedTask(taskId);
                                    else
                                        // remove all tasks of this cat
                                        for (int taskId :
                                                PetimoDbWrapper.getInstance().
                                                        getTaskIdsByCat(category.getId()))
                                            SharedPref.getInstance().
                                                    removeSelectedTask(taskId);
                                    if (!onBind)
                                        taskAdapter.notifyDataSetChanged();
                                }
                            });
                    break;
            }
        }

        /**
         * Update the view in case there is some item newly added
         * @param taskName
         */
        public void updateView(String taskName, int catId){
            if (mode.equals(CategoryListFragment.EDIT_MODE)){
                this.taskAdapter.taskList.add(0,
                        PetimoDbWrapper.getInstance().getTaskById(
                                PetimoDbWrapper.getInstance().getTaskIdFromName(taskName, catId)));
                taskAdapter.notifyItemInserted(0);
                this.taskAdapter.taskList.clear();
                this.taskAdapter.taskList.addAll(
                        PetimoDbWrapper.getInstance().getTasksByCat(catId));
                this.taskAdapter.notifyDataSetChanged();

            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + catTextView.getText() + "'";
        }
    }
}

package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.db.MonitorCategory;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.model.sharedpref.TaskSelector;
import de.tud.nhd.petimo.view.activities.ModifyTasksActivity;
import de.tud.nhd.petimo.view.fragments.TaskSelectorBottomSheet;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
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
    public CategoryListFragment catListFragment;
    private Activity activity;
    private String mode;
    private String selectorMode;
    private boolean onBind = false;
    private TaskSelectorBottomSheet.Listener mListener;


    public CategoryRecyclerViewAdapter(
            CategoryListFragment fragment, List<MonitorCategory> items,
            String mode, String selectorMode) {
        this.catListFragment = fragment;
        this.catList = items;
        this.mode = mode;
        this.selectorMode = selectorMode;
    }

    public CategoryRecyclerViewAdapter(
            Activity activity, List<MonitorCategory> items,
            String mode, String selectorMode, TaskSelectorBottomSheet.Listener listener) {
        this.activity = activity;
        this.catList = items;
        this.mode = mode;
        this.selectorMode = selectorMode;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (mode) {
            case CategoryListFragment.EDIT_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_category_edit, parent, false);
                break;
            case CategoryListFragment.MODIFY_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_category_modify, parent, false);
                break;
            case CategoryListFragment.SELECT_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_category_select, parent, false);
                break;
            case CategoryListFragment.VIEW_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_category_view, parent, false);
                break;
            default:
                throw new RuntimeException("Unknown Display Mode: " + mode);

        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        switch (mode){
            case CategoryListFragment.EDIT_MODE:
                onBindViewHolderEditMode(holder, position);
                break;
            case CategoryListFragment.MODIFY_MODE:
                onBindViewHolderModifyMode(holder, position);
                break;
            case CategoryListFragment.SELECT_MODE:
                onBindViewHolderSelectMode(holder, position);
                break;
            case CategoryListFragment.VIEW_MODE:
                onBindViewHolderViewMode(holder, position);
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

        holder.position = position;
        holder.category = catList.get(position);
        holder.catTextView.setText(catList.get(position).getName());
        holder.newTaskButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                PetimoDialog newTaskDialog = new PetimoDialog()
                        .setIcon(PetimoDialog.ICON_SAVE)
                        .setTitle(catListFragment.getActivity().getString(R.string.title_new_task))
                        .setContentLayout(R.layout.dialog_add_task)
                        .setPositiveButton(catListFragment.getActivity().getString(R.string.button_create),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CategoryListFragment.OnEditTaskListener
                                                mListener;
                                        EditText taskInput = (EditText)
                                                view.findViewById(R.id.editTextTaskName);
                                        Spinner prioritySpinner = (Spinner)
                                                view.findViewById(R.id.spinnerPriorities);;
                                        try {
                                            mListener = (CategoryListFragment.OnEditTaskListener)
                                                    catListFragment.getActivity();
                                        } catch (ClassCastException e) {
                                            throw new ClassCastException(
                                                    catListFragment.getActivity().toString()
                                                            + " must implement " +
                                                            "CategoryListFragment." +
                                                            "CategoryListFragment." +
                                                            "OnEditTaskListener");
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
                        .setNegativeButton(catListFragment.getActivity().getString(R.string.button_cancel),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // do nothing
                                    }
                                });
                newTaskDialog.show(catListFragment.getActivity().getSupportFragmentManager(), null);
            }
        });

        // Nesting fragments inside RecyclerView is not recommended, so I use recyclerView directly
        holder.taskAdapter = new TaskRecyclerViewAdapter(
                PetimoDbWrapper.getInstance().getTasksByCat(catList.get(position).getId()),
                mode, selectorMode, this, position);

        holder.taskListRecyclerView.setLayoutManager(new LinearLayoutManager(catListFragment.getActivity()));
        holder.taskListRecyclerView.setAdapter(holder.taskAdapter);

    }


    /**
     *
     * @param holder
     * @param position
     */
    private void onBindViewHolderModifyMode(final ViewHolder holder, final int position) {

        holder.position = position;
        holder.category = catList.get(position);
        holder.catTextView.setText(catList.get(position).getName());

        // Nesting fragments inside RecyclerView is not recommended, so I use recyclerView directly
        holder.taskAdapter = new TaskRecyclerViewAdapter(
                PetimoDbWrapper.getInstance().getTasksByCat(catList.get(position).getId()),
                mode, selectorMode, this, position);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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
                                .setTitle(catListFragment.getActivity().
                                        getString(R.string.title_remove_task))
                                .setMessage(catListFragment.getActivity().
                                        getString(R.string.message_confirm_remove)
                                        + vHolder.taskNameTextView.getText() + "?")
                                .setPositiveButton(
                                        catListFragment.getActivity().getString(R.string.button_yes),
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
                                        catListFragment.getActivity().getString(R.string.button_cancel),
                                        new PetimoDialog.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                holder.taskAdapter.notifyDataSetChanged();
                                            }
                                        }
                                );
                        removeTaskDialog.show(
                                catListFragment.getActivity().getSupportFragmentManager(), null);
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(holder.taskListRecyclerView);

        holder.taskListRecyclerView.setLayoutManager(new LinearLayoutManager(catListFragment.getActivity()));
        holder.taskListRecyclerView.setAdapter(holder.taskAdapter);

    }

    /**
     *
     * @param holder
     * @param position
     */
    private void onBindViewHolderSelectMode(final ViewHolder holder, final int position) {

        holder.position = position;
        holder.category = catList.get(position);
        holder.catCheckBox.setText(catList.get(position).getName());
        holder.taskAdapter = new TaskRecyclerViewAdapter(
                PetimoDbWrapper.getInstance().getTasksByCat(catList.get(position).getId()), mode,
                selectorMode, this, position);
        holder.taskListRecyclerView.setLayoutManager(
                new LinearLayoutManager(catListFragment.getActivity()));
        holder.taskListRecyclerView.setAdapter(holder.taskAdapter);

        ArrayList<Integer> tasks = TaskSelector.getInstance().
                getSelectedTasks(selectorMode);
        int selectedTaskNum = 0;
        for (int taskId: tasks)
            if (PetimoDbWrapper.getInstance().getCatIdFromTask(taskId) ==
                    catList.get(position).getId())
                selectedTaskNum++;
        // If all tasks belonging to this category are selected => checked
        if (selectedTaskNum == PetimoDbWrapper.getInstance().
                getTaskIdsByCat(catList.get(position).getId()).size() && selectedTaskNum != 0) {
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
     *
     * @param holder
     * @param position
     */
    private void onBindViewHolderViewMode(final ViewHolder holder, final int position) {

        holder.category = catList.get(position);
        holder.catTextView.setText(catList.get(position).getName());

        holder.taskAdapter = new TaskRecyclerViewAdapter(
                PetimoDbWrapper.getInstance().getTasksByCat(catList.get(position).getId()),
                mode, selectorMode, position, mListener);
        holder.taskListRecyclerView.setLayoutManager(new GridLayoutManager(this.activity, 2));

        //holder.taskListRecyclerView.setLayoutManager(new LinearLayoutManager(this.activity));
        holder.taskListRecyclerView.setAdapter(holder.taskAdapter);


    }

    CategoryRecyclerViewAdapter getAdapter(){
        return this;
    }

    /**
     * BlockListViewHolder that hold the view of a block displaying a category
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public int position;
        // Edit Mode Attributes
        public TextView catTextView;
        public ImageView newTaskButton;
        public ImageView editCatButton;

        // Modify Mode Attributes
        public ImageView editButton;

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
            this.newTaskButton = (ImageView) view.findViewById(R.id.button_add_task);
            this.editCatButton = (ImageView) view.findViewById(R.id.button_edit_cat);

            // Modify Mode
            this.editButton = (ImageView) view.findViewById(R.id.imageView_edit);

            // Select Mode
            this.catCheckBox = (CheckBox) view.findViewById(R.id.checkboxCat);

            // Common
            this.taskListRecyclerView = (RecyclerView)
                    view.findViewById(R.id.task_list_recycler_view);

            switch (mode){
                case CategoryListFragment.EDIT_MODE:
                    editCatButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(catListFragment.getActivity(),
                                    ModifyTasksActivity.class);
                            intent.putExtra(ModifyTasksActivity.ARG_CAT_ID, category.getId());
                            catListFragment.getActivity().startActivity(intent);
                        }
                    });
                    break;
                case CategoryListFragment.MODIFY_MODE:
                    editButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            PetimoDialog newCatDialog = new PetimoDialog()
                                    .setIcon(PetimoDialog.ICON_SAVE)
                                    .setTitle(catListFragment.getActivity().
                                            getString(R.string.title_edit_category))
                                    .setContentLayout(R.layout.dialog_add_category)
                                    .setOnViewCreatedTask(new PetimoDialog.OnViewCreatedTask() {
                                        @Override
                                        public void execute(View view) {
                                            ((TextView) view.findViewById(R.id.editTextCatName)).
                                                    setText(category.getName());
                                            Spinner spinner = (Spinner)
                                                    view.findViewById(R.id.spinnerPriorities);
                                            if (category.getPriority() < spinner.getAdapter().getCount())
                                                spinner.setSelection(category.getPriority());
                                        }
                                    })
                                    .setPositiveButton(catListFragment.getActivity().
                                                    getString(R.string.button_create),
                                            new PetimoDialog.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    CategoryListFragment.OnModifyTaskListener mListener;
                                                    EditText catInput = (EditText)
                                                            view.findViewById(R.id.editTextCatName);
                                                    Spinner prioritySpinner = (Spinner)
                                                            view.findViewById(R.id.spinnerPriorities);
                                                    try {
                                                        mListener = (CategoryListFragment.OnModifyTaskListener)
                                                                catListFragment.getActivity();
                                                    } catch (ClassCastException e) {
                                                        throw new ClassCastException(
                                                                catListFragment.getActivity().toString()
                                                                        + " must implement " +
                                                                        "CategoryListFragment." +
                                                                        "OnModifyTaskListener");
                                                    }
                                                    mListener.onConfirmEditingCatButtonClicked(
                                                            catListFragment,
                                                            category.getId(),
                                                            catInput.getText().toString(),
                                                            prioritySpinner.getSelectedItemPosition(),
                                                            "");

                                                }
                                            })
                                    .setNegativeButton(catListFragment.getActivity().
                                                    getString(R.string.button_cancel),
                                            new PetimoDialog.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    // do nothing
                                                }
                                            });
                            newCatDialog.show(
                                    catListFragment.getActivity().getSupportFragmentManager(), null);
                        }
                    });
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
                                            TaskSelector.getInstance().add(taskId);
                                    else
                                        // remove all tasks of this cat
                                        for (int taskId :
                                                PetimoDbWrapper.getInstance().
                                                        getTaskIdsByCat(category.getId()))
                                            TaskSelector.getInstance().remove(taskId);
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

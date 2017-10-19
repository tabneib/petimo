package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.db.MonitorTask;
import de.tud.nhd.petimo.model.sharedpref.TaskSelector;
import de.tud.nhd.petimo.view.fragments.TaskSelectorBottomSheet;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MonitorTask}
 */
public class TaskRecyclerViewAdapter extends
        RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    public static final String TAG = "TaskAdapter";
    private String mode;
    private String selectorMode;
    // Select mode attributes
    private CategoryRecyclerViewAdapter catAdapter;
    private int catPosition;
    public List<MonitorTask> taskList;
    private boolean onBind = false;
    private TaskSelectorBottomSheet.Listener mListener;


    public TaskRecyclerViewAdapter(List<MonitorTask> items, String mode, String selectorMode,
                                   CategoryRecyclerViewAdapter catAdapter, int catPosition) {
        this.taskList = items;
        this.catAdapter = catAdapter;
        this.catPosition = catPosition;
        this.mode = mode;
        this.selectorMode = selectorMode;
    }

    public TaskRecyclerViewAdapter(List<MonitorTask> items, String mode, String selectorMode,
                                   int catPosition, TaskSelectorBottomSheet.Listener listener) {
        this.taskList = items;
        this.mListener = listener;
        this.catPosition = catPosition;
        this.mode = mode;
        this.selectorMode = selectorMode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (mode){
            case CategoryListFragment.EDIT_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_task_edit, parent, false);
                break;
            case CategoryListFragment.MODIFY_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_task_modify, parent, false);
                break;
            case CategoryListFragment.SELECT_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_task_select, parent, false);
                break;
            case CategoryListFragment.VIEW_MODE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_task_view, parent, false);
                break;
            default:
                throw new RuntimeException("Display mode is not set.");
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.position = position;
        switch (mode){
            case CategoryListFragment.EDIT_MODE:
                holder.task = taskList.get(position);
                holder.taskNameTextView.setText(taskList.get(position).getName());
                break;
            case CategoryListFragment.MODIFY_MODE:
                holder.task = taskList.get(position);
                holder.taskNameTextView.setText(taskList.get(position).getName());
                break;

            case CategoryListFragment.SELECT_MODE:
                holder.task = taskList.get(position);
                holder.taskCheckBox.setText(taskList.get(position).getName());

                boolean notFound = true;

                // Check if the task is already selected
                if (TaskSelector.getInstance().
                        getSelectedTasks(selectorMode).
                        contains(holder.task.getId())){
                    onBind = true;
                    holder.taskCheckBox.setChecked(true);
                    onBind = false;
                    notFound = false;
                    break;
                }

                if (notFound) {
                    onBind = true;
                    holder.taskCheckBox.setChecked(false);
                    onBind = false;
                }
                break;
            case CategoryListFragment.VIEW_MODE:
                holder.task = taskList.get(position);
                holder.taskNameTextView.setText(taskList.get(position).getName());
                // TODO: add listener
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        holder.itemContainer.setBackgroundColor(
                                ((Context) mListener).getResources().getColor(
                                        R.color.colorPrimaryLightXX));
                        holder.cardView.animate().scaleXBy(-0.2f).
                                scaleYBy(-0.2f).setDuration(100).
                                setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                holder.cardView.animate().scaleXBy(0.2f).
                                        scaleYBy(0.2f).setDuration(100).
                                        setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mListener.onTaskSelected(
                                                holder.task.getCatId(), holder.task.getId());
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                    }
                });
                break;
            default:
                throw new RuntimeException("Display mode is not set.");
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    TaskRecyclerViewAdapter getAdapter(){
        return this;
    }

    /**
     *
     * @param editedTask
     * @param position
     */
    public void updateView(MonitorTask editedTask, int position){
        this.taskList.remove(position);
        this.taskList.add(position, editedTask);
        notifyItemChanged(position);
    }

    /**
     * BlockListViewHolder that holds a task item to display
     * TODO adapt according to display mode!
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public int position;
        // Edit mode
        public TextView taskNameTextView;

        // Modify mode
        public ImageView editButton;

        // Select mode
        public CheckBox taskCheckBox;

        // View Mode
        public FrameLayout itemContainer;
        public CardView cardView;

        // Common
        public MonitorTask task;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            // Edit Mode
            taskNameTextView = (TextView) view.findViewById(R.id.textViewTaskName);
            // Modify Mode
            editButton = (ImageView) view.findViewById(R.id.imageView_edit);
            // Select Mode
            taskCheckBox = (CheckBox) view.findViewById(R.id.checkboxTask);
            // View Mode
            itemContainer = (FrameLayout) view.findViewById(R.id.item_container);
            cardView = (CardView) view.findViewById(R.id.cardView);

            switch (mode){
                case CategoryListFragment.EDIT_MODE:
                    break;
                case CategoryListFragment.MODIFY_MODE:
                    editButton.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            PetimoDialog newTaskDialog = new PetimoDialog()
                                    .setTitle(catAdapter.catListFragment.getActivity().
                                            getString(R.string.title_edit_task))
                                    .setContentLayout(R.layout.dialog_add_task)
                                    .setOnViewCreatedTask(new PetimoDialog.OnViewCreatedTask() {
                                        @Override
                                        public void execute(View view) {
                                            ((TextView) view.findViewById(R.id.editTextTaskName)).
                                                    setText(task.getName());
                                            Spinner spinner = (Spinner)
                                                    view.findViewById(R.id.spinnerPriorities);
                                            if (task.getPriority() < spinner.getAdapter().getCount())
                                                spinner.setSelection(task.getPriority());
                                        }
                                    })
                                    .setPositiveButton(catAdapter.catListFragment.getActivity().
                                                    getString(R.string.button_ok),
                                            new PetimoDialog.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    CategoryListFragment.OnModifyTaskListener mListener;
                                                    EditText taskInput = (EditText)
                                                            view.findViewById(R.id.editTextTaskName);
                                                    Spinner prioritySpinner = (Spinner)
                                                            view.findViewById(R.id.spinnerPriorities);;
                                                    try {
                                                        mListener = (CategoryListFragment.OnModifyTaskListener)
                                                                catAdapter.catListFragment.getActivity();
                                                    } catch (ClassCastException e) {
                                                        throw new ClassCastException(
                                                                catAdapter.catListFragment.getActivity().toString()
                                                                        + " must implement " +
                                                                        "CategoryListFragment." +
                                                                        "OnModifyTaskListener");
                                                    }
                                                    mListener.onConfirmEditingTaskButtonClicked(
                                                            getAdapter(), position, task.getId(),
                                                            taskInput.getText().toString(),
                                                            prioritySpinner.getSelectedItemPosition(),
                                                            "");
                                                }
                                            })
                                    .setNegativeButton(catAdapter.catListFragment.getActivity().getString(R.string.button_cancel),
                                            new PetimoDialog.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    // do nothing
                                                }
                                            });
                            newTaskDialog.show(catAdapter.catListFragment.getActivity().
                                    getSupportFragmentManager(), null);
                        }
                    });

                    break;
                case CategoryListFragment.SELECT_MODE:

                    taskCheckBox.setOnCheckedChangeListener(
                            new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(
                                        CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked)
                                        TaskSelector.getInstance().add(task.getId());
                                    else
                                        TaskSelector.getInstance().remove(task.getId());
                                    // Rebind the corresponding category
                                    //catAdapter.onBindViewHolder(catViewHolder, catPosition);
                                    // Notify the corresponding cat viewHolder to update its check box
                                    if (!onBind)
                                        catAdapter.notifyItemChanged(catPosition);
                                }
                            });
                    break;
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + taskNameTextView.getText() + "'";
        }
    }
}

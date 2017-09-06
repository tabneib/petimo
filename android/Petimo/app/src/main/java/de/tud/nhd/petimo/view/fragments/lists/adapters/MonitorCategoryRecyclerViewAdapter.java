package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private final List<MonitorCategory> catList;
    private MonitorCategoryListFragment fragment;

    public MonitorCategoryRecyclerViewAdapter(
            MonitorCategoryListFragment fragment, List<MonitorCategory> items) {
        catList = items;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_monitorcategory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.category = catList.get(position);

        holder.catTextView.setText(catList.get(position).getName());

        // Setup the sub-fragment that displays the list of corresponding tasks
        fragment.getActivity().getSupportFragmentManager().beginTransaction().
                add(holder.taskListContainerID, MonitorTaskListFragment.newInstance(
                        1, catList.get(position).getName())).commit();

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
        public int taskListContainerID;
        public MonitorCategory category;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.catTextView = (TextView) view.findViewById(R.id.textViewCatName);
            this.taskListContainerID = R.id.task_list_container;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + catTextView.getText() + "'";
        }
    }
}

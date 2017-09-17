package de.tud.nhd.petimo.view.fragments.lists.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.PetimoSharedPref;

import java.util.Arrays;
import java.util.List;


public class CatTaskRecyclerViewAdapter
        extends RecyclerView.Adapter<CatTaskRecyclerViewAdapter.ViewHolder> {

    private List<String[]> catTaskList;
    private List<String[]> selected;

    public CatTaskRecyclerViewAdapter(List<String[]> items, List<String[]> selected) {
        this.catTaskList = items;
        this.selected = selected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_cattask, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.catTask = catTaskList.get(position);
        holder.checkBox.setText(holder.catTask[0] + " / " + holder.catTask[1]);
        // Set check
        for (String[] item : selected){
            if (item[0].equals(holder.catTask[0]) && item[1].equals(holder.catTask[1])){
                holder.checkBox.setChecked(true);
                break;
            }
        }
        // Set listener
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    PetimoSharedPref.getInstance().addSelectedTask(
                            holder.catTask[0], holder.catTask[1]);
                else
                    PetimoSharedPref.getInstance().removeSelectedTask(
                            holder.catTask[0], holder.catTask[1]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return catTaskList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        // {Category, Task}
        public String[] catTask;
        public CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            mView = view;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + Arrays.toString(catTask) + "'";
        }
    }
}

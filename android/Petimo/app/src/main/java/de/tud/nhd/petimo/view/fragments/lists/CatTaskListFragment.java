package de.tud.nhd.petimo.view.fragments.lists;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.PetimoSharedPref;
import de.tud.nhd.petimo.view.fragments.lists.adapters.CatTaskRecyclerViewAdapter;

public class CatTaskListFragment extends Fragment {


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CatTaskListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CatTaskListFragment newInstance() {
        CatTaskListFragment fragment = new CatTaskListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment_cattask, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            // TODO: create the list of cat-task

            ArrayList<String[]> catTasks = new ArrayList<>();
            for (String cat : PetimoController.getInstance().getAllCatNames()){
                for (String task: PetimoController.getInstance().getTaskNameByCat(cat))
                    catTasks.add(new String[]{cat, task});
            }
            recyclerView.setAdapter(new CatTaskRecyclerViewAdapter(
                    catTasks, PetimoSharedPref.getInstance().getSelectedTasks()));
        }
        return view;
    }
}

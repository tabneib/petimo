package de.tud.nhd.petimo.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;
import de.tud.nhd.petimo.view.fragments.lists.adapters.CategoryRecyclerViewAdapter;
import de.tud.nhd.petimo.view.fragments.lists.adapters.TaskRecyclerViewAdapter;

public class ModifyTasksActivity extends AppCompatActivity
            implements CategoryListFragment.OnModifyTaskListener{

    private static final String TAG = "ModifyTasksActivity";

    public static final String ARG_CAT_ID = "ARG_CAT_ID";

    private CategoryListFragment catListFragment;
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_task);

        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.activity_modifytasks_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the ID of the category to be edited
        int catId = getIntent().getIntExtra(ARG_CAT_ID, -1);
        if (catId == -1)
            onBackPressed();
        else{
            catListFragment = CategoryListFragment.getInstance(
                    CategoryListFragment.MODIFY_MODE, null);
            Bundle bundle = catListFragment.getArguments();
            bundle.putInt(CategoryListFragment.ARG_CAT_ID, catId);
            catListFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(
                    R.id.cat_list_fragment_container, catListFragment).commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onConfirmEditingTaskButtonClicked(
            TaskRecyclerViewAdapter taskAdapter, int position,
            int taskId, String inputTaskName, int priority, String note) {
        try{
            PetimoDbWrapper.getInstance().modifyTask(taskId, inputTaskName, priority, note);
        }
        catch (DbErrorException e){
            // TODO notify the user
            e.printStackTrace();
        }
        // Update the recyclerView
        taskAdapter.updateView(PetimoDbWrapper.getInstance().getTaskById(taskId), position);
    }

    @Override
    public void onConfirmEditingCatButtonClicked(
            CategoryListFragment catListFragment,
            int catId, String newCatName, int priority, String note) {
        try{
            PetimoDbWrapper.getInstance().modifyCategory(catId, newCatName, priority, note);
        }
        catch (DbErrorException e){
            // TODO notify the user
            e.printStackTrace();
        }
        // Update the recyclerView
        catListFragment.updateView();
    }
}

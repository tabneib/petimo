package de.tud.nhd.petimo.view.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.controller.exception.DbErrorException;
import de.tud.nhd.petimo.controller.exception.InvalidCategoryException;
import de.tud.nhd.petimo.controller.exception.InvalidInputNameException;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;
import de.tud.nhd.petimo.view.fragments.lists.adapters.CategoryRecyclerViewAdapter;

public class EditTasksActivity extends AppCompatActivity
        implements CategoryListFragment.OnEditTaskListener {

    private static final String TAG = "EditTasksActivity";
    private static final String TASK_LIST_FRAGMENT_TAG = "TASK_LIST_FRAGMENT_TAG";
    private ImageView addCatButton;
    CategoryListFragment catListFragment;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tasks);

        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.activity_edittasks_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        catListFragment = (CategoryListFragment)
                getSupportFragmentManager().findFragmentByTag(TASK_LIST_FRAGMENT_TAG);
        if (catListFragment == null){
            catListFragment = CategoryListFragment.getInstance(
                    CategoryListFragment.EDIT_MODE, null);
            getSupportFragmentManager().beginTransaction().add(
                    R.id.cat_list_fragment_container, catListFragment, TASK_LIST_FRAGMENT_TAG).commit();
        }
        else
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.cat_list_fragment_container, catListFragment, TASK_LIST_FRAGMENT_TAG).commit();

        addCatButton = (ImageView) findViewById(R.id.button_add_cat);

        addCatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PetimoDialog newCatDialog = new PetimoDialog()
                        .setIcon(PetimoDialog.ICON_SAVE)
                        .setTitle(getString(R.string.title_new_category))
                        .setContentLayout(R.layout.dialog_add_category)
                        .setPositiveButton(getString(R.string.button_create),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        EditText catInput = (EditText)
                                                view.findViewById(R.id.editTextCatName);
                                        Spinner prioritySpinner = (Spinner)
                                                view.findViewById(R.id.spinnerPriorities);

                                        onConfirmAddingCatButtonClicked(
                                                catListFragment,
                                                catInput.getText().toString(),
                                                prioritySpinner.getSelectedItemPosition(),
                                                "");
                                    }
                                })
                        .setNegativeButton(getString(R.string.button_cancel),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // do nothing
                                    }
                                });
                newCatDialog.show(getSupportFragmentManager(), null);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        CategoryListFragment catListFragment = (CategoryListFragment)
                getSupportFragmentManager().findFragmentByTag(TASK_LIST_FRAGMENT_TAG);
        if (catListFragment != null)
            catListFragment.updateView();
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


    public void onConfirmAddingCatButtonClicked(
            CategoryListFragment catListFragment, String newCatName, int priority, String note) {

        try{
            PetimoController.getInstance().addCategory(newCatName, priority, note);
        }
        catch (DbErrorException e){
            // TODO Notify the user !
        }
        catch (InvalidCategoryException e){
            // TODO Check for this during the user is typing !
        }
        catch(InvalidInputNameException e){
            // TODO Check for this during the user is typing !
        }

        // TODO display a snack bar to notify the usr
        // Just for now: display a Toast
        Toast.makeText(this, "Added new category: " + newCatName, Toast.LENGTH_LONG).show();

        // Update the recyclerView
        catListFragment.updateView();


        /* Hard-coded: Re-add the whole CategoryListFragment
        getActivity().getSupportFragmentManager().beginTransaction().
                remove(CategoryListFragment.getInstance()).commit();

        getActivity().getSupportFragmentManager().beginTransaction().add(
                R.id.tasks_list_fragment_container,
                CategoryListFragment.getInstance()).commit();*/

    }


    @Override
    public void onConfirmAddingTaskButtonClicked(
            CategoryRecyclerViewAdapter.ViewHolder viewHolder,
            int catId, String inputTask, int priority, String note) {

        // Add new task
        try{
            PetimoController.getInstance().addTask(inputTask, catId, priority, note);
        }
        catch (InvalidInputNameException e){
            e.printStackTrace();
            // TODO
        }
        catch (InvalidCategoryException e){
            e.printStackTrace();
            // TODO
        }
        catch (DbErrorException e){
            e.printStackTrace();
            // TODO
        }
        // TODO display a snack bar to notify the usr
        // Just for now: display a Toast
        Toast.makeText(this, "Added new task: " + inputTask, Toast.LENGTH_LONG).show();

        // Update the recyclerView
        viewHolder.updateView(inputTask, catId);

    }
}

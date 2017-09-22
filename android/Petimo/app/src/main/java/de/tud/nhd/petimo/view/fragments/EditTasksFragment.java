package de.tud.nhd.petimo.view.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.listener.OnEditTaskFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditTasksFragment extends Fragment {


    private static final String TAG = "EditTaskFragment";
    private static EditTasksFragment _instance;
    private Button addCatButton;
    private CategoryListFragment catListFragment;

    public EditTasksFragment() {
        // Required empty public constructor
    }

    /**
     * Return an (unique) instance of {@link EditTasksFragment}, if not yet exists then initialize
     * @return the EditTaskFragment instance
     */
    public static EditTasksFragment getInstance(){
        if(_instance == null){
            _instance = new EditTasksFragment();
            Log.d(TAG, "Initialized ! =====> " + _instance);
            return _instance;
        }
        else return _instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_tasks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        catListFragment = CategoryListFragment.getInstance(CategoryListFragment.EDIT_MODE);
        getActivity().getSupportFragmentManager().beginTransaction().add(
                R.id.tasks_list_fragment_container, catListFragment).commit();


        addCatButton = (Button) this.getActivity().findViewById(R.id.button_add_task);
        addCatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PetimoDialog newCatDialog = new PetimoDialog()
                        .setIcon(PetimoDialog.ICON_SAVE)
                        .setTitle(getActivity().getString(R.string.title_new_category))
                        .setContentLayout(R.layout.dialog_add_category)
                        .setPositiveButton(getActivity().getString(R.string.button_create),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        OnEditTaskFragmentInteractionListener mListener;
                                        EditText catInput = (EditText)
                                                view.findViewById(R.id.editTextCatName);
                                        Spinner prioritySpinner = (Spinner)
                                                view.findViewById(R.id.spinnerPriorities);
                                        try {
                                           mListener = (OnEditTaskFragmentInteractionListener)
                                                            getActivity();
                                        } catch (ClassCastException e) {
                                            throw new ClassCastException(getActivity().toString()
                                                    + " must implement " +
                                                    "OnEditTaskFragmentInteractionListener");
                                        }
                                        mListener.onConfirmAddingCatButtonClicked(
                                                catListFragment,
                                                catInput.getText().toString(),
                                                prioritySpinner.getSelectedItemPosition());
                                    }
                                })
                        .setNegativeButton(getActivity().getString(R.string.button_cancel),
                                new PetimoDialog.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // do nothing
                                    }
                                });
                newCatDialog.show(getActivity().getSupportFragmentManager(), null);
            }
        });

    }
}

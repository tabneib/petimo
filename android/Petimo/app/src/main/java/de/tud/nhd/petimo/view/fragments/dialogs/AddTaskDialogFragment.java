package de.tud.nhd.petimo.view.fragments.dialogs;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.view.fragments.listener.OnEditTaskFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.MonitorCategoryListFragment;
import de.tud.nhd.petimo.view.fragments.lists.adapters.MonitorCategoryRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskDialogFragment extends DialogFragment {


    OnEditTaskFragmentInteractionListener mListener;
    private EditText taskInput;
    private Spinner prioritySpinner;
    private Button positiveButton;
    private Button negativeButton;

    public MonitorCategoryListFragment catListFragment = null;
    public MonitorCategoryRecyclerViewAdapter.ViewHolder viewHolder = null;
    public String category;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnEditTaskFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnModeFragmentInteractionListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_task, container);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.taskInput = (EditText) this.getView().findViewById(R.id.editTextTaskName);

        this.positiveButton = (Button) this.getView().findViewById(R.id.positiveButton);
        this.negativeButton = (Button) this.getView().findViewById(R.id.negativeButton);

        this.prioritySpinner= (Spinner) this.getView().findViewById(R.id.spinnerPriorities);


        positiveButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mListener.onConfirmAddingTaskStopButtonClicked(
                        viewHolder,
                        category,
                        taskInput.getText().toString(),
                        prioritySpinner.getSelectedItemPosition());
                dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}

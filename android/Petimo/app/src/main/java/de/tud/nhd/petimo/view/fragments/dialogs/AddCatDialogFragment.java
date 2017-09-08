package de.tud.nhd.petimo.view.fragments.dialogs;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.view.fragments.listener.OnEditTaskFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.CategoryListFragment;

public class AddCatDialogFragment extends DialogFragment {

    OnEditTaskFragmentInteractionListener mListener;
    private EditText catInput;
    private Button positiveButton;
    private Button negativeButton;
    private Spinner prioritySpinner;
    public CategoryListFragment catListFragment = null;

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
        return inflater.inflate(R.layout.dialog_add_category, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.catInput = (EditText) this.getView().findViewById(R.id.editTextCatName);
        this.positiveButton = (Button) this.getView().findViewById(R.id.positiveButton);
        this.negativeButton = (Button) this.getView().findViewById(R.id.negativeButton);
        this.prioritySpinner= (Spinner) this.getView().findViewById(R.id.spinnerPriorities);
        positiveButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mListener.onConfirmAddingCatButtonClicked(
                        catListFragment,
                        catInput.getText().toString(),
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

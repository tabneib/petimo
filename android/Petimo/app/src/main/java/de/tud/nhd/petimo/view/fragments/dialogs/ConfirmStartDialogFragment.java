package de.tud.nhd.petimo.view.fragments.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;

public class ConfirmStartDialogFragment extends DialogFragment {

    OnModeFragmentInteractionListener mListener;
    public static final String CATEGORY = "category";
    public static final String TASK = "task";



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnModeFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnModeFragmentInteractionListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        String msg = getString(R.string.message_confirm_start_monitor) + ":\n" +
                args.getString(CATEGORY) + " / " + args.getString(TASK);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setPositiveButton(R.string.button_start, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onConfirmStartButtonClicked(
                                args.getString(CATEGORY), args.getString(TASK));
                    }
                })
                .setNegativeButton(R.string.button_cancel,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        return builder.create();
    }
}

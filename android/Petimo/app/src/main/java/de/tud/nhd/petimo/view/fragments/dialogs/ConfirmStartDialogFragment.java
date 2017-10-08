package de.tud.nhd.petimo.view.fragments.dialogs;


import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.db.PetimoDbWrapper;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;

public class ConfirmStartDialogFragment extends Fragment {

    public static final String TAG = "ConfirmStartDialog";
    OnModeFragmentInteractionListener mListener;

    int catId;
    int taskId;

    private TextView textViewCatTask;
    private TextView textViewStartTime;
    private TextClock textClock;
    private Button buttonEdit;

    // array storing the hour and minute chosen by user as start time
    public int[] manualTime = null;

    public static ConfirmStartDialogFragment newInstance(int catId, int taskId){
        ConfirmStartDialogFragment fragment = new ConfirmStartDialogFragment();
        fragment.catId = catId;
        fragment.taskId = taskId;
        Log.d(TAG, "catId/taskId ====> " + catId + " / " + taskId);
        return fragment;
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        // Bug: Must not attach to root => false
        // see: https://stackoverflow.com/questions/19301458/
        return inflater.inflate(R.layout.dialog_confirm_start_monitor, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewCatTask = (TextView) getView().findViewById(R.id.textViewCatTask);
        textViewStartTime = (TextView) getView().findViewById(R.id.textViewStartTime);
        textClock = (TextClock) getView().findViewById(R.id.textClock);
        buttonEdit = (Button) getView().findViewById(R.id.button_edit);

        final Bundle args = getArguments();
        textViewCatTask.setText(PetimoDbWrapper.getInstance().getCatNameById(catId) + " / " +
                PetimoDbWrapper.getInstance().getTaskNameById(taskId));
        textViewStartTime.setVisibility(View.INVISIBLE);

        buttonEdit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        // Check if the selected time is valid
                        if (PetimoController.getInstance().
                                checkValidLiveStartTime(selectedHour, selectedMinute)){
                            textViewStartTime.setText(
                                    (selectedHour<10 ? "0" + selectedHour : selectedHour) +
                                            ":" + (selectedMinute<10 ? "0" + selectedMinute :
                                            selectedMinute));
                            textViewStartTime.setVisibility(View.VISIBLE);
                            textClock.setPaintFlags(
                                    textClock.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            textClock.setActivated(false);
                            manualTime = new int[]{selectedHour, selectedMinute};
                        }
                        else
                            Toast.makeText(
                                    getActivity(), getString(R.string.message_invalid_start_time),
                                    Toast.LENGTH_SHORT).show();


                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.title_select_start_time));
                mTimePicker.show();
            }
        });

        /*
        buttonPositive.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if (manualTime != null){
                    mListener.onConfirmStartButtonClicked(args.getString(CATEGORY),
                            args.getString(TASK), PetimoTimeUtils.getTimeMillisFromHM(
                                    manualTime[0], manualTime[1]));
                }
                else{
                    mListener.onConfirmStartButtonClicked(args.getString(CATEGORY),
                            args.getString(TASK), System.currentTimeMillis());
                }
                dismiss();
            }
        });

        buttonNegative.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        */
        //Log.d(TAG, "textClock ====> " + textClock.getT);
    }

    /*
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
    */

}

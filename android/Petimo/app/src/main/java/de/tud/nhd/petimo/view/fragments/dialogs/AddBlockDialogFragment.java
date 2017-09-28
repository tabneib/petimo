package de.tud.nhd.petimo.view.fragments.dialogs;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.PetimoContract;
import de.tud.nhd.petimo.model.PetimoDbWrapper;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;

public class AddBlockDialogFragment extends Fragment {

    public static final String TAG = "AddBlockDialog";
    public int date;
    Calendar mcurrentTime;

    TextView subTitle;
    public Spinner catSpinner;
    public Spinner taskSpinner;
    Button startButton;
    Button endButton;

    public long[] manualTime = new long[2];


    public AddBlockDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param date
     * @return A new instance of fragment AddBlockDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddBlockDialogFragment newInstance(int date) {
        AddBlockDialogFragment dialog = new AddBlockDialogFragment();
        dialog.date = date;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcurrentTime = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_add_block, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subTitle = (TextView) view.findViewById(R.id.textViewDate);
        catSpinner = (Spinner) view.findViewById(R.id.spinnerCat);
        taskSpinner = (Spinner) view.findViewById(R.id.spinnerTask);
        startButton = (Button) view.findViewById(R.id.button_start);
        endButton = (Button) view.findViewById(R.id.button_end);

        subTitle.setText(PetimoTimeUtils.getDateStrFromInt(date));
        initCatSpinner();


        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateTaskSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO implement or remove
            }
        });




        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(
                                    TimePicker timePicker, int selectedHour, int selectedMinute) {
                                // Check if the selected time is valid
                                if (PetimoController.getInstance().
                                        checkValidManualStartTime(
                                                date, selectedHour, selectedMinute)) {
                                    startButton.setText(
                                            (selectedHour < 10 ? "0" + selectedHour : selectedHour)
                                                    + ":" + (selectedMinute < 10 ? "0" +
                                                    selectedMinute : selectedMinute));
                                    manualTime[0] = PetimoTimeUtils.getTimeMillisFromHM(
                                            date, selectedHour, selectedMinute);
                                    // Reset end time
                                    manualTime[1] = 0;
                                    endButton.setText(getActivity().getString(R.string.loading_text));
                                }
                                else
                                    Toast.makeText(
                                            getActivity(),
                                            getString(R.string.message_invalid_manual_start_time),
                                            Toast.LENGTH_SHORT).show();
                            }
                        },
                        mcurrentTime.get(Calendar.HOUR_OF_DAY),
                        mcurrentTime.get(Calendar.MINUTE),
                        true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.title_select_start_time));
                mTimePicker.show();
            }
        });


        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker,
                                                  int selectedHour, int selectedMinute) {
                                // Check is start time is already set
                                if (manualTime[0] == 0){
                                    Toast.makeText(getActivity(),
                                            getString(R.string.message_no_manual_start_time),
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Check if the selected time is valid
                                if (PetimoController.getInstance().
                                        checkValidManualStopTime(
                                                date, selectedHour, selectedMinute, manualTime[0])){
                                    endButton.setText(
                                            (selectedHour<10 ? "0" + selectedHour : selectedHour) +
                                                    ":" + (selectedMinute<10 ? "0" + selectedMinute
                                                    : selectedMinute));
                                    manualTime[1] = PetimoTimeUtils.getTimeMillisFromHM(
                                            date, selectedHour, selectedMinute);
                                }
                                else
                                    Toast.makeText(
                                            getActivity(), getString(
                                                    R.string.message_invalid_manual_stop_time),
                                            Toast.LENGTH_SHORT).show();
                            }
                        },
                        mcurrentTime.get(Calendar.HOUR_OF_DAY),
                        mcurrentTime.get(Calendar.MINUTE),
                        true);//Yes 24 hour time
                mTimePicker.setTitle(getString(R.string.title_select_stop_time));
                mTimePicker.show();
            }
        });
    }


    /**
     * Initialize the contents of the category spinner with data from the database
     * and set focus on the last monitored task
     */
    private void initCatSpinner(){
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                ArrayAdapter<String> catSpinnerAdapter = new ArrayAdapter<>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        PetimoDbWrapper.getInstance().getAllCatNames());
                catSpinner.setAdapter(catSpinnerAdapter);
                int catPos = PetimoController.getInstance().getLastMonitoredTask()[0];
                catSpinner.setSelection(catPos, true);
                catSpinnerAdapter.notifyDataSetChanged();

                // init task spinner accordingly
                updateTaskSpinner();
                taskSpinner.setSelection(
                        PetimoController.getInstance().getLastMonitoredTask()[1], true);
            }
        });
    }


    /**
     * Update the contents of the task spinner according to the selected item of the cat spinner
     */
    private void updateTaskSpinner(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Case there is no category, catSpinner.getSelectedItem() will be null ;)
                if (catSpinner.getSelectedItem() != null) {
                    ArrayAdapter<String> taskSpinnerAdapter = new ArrayAdapter<String>(getContext(),
                            R.layout.support_simple_spinner_dropdown_item,
                            PetimoDbWrapper.getInstance().getTaskNamesByCat(
                                    PetimoDbWrapper.getInstance().getAllCatIds().
                                            get(catSpinner.getSelectedItemPosition())));
                    taskSpinner.setAdapter(taskSpinnerAdapter);
                    taskSpinnerAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}

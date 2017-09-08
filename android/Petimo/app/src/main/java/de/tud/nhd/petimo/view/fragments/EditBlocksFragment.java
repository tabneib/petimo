package de.tud.nhd.petimo.view.fragments;


import android.app.DatePickerDialog;
import java.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.view.fragments.lists.DayListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditBlocksFragment extends Fragment {

    private static final String TAG = "EditBlocksFragment";
    private static EditBlocksFragment _instance;
    Button fromDateButton;
    Button toDateButton;
    Calendar myCalendar = Calendar.getInstance();

    public EditBlocksFragment() {
        // Required empty public constructor
    }

    /**
     * Return an (unique) instance of {@link EditBlocksFragment}, if not yet exists then initialize
     * @return the EditBlocksFragment instance
     */
    public static EditBlocksFragment getInstance(){
        if (_instance == null){
            _instance = new EditBlocksFragment();
            return _instance;
        }
        else
            return _instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_blocks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup DatePicker dialogs
        fromDateButton = (Button) view.findViewById(R.id.button_date_from);
        toDateButton = (Button) view.findViewById(R.id.button_date_to);





        fromDateButton.setOnClickListener(new View.OnClickListener(){

            DatePickerDialog.OnDateSetListener onDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month,
                                              int dayOfMonth) {
                            // TODO Auto-generated method stub
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, month);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        }

            };

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), onDateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        toDateButton.setOnClickListener(new View.OnClickListener(){

            DatePickerDialog.OnDateSetListener onDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, month);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        }
                    };
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), onDateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Fill in the fragment to display day list
        getActivity().getSupportFragmentManager().beginTransaction().add(
                R.id.day_list_fragment_container, DayListFragment.newInstance()).commit();
    }
}

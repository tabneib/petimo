package de.tud.nhd.petimo.view.fragments;


import android.app.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.controller.TimeUtils;
import de.tud.nhd.petimo.view.fragments.lists.DayListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditBlocksFragment extends Fragment {

    private static final String TAG = "EditBlocksFragment";
    private static EditBlocksFragment _instance;
    Button fromDateButton;
    Button toDateButton;
    Button showButton;
    Calendar fromCalendar = Calendar.getInstance();
    Calendar toCalendar = Calendar.getInstance();

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

        // Setup DatePicker dialogs and update textViews accordingly
        fromDateButton = (Button) view.findViewById(R.id.button_date_from);
        toDateButton = (Button) view.findViewById(R.id.button_date_to);
        showButton = (Button) view.findViewById(R.id.button_display_day_range);

        // default date range is the last 1 week
        fromCalendar.setTime(new Date());
        toCalendar.setTime(new Date());
        fromCalendar.add(Calendar.DATE, -6);

        fromDateButton.setText(TimeUtils.getDateStrFromCalendar(fromCalendar));
        toDateButton.setText(TimeUtils.getDateStrFromCalendar(toCalendar));

        fromDateButton.setOnClickListener(new View.OnClickListener(){

            DatePickerDialog.OnDateSetListener onDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month,
                                              int dayOfMonth) {
                            fromCalendar.set(Calendar.YEAR, year);
                            fromCalendar.set(Calendar.MONTH, month);
                            fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            // Update the fromButton accordingly to display the date
                            fromDateButton.setText(TimeUtils.getDateStrFromCalendar(fromCalendar));
                        }

            };

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), onDateSetListener, fromCalendar
                        .get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                        fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        toDateButton.setOnClickListener(new View.OnClickListener(){

            DatePickerDialog.OnDateSetListener onDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int month, int dayOfMonth) {
                            toCalendar.set(Calendar.YEAR, year);
                            toCalendar.set(Calendar.MONTH, month);
                            toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            // Update the toButton accordingly to display the date
                            toDateButton.setText(TimeUtils.getDateStrFromCalendar(toCalendar));

                        }
                    };
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), onDateSetListener, toCalendar
                        .get(Calendar.YEAR), toCalendar.get(Calendar.MONTH),
                        toCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        showButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DayListFragment dayListFragment = (DayListFragment) getActivity().
                        getSupportFragmentManager().findFragmentByTag(TAG + "day_list_fragment");
                if(dayListFragment != null){
                    dayListFragment.adapter.dayList = PetimoController.getInstance().
                            getDaysFromRange(TimeUtils.getDateIntFromCalendatr(fromCalendar),
                                    TimeUtils.getDateIntFromCalendatr(toCalendar), true);
                    dayListFragment.adapter.notifyDataSetChanged();
                }
            }
        });

        // Fill in the fragment to display day list
        getActivity().getSupportFragmentManager().beginTransaction().add(
                R.id.day_list_fragment_container, DayListFragment.newInstance(),
                TAG + "day_list_fragment").commit();
    }
}

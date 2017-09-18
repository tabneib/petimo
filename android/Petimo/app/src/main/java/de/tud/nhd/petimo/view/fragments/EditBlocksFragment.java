package de.tud.nhd.petimo.view.fragments;


import android.app.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.PetimoSharedPref;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.view.fragments.lists.DayListFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditBlocksFragment extends Fragment {

    private static final String TAG = "EditBlocksFragment";
    private static final String MENU_FRAGMENT_TAG = TAG + "-menu";
    private static EditBlocksFragment _instance;
    Button fromDateButton;
    Button toDateButton;
    ImageButton showButton;
    ImageButton menuImageButton;
    RelativeLayout menuContainer;
    RelativeLayout listContainer;
    LinearLayout parentContainer;
    LinearLayout header;
    Calendar fromCalendar = Calendar.getInstance();
    Calendar toCalendar = Calendar.getInstance();
    boolean menuOpened = false;

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

        menuContainer = (RelativeLayout) view.findViewById(R.id.drop_down_menu_container);
        listContainer = (RelativeLayout) view.findViewById(R.id.day_list_fragment_container);
        parentContainer = (LinearLayout) view.findViewById(R.id.parentContainer) ;
        header = (LinearLayout) view.findViewById(R.id.header);

        menuImageButton = (ImageButton) view.findViewById(R.id.button_menu);

        // Setup DatePicker dialogs and update textViews accordingly
        fromDateButton = (Button) view.findViewById(R.id.button_date_from);
        toDateButton = (Button) view.findViewById(R.id.button_date_to);
        showButton = (ImageButton) view.findViewById(R.id.button_display_day_range);


        // default date range is the last 1 week
        fromCalendar.setTime(new Date());
        toCalendar.setTime(new Date());
        fromCalendar.add(Calendar.DATE, -6);

        fromDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(fromCalendar));
        toDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(toCalendar));
        //fromDateButton.setPaintFlags(fromDateButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //toDateButton.setPaintFlags(toDateButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


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
                            fromDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(fromCalendar));
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
                            toDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(toCalendar));

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
                updateDayList();
            }
        });

        header.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                updateMenuDisplay(true);
            }
        });

        menuImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateMenuDisplay(true);
            }
        });
        // update menu display anyway
        updateMenuDisplay(false);

        // Fill in the fragment to display day list
        getActivity().getSupportFragmentManager().beginTransaction().add(
                R.id.day_list_fragment_container, DayListFragment.newInstance(),
                TAG + "day_list_fragment").commit();
    }

    /**
     * Force the recyclerView to rebind all items
     */
    public void updateDayList(){
        DayListFragment dayListFragment = (DayListFragment) getActivity().
                getSupportFragmentManager().findFragmentByTag(TAG + "day_list_fragment");
        if(dayListFragment != null){
            dayListFragment.adapter.dayList = PetimoController.getInstance().
                    getDaysFromRange(PetimoTimeUtils.getDateIntFromCalendatr(fromCalendar),
                            PetimoTimeUtils.getDateIntFromCalendatr(toCalendar),
                            PetimoSharedPref.getInstance().getSettingsBoolean(PetimoSharedPref.
                                    SETTINGS_MONITORED_BLOCKS_SHOW_EMPTY_DAYS, true),
                            PetimoSharedPref.getInstance().getSettingsBoolean(PetimoSharedPref.
                                    SETTINGS_MONITORED_BLOCKS_SHOW_SELECTED_TASKS, false));
            dayListFragment.adapter.notifyDataSetChanged();
        }
    }

    public void reloadDayList(){
        DayListFragment dayListFragment = (DayListFragment) getActivity().
                getSupportFragmentManager().findFragmentByTag(TAG + "day_list_fragment");
        if(dayListFragment != null) {
            dayListFragment.adapter.notifyDataSetChanged();
        }
    }

    /**
     *
     * @param isClicked
     */
    private void updateMenuDisplay(boolean isClicked){
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Drawable upIcon = getResources().getDrawable(
                R.drawable.ic_arrow_drop_up_black_24dp, null);
        Drawable downIcon = getResources().getDrawable(
                R.drawable.ic_arrow_drop_down_black_24dp, null);

        EditBlocksMenuFragment menuFragment = (EditBlocksMenuFragment)
                fm.findFragmentByTag(MENU_FRAGMENT_TAG);
        if(menuFragment != null){
            // old menuFragment found
            // menu is currently opened
            if (menuOpened) {
                // Open, User clicked ==> close
                ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
                ft.hide(menuFragment).commit();
                menuOpened = false;
                menuImageButton.setBackground(downIcon);
            }
            // menu is currently closed
            else {
                // Closed, User clicked => open
                ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
                ft.show(menuFragment).commit();
                menuOpened = true;
                menuImageButton.setBackground(upIcon);
            }
        }
        else{
            // init
            if(isClicked) {
                // Open menu for the first time
                ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
                ft.add(menuContainer.getId(),
                        EditBlocksMenuFragment.newInstance(this), MENU_FRAGMENT_TAG).commit();
                menuOpened = true;
                menuImageButton.setBackground(upIcon);
            }
            else{
                // Navigate (back) to this view
                // Navigate to this View
                if (menuOpened) {
                    // Reconstruct old state -> open
                    ft.add(menuContainer.getId(),
                            EditBlocksMenuFragment.newInstance(this), MENU_FRAGMENT_TAG).commit();
                    menuImageButton.setBackground(upIcon);
                }
            }
        }
    }
}

package de.tud.nhd.petimo.view.fragments.menu;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;

public class PetimoDatePickerMenu extends Fragment {

    private OnDateRangeChangeListener mListener;
    private android.support.v7.widget.CardView menu;
    private ImageButton menuButton;
    private RelativeLayout menuLayout;
    Button fromDateButton;
    Button toDateButton;

    Calendar fromCalendar = java.util.Calendar.getInstance();
    Calendar toCalendar = java.util.Calendar.getInstance();

    private boolean menuOpened = true;
    private final long ANIMATION_SPEED = 300;

    public PetimoDatePickerMenu() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment PetimoDatePickerMenu.
     */
    public static PetimoDatePickerMenu newInstance() {
        PetimoDatePickerMenu fragment = new PetimoDatePickerMenu();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_petimo_date_picker_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menu = (android.support.v7.widget.CardView) view.findViewById(R.id.menu);
        menuLayout = (RelativeLayout) view.findViewById(R.id.menu_layout);
        menuButton = (ImageButton) view.findViewById(R.id.menu_button);
        menuButton.getBackground().setAlpha(127);
        menuLayout.getBackground().setAlpha(255);
        menuButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                updateMenuDisplay();
            }
        });

        // DatePicker
        fromDateButton = (Button) view.findViewById(R.id.button_date_from);
        toDateButton = (Button) view.findViewById(R.id.button_date_to);

        // default date range is the last 1 week
        fromCalendar.setTime(new Date());
        toCalendar.setTime(new Date());
        fromCalendar.add(java.util.Calendar.DATE, -6);

        fromDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(fromCalendar));
        toDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(toCalendar));

        fromDateButton.setOnClickListener(new View.OnClickListener(){

            DatePickerDialog.OnDateSetListener onDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month,
                                              int dayOfMonth) {
                            fromCalendar.set(java.util.Calendar.YEAR, year);
                            fromCalendar.set(java.util.Calendar.MONTH, month);
                            fromCalendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
                            mListener.onDateChanged(fromCalendar, toCalendar);
                            // Update the fromButton accordingly to display the date
                            fromDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(fromCalendar));
                        }
                    };

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), onDateSetListener, fromCalendar
                        .get(java.util.Calendar.YEAR), fromCalendar.get(java.util.Calendar.MONTH),
                        fromCalendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
            }
        });


        toDateButton.setOnClickListener(new View.OnClickListener(){

            DatePickerDialog.OnDateSetListener onDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int month, int dayOfMonth) {
                            toCalendar.set(java.util.Calendar.YEAR, year);
                            toCalendar.set(java.util.Calendar.MONTH, month);
                            toCalendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
                            mListener.onDateChanged(fromCalendar, toCalendar);
                            // Update the toButton accordingly to display the date
                            toDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(toCalendar));

                        }
                    };
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), onDateSetListener, toCalendar
                        .get(java.util.Calendar.YEAR), toCalendar.get(java.util.Calendar.MONTH),
                        toCalendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDateRangeChangeListener) {
            mListener = (OnDateRangeChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDateRangeChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Display or hide the menu
     */
    private void updateMenuDisplay(){

        Drawable enterIcon = getResources().getDrawable(
                R.drawable.ic_enter_to_app_black_36dp, null);
        Drawable exitIcon = getResources().getDrawable(
                R.drawable.ic_exit_to_app_black_36dp, null);

        menuOpened = !menuOpened;
        if (!menuOpened) {

            // Hide the menu
            menuButton.setBackground(enterIcon);
            menuButton.animate().alpha(1.0f).setDuration(ANIMATION_SPEED * 7);
            menu.animate().translationX(menu.getWidth() - menuButton.getWidth()).
                    setDuration(ANIMATION_SPEED).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    menu.setCardElevation(12.0f);
                    menu.animate().alpha(1.0f).setDuration(ANIMATION_SPEED);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(!menuOpened){
                        // Menu is hidden
                        menu.setAlpha(0.5f);
                        menu.setCardElevation(0.0f);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        else{
            // Display the menu
            menuButton.setBackground(exitIcon);
            menuButton.animate().alpha(0.7f).setDuration(ANIMATION_SPEED * 2);
            menu.animate().translationX(0).setDuration(ANIMATION_SPEED);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnDateRangeChangeListener {
        void onDateChanged(Calendar fromCalendar, Calendar toCalendar);
    }
}

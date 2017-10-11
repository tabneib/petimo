package de.tud.nhd.petimo.view.fragments.menu;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.util.Calendar;
import java.util.Date;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.model.sharedpref.PetimoSPref;
import de.tud.nhd.petimo.model.sharedpref.PetimoSettingsSPref;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;

public class PetimoStatisticsMenu extends Fragment {

    private OnDateRangeChangeListener mListener;
    private android.support.v7.widget.CardView menu;
    private ImageButton menuButton;
    private RelativeLayout buttonContainer;
    private LinearLayout menuContainer;
    Button fromDateButton;
    Button toDateButton;
    RadioButton radioTask;
    RadioButton radioCat;
    Switch switchShowSelected;

    Calendar fromCalendar = Calendar.getInstance();
    Calendar toCalendar = Calendar.getInstance();

    boolean menuOpened = true;
    private final long ANIMATION_SPEED = 300;


    public PetimoStatisticsMenu() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment PetimoStatisticsMenu.
     */
    public static PetimoStatisticsMenu newInstance(boolean opened) {
        PetimoStatisticsMenu fragment = new PetimoStatisticsMenu();
        // This hack is a work-around
        fragment.menuOpened = !opened;
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
        return inflater.inflate(R.layout.fragment_petimo_statistics_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menu = (android.support.v7.widget.CardView) view.findViewById(R.id.statistics_menu);
        buttonContainer = (RelativeLayout) view.findViewById(R.id.menu_button_container);
        menuContainer = (LinearLayout) view.findViewById(R.id.menu_content);
        menuButton = (ImageButton) view.findViewById(R.id.menu_button);
        radioCat = (RadioButton) view.findViewById(R.id.radio_cats);
        radioTask = (RadioButton) view.findViewById(R.id.radio_tasks);
        switchShowSelected = (Switch) view.findViewById(R.id.switch_only_selected);

        updateChecked();

        menuButton.getBackground().setAlpha(127);
        buttonContainer.getBackground().setAlpha(255);
        menuButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                updateMenuDisplay();
            }
        });

        // It takes sometime for the views of this fragment to receive their width
        ViewTreeObserver vto = menu.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateMenuDisplay();
                ViewTreeObserver obs = menu.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }
        });



        // DatePicker
        fromDateButton = (Button) view.findViewById(R.id.button_date_from);
        toDateButton = (Button) view.findViewById(R.id.button_date_to);

        // default date range is the last 1 week
        fromCalendar.setTime(new Date());
        toCalendar.setTime(new Date());
        fromCalendar.add(Calendar.DATE, -6);

        fromDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(fromCalendar));
        toDateButton.setText(PetimoTimeUtils.getDateStrFromCalendar(toCalendar));

        fromDateButton.setOnClickListener(new View.OnClickListener(){

            DatePickerDialog.OnDateSetListener onDateSetListener =
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month,
                                              int dayOfMonth) {
                            fromCalendar.set(Calendar.YEAR, year);
                            fromCalendar.set(Calendar.MONTH, month);
                            fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            mListener.onDateChanged(fromCalendar, toCalendar);
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
                            mListener.onDateChanged(fromCalendar, toCalendar);
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
     *
     */
    private void updateChecked(){
        switch (PetimoSettingsSPref.getInstance().getSettingsString(
                PetimoSettingsSPref.STATISTICS_GROUP_BY, PetimoSPref.Consts.GROUP_BY_TASK)) {
            case PetimoSPref.Consts.GROUP_BY_TASK:
                this.radioTask.setChecked(true);
                this.switchShowSelected.setChecked(PetimoSettingsSPref.getInstance().
                        getSettingsBoolean(PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_TASKS,
                                false));
                this.switchShowSelected.setText(getString(R.string.option_show_selected_tasks));
                break;
            case PetimoSPref.Consts.GROUP_BY_CAT:
                this.radioCat.setChecked(true);
                this.switchShowSelected.setChecked(PetimoSettingsSPref.getInstance().
                        getSettingsBoolean(PetimoSettingsSPref.STATISTICS_SHOW_SELECTED_CATS,
                                false));
                this.switchShowSelected.setText(getString(R.string.option_show_selected_tasks));
                this.switchShowSelected.setText(getString(R.string.option_show_selected_categories));
                break;
            default:
                throw new RuntimeException("Unknown grouping mode!");
        }

    }

    /**
     * Display or hide the menu.
     * This method must be called after the fragment if added by the parent activity's fragment
     * manager
     */
    public void updateMenuDisplay(){
        menuOpened = !menuOpened;
        if (!menuOpened)
            hideMenu(ANIMATION_SPEED, null);
        else
            displayMenu(ANIMATION_SPEED, null);
    }

    /**
     *
     */
    private void hideMenu(final long speed, final PostTask task){
        Drawable enterIcon = getResources().getDrawable(
                R.drawable.ic_enter_to_app_black_36dp, null);
        menuButton.setBackground(enterIcon);
        menuButton.animate().alpha(1.0f).setDuration(speed * 7);
        menu.animate().translationX(menu.getWidth() - menuButton.getWidth()).
                setDuration(speed).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                menu.setCardElevation(12.0f);
                menu.setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!menuOpened){
                    // Menu is hidden
                    menu.setAlpha(0.5f);
                    menu.setCardElevation(0.0f);
                    menuContainer.setVisibility(View.INVISIBLE);
                }
                if (task != null)
                    task.execute();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     *
     */
    private void displayMenu(final long speed, final PostTask task){

        Drawable exitIcon = getResources().getDrawable(
                R.drawable.ic_exit_to_app_black_36dp, null);
        menuButton.setBackground(exitIcon);
        menuButton.animate().alpha(0.7f).setDuration(speed * 2);
        menu.animate().translationX(0).setDuration(speed).setListener(
                new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                menuContainer.setVisibility(View.VISIBLE);
                menu.setCardElevation(12.0f);
                menu.setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!menuOpened){
                    // Menu is hidden
                    menu.setAlpha(0.5f);
                    menu.setCardElevation(0.0f);
                }
                if (task != null)
                    task.execute();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    /**
     * Call this method if there is some popup window is going to cover the menu.
     * The menu will be hidden if it's currently displayed. Its previously state is stored and can
     * be restored by calling reactivate()
     */
    public void deactivate(PostTask task){
        if (menuOpened)
            hideMenu(ANIMATION_SPEED / 2, task);
        else
            task.execute();
    }

    /**
     * Restore the displaying state of the menu
     */
    public void reactivate(){
        if (menuOpened)
            displayMenu(ANIMATION_SPEED * 2, null);
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

    /**
     * The Task to execute after the hiding/displaying animation finishes
     */
    public interface PostTask{
        public void execute();
    }
}

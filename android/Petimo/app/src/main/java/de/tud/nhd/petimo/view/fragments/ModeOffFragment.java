package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.model.PetimoDbWrapper;
import de.tud.nhd.petimo.utils.PetimoTimeUtils;
import de.tud.nhd.petimo.view.fragments.dialogs.ConfirmStartDialogFragment;
import de.tud.nhd.petimo.view.fragments.dialogs.PetimoDialog;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.MonitoredTaskListFragment;
import de.tud.nhd.petimo.view.fragments.menu.ModeOffMenuFragment;

/**
 * Activities that contain this fragment must implement the
 * {@link OnModeFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ModeOffFragment extends Fragment {

    private static ModeOffFragment _instance;
    private static final String TAG = "ModeOffFragment";
    private OnModeFragmentInteractionListener mListener;

    private ImageButton menuImageButton;
    private RelativeLayout menuContainer;
    private RelativeLayout taskListContainer;
    private LinearLayout menuHeader;
    Spinner catSpinner;
    Spinner taskSpinner;
    Button startButton;
    boolean menuOpened = false;

    /**
     * tag used to avoid undesired call of catSpinner.onSelectedItem
     */
    private int catSpinnerPosition;

    public static final String MENU_FRAGMENT_TAG = TAG + "-menu";
    public static final String TASK_LIST_FRAGMENT_TAG = TAG + "-taskList";



    public ModeOffFragment() {
        // Required empty public constructor
    }

    /**
     * Return an (unique) instance of {@link ModeOffFragment}, if not yet exists then initialize it
     * @return the ModeOffFragment instance
     */
    public static ModeOffFragment getInstance(){
        if(_instance == null){
            _instance = new ModeOffFragment();
        }
        return _instance;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Created");
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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_off_mode, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        catSpinner = (Spinner) getView().findViewById(R.id.spinnerCat);
        taskSpinner = (Spinner) getView().findViewById(R.id.spinnerTask);
        startButton = (Button) getView().findViewById(R.id.buttonStart);
        menuImageButton = (ImageButton) getView().findViewById(R.id.menu_button);
        menuHeader = (LinearLayout) getView().findViewById(R.id.menu_header);

        //Log.d(TAG, "Button color ===> " + ((ColorDrawable) startButton.getBackground()).getColor());

        taskListContainer = (RelativeLayout) getView().findViewById(R.id.monitored_tasks_container);
        menuContainer =
                (RelativeLayout) getView().findViewById(R.id.monitored_tasks_menu_container);

        //----------------- Monitor control dashboard --------------------------------------------->
        startButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                final int catId = PetimoDbWrapper.getInstance().getAllCatIds().get(
                        catSpinner.getSelectedItemPosition());
                final int taskId = PetimoDbWrapper.getInstance().getTaskIdsByCat(catId).
                        get(taskSpinner.getSelectedItemPosition());

                final ConfirmStartDialogFragment contentFragment =
                        ConfirmStartDialogFragment.newInstance(catId, taskId);

                if (catId != -1 && taskId != -1) {
                    PetimoDialog confirmStartDialog = PetimoDialog.newInstance(getActivity())
                            .setIcon(PetimoDialog.ICON_TIME_EMPTY)
                            .setTitle(getActivity().getString(R.string.title_start_monitor))
                            .setContentFragment(contentFragment)
                            .setPositiveButton(getActivity().getString(
                                    R.string.button_start_monitor),
                                    new PetimoDialog.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (contentFragment.manualTime != null){
                                                mListener.onConfirmStartButtonClicked(
                                                        catId,taskId,
                                                        PetimoTimeUtils.getTimeMillisFromHM(
                                                                contentFragment.manualTime[0],
                                                                contentFragment.manualTime[1]));
                                            }
                                            else{
                                                mListener.onConfirmStartButtonClicked(
                                                        catId, taskId,
                                                        System.currentTimeMillis());
                                            }
                                        }
                                    })
                            .setNegativeButton(getActivity().getString(R.string.button_cancel),
                                    new PetimoDialog.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // do nothing
                                        }
                                    });
                    confirmStartDialog.show(getActivity().getSupportFragmentManager(),null);
                }
                else {
                    // TODO: notify that user has to choose a cat and a task
                }
            }
        });

        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Only auto update taskSpinner if the catSpinner is selected by the user
                if (position != catSpinnerPosition) {
                    updateTaskSpinner();
                    catSpinnerPosition = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO It should be nothing selected at app startup!
            }
        });

        taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Up date the cat/task displayed on start monitor button
                // bug: If db is empty, only catSpinner is empty but not taskSpinner, so we have
                // to check for this case
                if (catSpinner.getSelectedItem() != null){
                    int catId = PetimoDbWrapper.getInstance().getAllCatIds().get(
                            catSpinner.getSelectedItemPosition());
                    int taskId = PetimoDbWrapper.getInstance().getTaskIdsByCat(catId).get(
                            taskSpinner.getSelectedItemPosition());
                    updateStartButtonText(catId, taskId);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO It should be nothing selected at app startup!
            }
        });

        //----------------- TaskList -------------------------------------------------------------->
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MonitoredTaskListFragment taskListFragment = (MonitoredTaskListFragment)
                fm.findFragmentByTag(TASK_LIST_FRAGMENT_TAG);
        if(taskListFragment != null)
            // old taskListFragment found -> replace
            ft.show(taskListFragment).commit();
        else
            // old taskListFragment not found -> add new
            ft.add(taskListContainer.getId(),
                    MonitoredTaskListFragment.newInstance(2), TASK_LIST_FRAGMENT_TAG).commit();

        //------------------ Menu ----------------------------------------------------------------->
        menuHeader.setOnClickListener(new View.OnClickListener(){

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
    }


    /**
     * Decide if the menu should be opened or closed
     * @param isClicked weather this method is called upon the menu button is clicked
     */
    private void updateMenuDisplay(boolean isClicked){
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Drawable upIcon = getResources().getDrawable(
                R.drawable.ic_arrow_drop_up_black_24dp, null);
        Drawable downIcon = getResources().getDrawable(
                R.drawable.ic_arrow_drop_down_black_24dp, null);

        ModeOffMenuFragment menuFragment = (ModeOffMenuFragment)
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
                        new ModeOffMenuFragment(), MENU_FRAGMENT_TAG).commit();
                menuOpened = true;
                menuImageButton.setBackground(upIcon);
            }
            else{
                // Navigate (back) to this view
                // Navigate to this View
                if (menuOpened) {
                    // Reconstruct old state -> open
                    ft.add(menuContainer.getId(),
                            new ModeOffMenuFragment(), MENU_FRAGMENT_TAG).commit();
                    menuImageButton.setBackground(upIcon);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initCatSpinner();
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
                // Avoid undesired call of catSpinner.onItemSelected
                catSpinnerPosition = catPos;
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
                                    PetimoDbWrapper.getInstance().getAllCatIds().get(
                                            catSpinner.getSelectedItemPosition())));
                    taskSpinner.setAdapter(taskSpinnerAdapter);
                    taskSpinnerAdapter.notifyDataSetChanged();
                    // Also display the chosen cat/task on the start monitor button
                    int catId = PetimoDbWrapper.getInstance().getAllCatIds().get(
                            catSpinner.getSelectedItemPosition());
                    int taskId = PetimoDbWrapper.getInstance().getTaskIdsByCat(catId).get(
                            taskSpinner.getSelectedItemPosition());
                    Log.d(TAG, "catSpinner.getSelectedItemPosition() ===>  " +
                            catSpinner.getSelectedItemPosition());
                    Log.d(TAG, "catIds ===> " +
                            PetimoDbWrapper.getInstance().getAllCatIds());
                    updateStartButtonText(catId, taskId);
                }
                // There is no task selected => display no cat/task on the start monitor button
                updateStartButtonText(-1, -1);
            }
        });
    }

    /**
     * Update all spinners selection according to the given chosen cat and task
     * @param catId
     */
    public void updateAllSpinner(int catId, int taskId){
        int catPos = PetimoDbWrapper.getInstance().getAllCatIds().indexOf(catId);
        catSpinner.setSelection(catPos);
        // Set the tag up-to-date in order to avoid undesired call of catSpinner.onItemSelected
        catSpinnerPosition = catPos;
        updateTaskSpinner();
        taskSpinner.setSelection(
                PetimoDbWrapper.getInstance().getTaskIdsByCat(catId).indexOf(taskId));
    }

    /**
     * Display the current chosen cat/task on the start monitor button
     * @param catId
     * @param taskId
     */
    public void updateStartButtonText(int catId, int taskId){
        if(catId != -1 && taskId != -1)
            startButton.setText(getString(R.string.button_start_monitor) + "\n\n" +
            PetimoDbWrapper.getInstance().getCatNameById(catId) + " / " +
                    PetimoDbWrapper.getInstance().getTaskNameById(taskId));
    }
}

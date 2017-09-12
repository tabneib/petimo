package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.controller.PetimoController;
import de.tud.nhd.petimo.view.fragments.dialogs.ConfirmStartDialogFragment;
import de.tud.nhd.petimo.view.fragments.listener.OnModeFragmentInteractionListener;
import de.tud.nhd.petimo.view.fragments.lists.MonitoredTaskListFragment;

/**
 * Activities that contain this fragment must implement the
 * {@link OnModeFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ModeOffFragment extends Fragment {

    private static ModeOffFragment _instance;
    private static final String TAG = "ModeOffFragment";
    private OnModeFragmentInteractionListener mListener;

    private Button menuButton;
    private RelativeLayout menuContainer;
    private RelativeLayout taskListContainer;
    Spinner catSpinner;
    Spinner taskSpinner;
    Button startButton;
    RadioButton radioButtonTime;
    RadioButton radioButtonFreq;
    boolean menuOpened = false;

    /**
     * tag used to avoid undesired call of catSpinner.onSelectedItem
     */
    private int catSpinnerPosition;

    private final String MENU_FRAGMENT_TAG = TAG + "-menu";
    private final String TASK_LIST_FRAGMENT_TAG = TAG + "-taskList";



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
        menuButton = (Button) getView().findViewById(R.id.menu_button);
        radioButtonFreq = (RadioButton) getView().findViewById(R.id.radioButtonFreq);
        radioButtonTime = (RadioButton) getView().findViewById(R.id.radioButtonTime);
        taskListContainer = (RelativeLayout) getView().findViewById(R.id.monitored_tasks_container);
        menuContainer =
                (RelativeLayout) getView().findViewById(R.id.monitored_tasks_menu_container);

        //----------------- Monitor control dashboard --------------------------------------------->
        startButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                DialogFragment dialogFragment = new ConfirmStartDialogFragment();
                Bundle args = new Bundle();
                if (catSpinner.getSelectedItem() != null)
                    args.putString(ConfirmStartDialogFragment.CATEGORY,
                            catSpinner.getSelectedItem().toString());
                if (taskSpinner.getSelectedItem() != null)
                    args.putString(ConfirmStartDialogFragment.TASK,
                            taskSpinner.getSelectedItem().toString());
                dialogFragment.setArguments(args);
                dialogFragment.show(getFragmentManager(), null);
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
                // TODO implement or remove
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
        menuButton.setOnClickListener(new View.OnClickListener(){

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
            }
            // menu is currently closed
            else {
                // Closed, User clicked => open
                ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);
                ft.show(menuFragment).commit();
                menuOpened = true;
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
            }
            else{
                // Navigate (back) to this view
                // Navigate to this View
                if (menuOpened) {
                    // Reconstruct old state -> open
                    ft.add(menuContainer.getId(),
                            new ModeOffMenuFragment(), MENU_FRAGMENT_TAG).commit();
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
                        PetimoController.getInstance().getAllCatNames());
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
                ArrayAdapter<String> taskSpinnerAdapter = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        PetimoController.getInstance().getTaskNameByCat(
                                catSpinner.getSelectedItem().toString()));
                taskSpinner.setAdapter(taskSpinnerAdapter);
                taskSpinnerAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Update all spinners selection according to the given chosen cat and task
     * @param category
     */
    public void updateAllSpinner(String category, String task){
        int catPos = PetimoController.getInstance().getAllCatNames().indexOf(category);
        catSpinner.setSelection(catPos);
        // Set the tag up-to-date in order to avoid undesired call of catSpinner.onItemSelected
        catSpinnerPosition = catPos;
        updateTaskSpinner();
        taskSpinner.setSelection(
                PetimoController.getInstance().getTaskNameByCat(category).indexOf(task));
    }
}

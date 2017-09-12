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
    boolean menuOpened = false;

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
        taskListContainer = (RelativeLayout) getView().findViewById(R.id.monitored_tasks_container);
        menuContainer =
                (RelativeLayout) getView().findViewById(R.id.monitored_tasks_menu_container);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        //----------------- TaskList -------------------------------------------------------------->
        MonitoredTaskListFragment taskListFragment = (MonitoredTaskListFragment)
                fm.findFragmentByTag(TASK_LIST_FRAGMENT_TAG);
        if(taskListFragment != null){
            Log.d(TAG, "old taskListFragment found -> replace");
            ft.detach(taskListFragment).attach(taskListFragment).commit();        }
        else{
            Log.d(TAG, "old taskListFragment not found -> add new");
            taskListFragment = MonitoredTaskListFragment.newInstance(2);
            ft.add(taskListContainer.getId(), taskListFragment, TASK_LIST_FRAGMENT_TAG).commit();
        }

        //------------------ Menu ----------------------------------------------------------------->
        menuButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                updateMenuDisplay(true);
            }
        });

        // update menu display anyway
        //updateMenuDisplay(false);


    }

    /**
     * Decide if the menu should be opened or closed
     * @param isClicked weather this method is called upon the menu button is clicked
     */
    private void updateMenuDisplay(boolean isClicked){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_top);

        ModeOffMenuFragment menuFragment = (ModeOffMenuFragment)
                fm.findFragmentByTag(MENU_FRAGMENT_TAG);
        if(menuFragment != null){
            Log.d(TAG, "old menuFragment found");
            // menu is currently opened
            if (menuOpened) {
                if (isClicked) {
                    Log.d(TAG, "menuOpened: " + menuOpened + " / isClicked: " + isClicked);
                    ft.hide(menuFragment).commit();
                    menuOpened = false;
                }
                else {
                    // update the menu display according to the last state
                    Log.d(TAG, "menuOpened: " + menuOpened + " / isClicked: " + isClicked);
                    ft.show(menuFragment).commit();
                }
            }
            // menu is currently closed
            else {
                if(isClicked) {
                    Log.d(TAG, "menuOpened: " + menuOpened + " / isClicked: " + isClicked);
                    ft.show(menuFragment).commit();
                    menuOpened = true;
                }
            }
        }
        else{
            // init
            if(isClicked) {
                Log.d(TAG, "menuOpened: " + menuOpened + " / isClicked: " + isClicked);
                menuFragment = new ModeOffMenuFragment();
                ft.add(menuContainer.getId(), menuFragment, MENU_FRAGMENT_TAG).commit();
                menuOpened = true;
            }
            else
                Log.d(TAG, "menuOpened: " + menuOpened + " / isClicked: " + isClicked);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        new WaitForDb().execute((Void) null);
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
                updateTaskSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO implement or remove
            }
        });

    }

    /**
     * Initialize the contents of the category spinner with data from the database
     */
    private void initCatSpinner(){
        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                ArrayAdapter<String> catSpinnerAdapter = new ArrayAdapter<String>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        PetimoController.getInstance().getAllCatNames());

                catSpinner.setAdapter(catSpinnerAdapter);
                catSpinnerAdapter.notifyDataSetChanged();

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
     * Busy loop until the db wrapper is ready. This is only done when the app is starting up
     * in Off Mode
     */
    private class WaitForDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (!PetimoController.getInstance().isDbReady()){
                try{
                    Thread.sleep(20);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            initCatSpinner();
            return null;
        }
    }
}

package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.view.activities.EditBlockActivity;
import de.tud.nhd.petimo.view.activities.EditTasksActivity;
import de.tud.nhd.petimo.view.activities.MainActivity;
import de.tud.nhd.petimo.view.activities.SettingsActivity;
import de.tud.nhd.petimo.view.activities.StatisticsActivity;

public class DrawerFragment extends Fragment {

    public static final String TAG = "DRAWER_FRAGMENT";

    private static final String ARG_ACTIVITY = "ARG_ACTIVITY";

    private String parentActivityTag;
    private OnFragmentInteractionListener mListener;

    View currentContainer;
    LinearLayout itemMonitor;
    LinearLayout itemMonitoredTasks;
    LinearLayout itemStatistics;
    LinearLayout itemManageTasks;
    LinearLayout itemSettings;


    public DrawerFragment() {
    }

    /**
     *
     * @param parentActivityTag Parameter 1.
     * @return A new instance of fragment DrawerFragment.
     */
    public static DrawerFragment newInstance(String parentActivityTag) {
        DrawerFragment fragment = new DrawerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTIVITY, parentActivityTag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            parentActivityTag = getArguments().getString(ARG_ACTIVITY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemMonitor = (LinearLayout) view.findViewById(R.id.item_monitor);
        itemMonitoredTasks = (LinearLayout) view.findViewById(R.id.item_monitored_tasks);
        itemStatistics = (LinearLayout) view.findViewById(R.id.item_statistics);
        itemManageTasks = (LinearLayout) view.findViewById(R.id.item_manage_tasks);
        itemSettings = (LinearLayout) view.findViewById(R.id.item_settings);

        itemMonitor.setOnClickListener(
                genOnclickListener(MainActivity.TAG, MainActivity.class));
        itemMonitoredTasks.setOnClickListener(
                genOnclickListener(EditBlockActivity.TAG, EditBlockActivity.class));
        itemStatistics.setOnClickListener(genOnclickListener(
                StatisticsActivity.TAG, StatisticsActivity.class));
        itemManageTasks.setOnClickListener(
                genOnclickListener(EditTasksActivity.TAG, EditTasksActivity.class));
        itemSettings.setOnClickListener(
                genOnclickListener(SettingsActivity.TAG, SettingsActivity.class));

    }

    @Override
    public void onResume() {
        super.onResume();
        initBackground();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof OnFragmentInteractionListener))
            throw new IllegalStateException(
                    "Parent activity must implement OnFragmentInteractionListener!");
        else this.mListener = (OnFragmentInteractionListener) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initBackground(){
        setContainerBackground(itemMonitor, MainActivity.TAG);
        setContainerBackground(itemMonitoredTasks, EditBlockActivity.TAG);
        setContainerBackground(itemStatistics, StatisticsActivity.TAG);
        setContainerBackground(itemManageTasks, EditTasksActivity.TAG);
        setContainerBackground(itemSettings, SettingsActivity.TAG);

    }

    private View.OnClickListener genOnclickListener(
            final String activityTag, final Class activityClass) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!activityTag.equals(parentActivityTag)) {
                    currentContainer.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                    v.setBackgroundColor(
                            getActivity().getResources().getColor(R.color.colorPrimary));
                    mListener.onItemClick();
                    Intent intent = new Intent(getActivity(), activityClass);
                    getActivity().startActivity(intent);
                }
            }
        };
    }

    private void setContainerBackground(View view, final String activityTag){
        if (!activityTag.equals(parentActivityTag))
            view.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        else {
            currentContainer = view;
            view.setBackgroundColor(
                    getActivity().getResources().getColor(R.color.colorPrimary));
        }
    }

    public interface OnFragmentInteractionListener{
        public void onItemClick();
    }
}

package de.tud.nhd.petimo.view.fragments.listener;

/**
 * Created by nhd on 03.09.17.
 */

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 */
public interface OnModeFragmentInteractionListener {

    /**
     * Call-back method which is called when the start monitor button is clicked
     * @param inputCat
     * @param inputTask
     */
    void onConfirmStartButtonClicked(String inputCat, String inputTask, long startTime);

    /**
     * Call-back method which is called when the stop confirm button is clicked
     */
    void onConfirmStopButtonClicked(long stopTime);


    void onLastMonitoredTaskSelected(String category, String task);
}

package de.tud.nhd.petimo.view.fragments;

/**
 * Created by nhd on 03.09.17.
 */

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 */
public interface OnFragmentInteractionListener {

    /**
     * Call-back method which is called when the start monitor button is clicked
     * @param inputCat
     * @param inputTask
     */
    void onStartButtonClicked(String inputCat, String inputTask);

    /**
     * Call-back method which is called when the stop monitor button is clicked
     * @param inputCat
     * @param inputTask
     */
    void onStopButtonClicked();
}

package de.tud.nhd.petimo.view.fragments.listener;

/**
 * Created by nhd on 03.09.17.
 */

import de.tud.nhd.petimo.view.fragments.lists.MonitorCategoryListFragment;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 */
public interface OnEditTaskFragmentInteractionListener {

    /**
     * Call-back method which is called when the user confirm adding a new category
     * @param inputCat name of the category to be added
     */
    void onConfirmAddingCatButtonClicked(
            MonitorCategoryListFragment catListFragment, String inputCat, int priority);

    /**
     * Call-back method which is called when the stop confirm button is clicked
     */
    void onConfirmAddingTaskStopButtonClicked();
}

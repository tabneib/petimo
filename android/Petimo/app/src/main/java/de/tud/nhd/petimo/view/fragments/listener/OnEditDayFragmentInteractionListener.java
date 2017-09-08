package de.tud.nhd.petimo.view.fragments.listener;

import de.tud.nhd.petimo.model.MonitorBlock;
import de.tud.nhd.petimo.model.MonitorDay;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface OnEditDayFragmentInteractionListener {
    // TODO: Update argument type and name
    void onRemovingMonitorBlock(MonitorBlock item);
}

package de.tud.nhd.petimo.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tud.nhd.petimo.R;
import de.tud.nhd.petimo.view.fragments.listener.OnEditBlocksMenuFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEditBlocksMenuFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EditBlocksMenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";

    private OnEditBlocksMenuFragmentInteractionListener mListener;
    //public RelativeLayout container;
    //public RelativeLayout parentContainer;

    public EditBlocksMenuFragment() {
        // Required empty public constructor
    }

    public static EditBlocksMenuFragment newInstance(){
        EditBlocksMenuFragment fragment = new EditBlocksMenuFragment();
        //fragment.container = container;
        //fragment.parentContainer = parentContainer;
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_blocks_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // update the topMargin of the container of this fragment
        /*

        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateContainerTopMargin();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    vto.removeOnGlobalLayoutListener(this);
                else
                    vto.removeOnGlobalLayoutListener(this);
            }
        });

        */
    }

    /*private void updateContainerTopMargin(){
        Log.d(TAG, "My height is  =====> " + getView().getMeasuredHeight());
        RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams)
                container.getLayoutParams();
        containerParams.topMargin = -1 *getView().getMeasuredHeight();
        container.setLayoutParams(containerParams);
        //getView().bringToFront();
        parentContainer.bringChildToFront(getView());

    }
    */


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnEditBlocksMenuFragmentInteractionListener) {
            mListener = (OnEditBlocksMenuFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEditBlocksMenuFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}

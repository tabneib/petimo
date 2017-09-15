package de.tud.nhd.petimo.view.fragments.dialogs;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.tud.nhd.petimo.R;

public class PetimoDialog extends DialogFragment {

    private static final String TAG = "PetimoDialog";
    public static final String CONTENT_FRAGMENT_TAG = TAG + "-ContentFragment";
    private String title;
    //private String subtitle;
    private String message;
    private int contentLayoutId;
    private String positiveButton;
    private String negativeButton;
    private PetimoDialog.OnClickListener posListener;
    private PetimoDialog.OnClickListener negListener;
    private int iconDrawableId;

    private ImageButton dialogIcon;
    private TextView textViewTitle;
    //private TextView textViewSubtitle;
    private TextView textViewMessage;
    private LinearLayout contentContainer;
    private Button buttonPositive;
    private Button buttonNegative;



    public PetimoDialog() {
        // Required empty public constructor
    }

    public static PetimoDialog newInstance(){
        return new PetimoDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Switch between dialog with/without icon
        if (iconDrawableId == 0)
            return inflater.inflate(R.layout.fragment_petimo_dialog, container, false);
        else
            return inflater.inflate(R.layout.fragment_petimo_dialog_icon, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        textViewTitle = (TextView) getView().findViewById(R.id.dialog_title);
        //textViewSubtitle = (TextView) getView().findViewById(R.id.dialog_subtitle);
        textViewMessage = (TextView) getView().findViewById(R.id.content_message);
        contentContainer = (LinearLayout) getView().findViewById(R.id.content_container);
        buttonPositive = (Button) getView().findViewById(R.id.button_positive);
        buttonNegative = (Button) getView().findViewById(R.id.button_negative);

        if (iconDrawableId != 0){
            dialogIcon = (ImageButton) getView().findViewById(R.id.dialog_icon);
            dialogIcon.setBackground(getResources().getDrawable(iconDrawableId, null));
        }

        textViewTitle.setText(title);

        // customized content layout has higher priority than a simple content message
        if (contentLayoutId != 0)
            LayoutInflater.from(getActivity()).inflate(contentLayoutId, contentContainer);
        else
            textViewMessage.setText(message);

        // Dialog buttons
        buttonPositive.setText(positiveButton);
        buttonNegative.setText(negativeButton);
        buttonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posListener.onClick(getView());
                dismiss();
            }
        });
        buttonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                negListener.onClick(getView());
                dismiss();
            }
        });
    }

    public PetimoDialog setTitle(String title) {
        this.title = title;
        /*if (textViewTitle != null)
            textViewTitle.setText(title);*/
        return this;
    }

    /*public PetimoDialog setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }*/

    public PetimoDialog setMessage(String message) {
        this.message = message;
        /*if (textViewMessage != null)
            textViewMessage.setText(message);*/
        return this;
    }

    public PetimoDialog setContentLayout(int contentLayoutId) {
        this.contentLayoutId = contentLayoutId;
        dismiss();
        return this;
    }

    public PetimoDialog setPositiveButton(String positiveButton,
                                          final PetimoDialog.OnClickListener posListener) {
        this.positiveButton = positiveButton;
        /*if (buttonPositive != null)
            buttonPositive.setText(positiveButton);*/
        this.posListener = posListener;
        return this;
    }

    public PetimoDialog setNegativeButton(String negativeButton,
                                          final PetimoDialog.OnClickListener negListener) {
        this.negativeButton = negativeButton;
        /*if (buttonNegative != null)
            buttonNegative.setText(negativeButton);*/
        this.negListener = negListener;
        return this;
    }

    public PetimoDialog setIcon(int id){
        this.iconDrawableId = id;
        return this;
    }


    /*public void show(){
        show(getActivity().getSupportFragmentManager(), TAG);
    }*/

    /**
     *
     */
    public static abstract class OnClickListener{
        public abstract void onClick(View view);
    }
}

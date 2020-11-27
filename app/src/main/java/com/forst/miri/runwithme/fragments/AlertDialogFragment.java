package com.forst.miri.runwithme.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.forst.miri.runwithme.R;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertDialogFragment extends DialogFragment {

    private static final String LISTENER = "listener_";
    private static final String TITLE = "tit_le_";
    private static final String MESSAGE = "me_ssage_";

    public interface YesNoListener extends Serializable{
        void onYes();

        void onNo();
    }

    private YesNoListener listener = null;
    String message, title;

    //private Context context = null;

    public AlertDialogFragment() {
    }

    public static AlertDialogFragment getAndSetArguments(/*Context context, */String message, String title, YesNoListener listener) {
        //this.context = context;
//        this.listener = listener;
//        this.message = message;
//        this.title = title;
        Bundle b = new Bundle();
        b.putSerializable(LISTENER, listener);
        b.putString(TITLE, title);
        b.putString(MESSAGE, message);
        AlertDialogFragment f = new AlertDialogFragment();
        f.setArguments(b);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.alert_dialog, container, false);
        String message = null;
        String title = null;
        Bundle b = getArguments();
        if( b != null){
            listener = (YesNoListener) b.getSerializable(LISTENER);
            title = b.getString(TITLE);
            message = b.getString(MESSAGE);
        }
        TextView tvTitle = (TextView) view.findViewById(R.id.alert_dialog_tv_title);
        if (title == null) {
            tvTitle.setVisibility(View.GONE);
        } else{
            tvTitle.setText(title);
        }

        TextView tvMessage = (TextView) view.findViewById(R.id.alert_dialog_tv_message);
        tvMessage.setText(message);
        Button bOk = (Button) view.findViewById(R.id.alert_dialog_button_ok);
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.onYes();
            }
        });
        return view;
    }

}

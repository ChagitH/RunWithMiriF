package com.forst.miri.runwithme.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.R;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;
import com.forst.miri.runwithme.miscellaneous.Dialogs;
import com.forst.miri.runwithme.miscellaneous.NetworkCenterHelper;
import com.forst.miri.runwithme.objects.ConnectedUser;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingPlanRegistrationFragment extends ParentFragment {


    public static final String FRAGMENT_TAG = TrainingPlanRegistrationFragment.class.getSimpleName();

    private Dialog progDailog;
    private boolean hasStoragePermission = false;
    private WebView webView;
    private Dialog spinningDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_training_plan_registration, container, false);

        TextView tv = (TextView) rootview.findViewById(R.id.practice_registration_explanation_textview);
        webView = (WebView) rootview.findViewById(R.id.practice_registration_just_do_it_webview);
//        webView.getSettings().setBuiltInZoomControls(true);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("http://www.miriforst.com");

        progDailog = Dialogs.showSpinningWheelDialog(getActivity());//ProgressDialog.show(getActivity(), "Loading","Please wait...", true);
        progDailog.setCancelable(false);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();

                if(spinningDialog != null && spinningDialog.isShowing()) spinningDialog.dismiss();
            }
        });

        fetchAndLoadUrl(1); //#1 = plan from 1-10



//        Button button = (Button) rootview.findViewById(R.id.practice_registration_just_do_it_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                purchaseDone();
//            }
//        });
        return rootview;
    }

    private void fetchAndLoadUrl(int program_id){
        spinningDialog = Dialogs.showSpinningWheelDialog(getActivity());
        spinningDialog.show();
        if(ConnectedUser.getInstance() != null) {
            NetworkCenterHelper.getPaymentUrl(getActivity(), ConnectedUser.getInstance(), program_id, new PostResponseCallback() {
                @Override
                public void requestStarted() {
                    Log.d(getClass().getName(), "fetchAndLoadUrl().requestStarted()");
                }

                @Override
                public void requestCompleted(String response) {
                    Log.d(getClass().getName(), "fetchAndLoadUrl().requestCompleted() " + response);
                    if(response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject != null ){
                                String url = jsonObject.getString("url");
                                if(url != null){
                                    webView.loadUrl(url);
                                }
                            }
                        } catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void requestEndedWithError(VolleyError error) {
                    Log.d(getClass().getName(), "fetchAndLoadUrl().requestEndedWithError()" + error);
                    if (error != null) error.printStackTrace();
                    if(spinningDialog != null && spinningDialog.isShowing()) spinningDialog.dismiss();
                }
            }) ;
        }
    }

    @Override
    public int getResourceID() {
        return R.layout.fragment_training_plan_registration;
    }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    public TrainingPlanRegistrationFragment() {}


    @Override
    public void storagePermissionGranted() {
        this.hasStoragePermission = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(spinningDialog != null && spinningDialog.isShowing()) spinningDialog.dismiss();
    }
}

package com.forst.miri.runwithme.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forst.miri.runwithme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgreementFragment extends ParentFragment {

//    private Dialog progDailog;
//    private WebView webview;

    public static final String FRAGMENT_TAG = AgreementFragment.class.getSimpleName();
    public AgreementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_agreement, container, false);
//        webview = (WebView)rootView.findViewById(R.id.agreement_webview);
////        File pdfFile = new File("assets/agreement.pdf");
////        Uri path = Uri.fromFile(pdfFile);
//
//        progDailog = Dialogs.showSpinningWheelDialog(getActivity());//ProgressDialog.show(getActivity(), "Loading","Please wait...", true);
//        progDailog.setCancelable(false);
//
////        webview.getSettings().setLoadWithOverviewMode(true);
////        webview.getSettings().setUseWideViewPort(true);
//        webview.setWebViewClient(new WebViewClient(){
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                progDailog.show();
//                view.loadUrl(url);
//
//                return true;
//            }
//            @Override
//            public void onPageFinished(WebView view, final String url) {
//                progDailog.dismiss();
////
////                if(spinningDialog != null && spinningDialog.isShowing()) spinningDialog.dismiss();
//            }
//
//        });


        return rootView;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        File pdfFile = new File("assets/agreement.pdf");
//
////        Uri path = Uri.fromFile(pdfFile);
//        webview.loadUrl(pdfFile.toString());
//    }

    @Override
    public void storagePermissionGranted() { }

    @Override
    public String getFragmentName() {
        return this.FRAGMENT_TAG;
    }

    @Override
    public int getResourceID() {
        return R.layout.fragment_agreement;
    }

}

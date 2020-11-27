package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.forst.miri.runwithme.interfaces.GetResponseCallback;
import com.forst.miri.runwithme.interfaces.PostResponseCallback;

import java.util.Map;

/**
 * Created by chagithazani on 9/6/17.
 */

public class NetworkCenter {

    private static Post mPostInstance;
    public static Post postInstance(){
        if(mPostInstance == null) mPostInstance = new Post();
        return mPostInstance;
    }

    public static class Post {

        private Post() {

        }

        public void requestPost(Context context, String url, final PostResponseCallback callback, final Map<String, String> params, final Map<String, String> headers){

            if (callback != null) callback.requestStarted();

            StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (callback != null) callback.requestCompleted(response);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (callback != null) callback.requestEndedWithError(error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    if(params != null) return params;
                    return super.getParams();
                }


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if(headers != null) return headers;
                    return super.getHeaders();
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).addToRequestQueue(request);



        }


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static Get mGetInstance;
    public static Get getInstance(){
        if(mGetInstance == null) mGetInstance = new Get();
        return mGetInstance;
    }

    public static class Get {
        private Get(){

        }

        public void requestGet(Context context, String urlRequest, final Map<String, String> params, final Map<String, String> headers , final GetResponseCallback callback){
            // Request a string response from the provided URL.
            if(callback != null) callback.requestStarted();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlRequest, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(callback != null) callback.requestCompleted(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(callback != null) callback.requestEndedWithError(error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    if(params != null) return params;
                    return super.getParams();
                }


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if(headers != null) return headers;
                    return super.getHeaders();
                }
            };

            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static Put mPutInstance;
    public static Put putInstance(){
        if(mPutInstance == null) mPutInstance = new Put();
        return mPutInstance;
    }

    public static class Put {

        private Put() {

        }

        public void requestPut(Context context, String url, final PostResponseCallback callback, final Map<String, String> params, final Map<String, String> headers){

            if (callback != null) callback.requestStarted();

            StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (callback != null) callback.requestCompleted(response);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (callback != null) callback.requestEndedWithError(error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    if(params != null) return params;
                    return super.getParams();
                }


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if(headers != null) return headers;
                    return super.getHeaders();
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(context).addToRequestQueue(request);



        }


    }

}


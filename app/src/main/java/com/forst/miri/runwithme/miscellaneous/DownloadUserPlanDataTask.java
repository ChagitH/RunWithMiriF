package com.forst.miri.runwithme.miscellaneous;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.VolleyError;
import com.forst.miri.runwithme.exceptions.UserException;
import com.forst.miri.runwithme.interfaces.GetResponseCallback;
import com.forst.miri.runwithme.objects.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chagithazani on 12/25/17.
 */

public class DownloadUserPlanDataTask extends AsyncTask {

    private Context mContext;
    private User mUser;
    private GetUserCallback mCallback;

    public DownloadUserPlanDataTask(Context context, GetUserCallback callback) {
        this.mContext = context;
        this.mUser = User.createUserFromSharedPreferences(context, false);
        this.mCallback = callback;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if(mUser == null || mContext == null) {
            if(mCallback != null) mCallback.userPlanDataFetchFailed("mUser == null || mContext == null");
            return null;
        }

        NetworkCenterHelper.getConnectedUser(mContext, mUser, new GetResponseCallback() {
            @Override
            public void requestStarted() {

            }

            @Override
            public void requestCompleted(String response) {
                try {
                    Log.d(getClass().getName(), "^^^^^^^^^^^^^^^^^^^^^^^^^^^ DownloadUserPlanDataTask requestCompleted() - before user updated^^^^^^^^^^^^^^^^^^^^^^^^^^^");

                    JSONObject jsonUser = new JSONObject(response);
                    final User user = new User(jsonUser, mContext);
                    User.savePracticePlanDataLocally(mContext, user);
                    Log.d(getClass().getName(), "^^^^^^^^^^^^^^^^^^^^^^^^^^^ DownloadUserPlanDataTask requestCompleted() - AFTER user updated^^^^^^^^^^^^^^^^^^^^^^^^^^^ " + user != null ? user.getFirstName() : "NULL");

                    if(mCallback != null) mCallback.userPlanDataUpdated(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                    if(mCallback != null) mCallback.userPlanDataFetchFailed(e.getMessage());
                } catch (UserException e) {
                    e.printStackTrace();
                    if(mCallback != null) mCallback.userPlanDataFetchFailed(e.getMessage());
                }
            }

            @Override
            public void requestEndedWithError(VolleyError error) {
                String message = "";
                if(error != null) {
                    error.printStackTrace();
                    message = error.getMessage();
                }
                if(mCallback != null) mCallback.userPlanDataFetchFailed(message);
            }
        });


        return null;
    }

    public interface GetUserCallback {
        void userPlanDataUpdated(User user);
        void userPlanDataFetchFailed(String error);
    }
}

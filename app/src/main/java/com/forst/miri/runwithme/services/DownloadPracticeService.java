package com.forst.miri.runwithme.services;

import android.os.AsyncTask;

import com.forst.miri.runwithme.miscellaneous.DownloadPracticeTask;
import com.forst.miri.runwithme.objects.Practice;
import com.forst.miri.runwithme.objects.User;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

public class DownloadPracticeService extends GcmTaskService {
    private DownloadPracticeTask practiceDownloadTask;

    public DownloadPracticeService() {
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        practiceDownloadTask = new DownloadPracticeTask(this, null);

        User user = User.createUserFromSharedPreferences(this, true);
        if(user != null) {
            final int nextPracticeNum = user.getPracticeNum();
            final int planId = user.getProgramId();
            if (user.IsRegisteredToRunWithMiri() && Practice.isNextLessonNum(this, nextPracticeNum) == false) {
                practiceDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR , Integer.valueOf(nextPracticeNum), Integer.valueOf(planId));
            } else if(user.isInTrial() && Practice.isNextLessonNum(this, 1) == false){
                practiceDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Integer.valueOf(Practice.TRIAL_PRACTICE_NUM), Integer.valueOf(Practice.TRIAL_PROGRAM_ID));
            }else {
                return GcmNetworkManager.RESULT_SUCCESS;
            }
        } else {
            return GcmNetworkManager.RESULT_FAILURE;
        }

        return GcmNetworkManager.RESULT_RESCHEDULE;
    }

}

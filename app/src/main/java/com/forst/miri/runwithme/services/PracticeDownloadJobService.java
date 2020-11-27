package com.forst.miri.runwithme.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.forst.miri.runwithme.miscellaneous.DownloadPracticeTask;
import com.forst.miri.runwithme.objects.Practice;
import com.forst.miri.runwithme.objects.User;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PracticeDownloadJobService extends JobService {
    private DownloadPracticeTask practiceDownloadTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        practiceDownloadTask = new DownloadPracticeTask(this, null){
            @Override
            protected void onPostExecute(String result) {
                Log.d (PracticeDownloadJobService.class.getName(), "------------------------------------- onPostExecute(String " + result + " )"                                          );
                if(result != null) {
                    boolean success = result.matches(DownloadPracticeTask.PRACTICE_DOWNLOADED_SUCCESSFULLY_STR) ? true : false;
                    jobFinished(jobParameters, !success);
                } else {
                    jobFinished(jobParameters, true);
                }
            }
        };

        User user = User.createUserFromSharedPreferences(this, true);
        if(user != null) {
            final int nextPracticeNum = user.getPracticeNum();
            final int planId = user.getProgramId();
            if (user.IsRegisteredToRunWithMiri() && Practice.isNextLessonNum(this, nextPracticeNum) == false) {
                practiceDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Integer.valueOf(nextPracticeNum), Integer.valueOf(planId));
            } else if(user.isInTrial() && Practice.isNextLessonNum(this, 1) == false){
                practiceDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Integer.valueOf(Practice.TRIAL_PRACTICE_NUM), Integer.valueOf(Practice.TRIAL_PROGRAM_ID));
            }else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return false;
    }
}

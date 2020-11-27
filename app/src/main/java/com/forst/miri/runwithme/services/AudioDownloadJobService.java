package com.forst.miri.runwithme.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.forst.miri.runwithme.miscellaneous.DownloadAudioTask;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AudioDownloadJobService extends JobService {
    private DownloadAudioTask audioDownloadTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        audioDownloadTask = new DownloadAudioTask(this, null){
            @Override
            protected void onPostExecute(String result) {
                Log.d (AudioDownloadJobService.class.getName(), "------------------------------------- onPostExecute(String " + result + " )"                                          );
                if(result != null) {
                    boolean success = result.matches(DownloadAudioTask.AUDIO_DOWNLOADED_SUCCESSFULLY_STR) ? true : false;
                    jobFinished(jobParameters, !success);
                } else {
                    jobFinished(jobParameters, true);
                }
            }
        };
        audioDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return false;
    }
}

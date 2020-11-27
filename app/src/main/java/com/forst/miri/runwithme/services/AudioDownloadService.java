package com.forst.miri.runwithme.services;

import android.os.AsyncTask;

import com.forst.miri.runwithme.miscellaneous.DownloadAudioTask;
import com.forst.miri.runwithme.objects.GeneralAudio;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

public class AudioDownloadService extends GcmTaskService {
    private DownloadAudioTask audioDownloadTask;

    public AudioDownloadService() {
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        audioDownloadTask = new DownloadAudioTask(this, null);
        if(!GeneralAudio.doesExist(this)){
            audioDownloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return GcmNetworkManager.RESULT_SUCCESS;
        }
        return GcmNetworkManager.RESULT_RESCHEDULE;
    }

}

package com.forst.miri.runwithme.interfaces;

/**
 * Created by chagithazani on 8/3/17.
 */

public interface PostCallback<String> {
    /**
     * Indicates that the post operation has finished. This method is called even if the
     * download hasn't completed successfully.
     */
    void postEnded(String result);
}

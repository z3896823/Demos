package org.zyb.servicetest.utils;

/**
 * Created by Administrator on 2017/2/20.
 */

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}

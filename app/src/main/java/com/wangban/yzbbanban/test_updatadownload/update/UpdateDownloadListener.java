package com.wangban.yzbbanban.test_updatadownload.update;

/**
 * Created by YZBbanban on 16/8/7.
 */

public interface UpdateDownloadListener {
    /**
     * 下载请求开始回调
     */
    void onStarted();

    /**
     * 进度更新回调
     *
     * @param progress
     * @param downloadUrl
     */
    void onProgressChanged(int progress, String downloadUrl);

    /**
     * 下载完成回调
     *
     * @param completeSize
     * @param downloadUrl
     */
    void onFinished(int completeSize, String downloadUrl);

    /**
     * 下载失败
     */
    void onFailure();

}

package com.wangban.yzbbanban.test_updatadownload.update;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by YZBbanban on 16/8/7.
 * 下载调度管理器,调用我们的UpdateDownloadRequest
 */

public class UpdateManager {
    private static UpdateManager manager;
    private ThreadPoolExecutor threadPoolExecutor;
    private UpdateDownloadRequest request;

    private UpdateManager() {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    }

    static {
        manager = new UpdateManager();
    }

    public static UpdateManager getInstance() {
        return manager;
    }

    public void startDownload(String downloadUrl, String localPath, UpdateDownloadListener listener) {
        if (request != null) {
            return;
        }
        checkLocalFilePath(localPath);
        request = new UpdateDownloadRequest(downloadUrl, localPath, listener);
        Future<?> future = threadPoolExecutor.submit(request);

    }

    private void checkLocalFilePath(String path) {
        File dir = new File(path.substring(0, path.lastIndexOf("/")) + 1);
        if (dir.exists()) {
            dir.mkdir();
        }
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

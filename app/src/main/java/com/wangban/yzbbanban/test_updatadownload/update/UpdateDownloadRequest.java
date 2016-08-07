package com.wangban.yzbbanban.test_updatadownload.update;

import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * Created by YZBbanban on 16/8/7.
 * 真正负责处理文件的下载以及线程间的通信
 */
public class UpdateDownloadRequest implements Runnable{
    public UpdateDownloadRequest(String downloadUrl, String localPath, UpdateDownloadListener listener) {

    }
    //真正去建立连接的方法
    private void makeRequst() throws IOException,InterruptedIOException{


    }
    @Override
    public void run() {

    }


}

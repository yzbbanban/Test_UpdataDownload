package com.wangban.yzbbanban.test_updatadownload.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.lang.*;

/**
 * Created by YZBbanban on 16/8/7.
 * 真正负责处理文件的下载以及线程间的通信
 */
public class UpdateDownloadRequest implements Runnable {
    private String downloadUrl;
    private String localFilePath;
    private UpdateDownloadListener listener;
    private boolean isDownloading = false;
    private long currentLenth;
    private DownloadResponseHandler downloadHandler;

    public UpdateDownloadRequest(String downloadUrl, String localFilePath, UpdateDownloadListener listener) {
        this.downloadUrl = downloadUrl;
        this.localFilePath = localFilePath;
        this.listener = listener;


    }

    //真正去建立连接的方法
    private void makeRequst() throws IOException, InterruptedException {
        if (!Thread.currentThread().isInterrupted()) {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.connect();//阻塞当前线程
                currentLenth = connection.getContentLength();

                if (!Thread.currentThread().isInterrupted()) {
                    //真正完成下载
                    downloadHandler.sendResponseMessage(connection.getInputStream());
                }

            } catch (IOException e) {
                throw e;
            }
        }
    }

    @Override
    public void run() {
        try {
            makeRequst();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 格式化数字
     *
     * @param value
     * @return
     */
    private String getTwoPointFloatStr(float value) {
        DecimalFormat fnum = new DecimalFormat("0.00");
        return fnum.format(value);
    }

    /**
     * 下载过程中有可能会出现的所有异常
     */
    public enum FailureCode {
        UnKnownHost, Socket, SocketTimeOut, ConnectTimeOut, IO, HttpResponse, JSON, Interrupted
    }


    public class DownloadResponseHandler {
        protected static final int SUCCESS_MESSAGE = 0;
        protected static final int FAILURE_MESSAGE = 1;
        protected static final int START_MESSAGE = 2;
        protected static final int FINISH_MESSAGE = 3;
        protected static final int NETWORK_OFF = 4;
        private static final int PROGRESS_CHANGED = 5;

        private int mConpleteSize = 0;
        private int progress = 0;
        private Handler handler;

        public DownloadResponseHandler() {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    handleSelfMessage(msg);
                }
            };
        }

        protected void sendFinishMessage() {
            sendMessage(obtainMessage(FINISH_MESSAGE, null));
        }

        private void sendProgressChangedMessage(int progress) {
            sendMessage(obtainMessage(PROGRESS_CHANGED, new Object[]{progress}));
        }

        protected void sendFailureMessage(FailureCode failureCode) {
            sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{failureCode}));
        }

        private void sendMessage(Message msg) {

        }

        /**
         * 获取一个消息对象
         *
         * @param responseMessage
         * @param response
         * @return
         */
        protected Message obtainMessage(int responseMessage, Object response) {
            Message msg = null;
            if (handler != null) {
                msg = handler.obtainMessage(responseMessage, response);

            } else {
                msg = Message.obtain();
                msg.what = responseMessage;
                msg.obj = response;
            }
            return msg;
        }

        private void handleSelfMessage(Message msg) {
            Object[] response;
            switch (msg.what) {
                case FAILURE_MESSAGE:
                    response = (Object[]) msg.obj;
                    handleFailureMessage((FailureCode) response[0]);
                    break;

                case PROGRESS_CHANGED:
                    response = (Object[]) msg.obj;
                    handleProgressChangedMessage(((Integer) response[0]).intValue());
                    break;

                case FINISH_MESSAGE:
                    onFinish();
                    break;
            }

        }

        /**
         * 各种消息的处理逻辑
         */
        protected void handleProgressChangedMessage(int progress) {
            listener.onProgressChanged(progress, "");
        }

        protected void handleFailureMessage(FailureCode failureCode) {
            onFailure(failureCode);
        }

        public void onFinish() {
            listener.onFinished(mConpleteSize, "");
        }

        public void onFailure(FailureCode failCode) {
            listener.onFailure();
        }

        //文件下载方法,会发送各种消息类型事件
        void sendResponseMessage(InputStream is) {
            RandomAccessFile randomAccessFile = null;
            mConpleteSize = 0;
            try {
                byte[] buffer = new byte[1024];
                int lenth = -1;
                int limit = 0;
                randomAccessFile = new RandomAccessFile(localFilePath, "rwd");

                while ((lenth = is.read(buffer)) != -1) {
                    if (isDownloading) {
                        randomAccessFile.write(buffer, 0, lenth);
                        mConpleteSize += lenth;
                        if (mConpleteSize < currentLenth) {
                            progress = (int) Float.parseFloat(getTwoPointFloatStr(mConpleteSize / currentLenth));
                            if (limit / 30 == 0 || progress <= 100) {
                                sendProgressChangedMessage(progress);
                            }
                            limit++;
                        }
                    }

                }
                sendFinishMessage();

            } catch (Exception e) {
                sendFailureMessage(FailureCode.IO);
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {

                        is.close();

                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

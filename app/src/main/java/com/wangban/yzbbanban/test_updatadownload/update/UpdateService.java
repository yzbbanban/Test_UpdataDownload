package com.wangban.yzbbanban.test_updatadownload.update;

import android.app.*;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.wangban.yzbbanban.test_updatadownload.R;

import java.io.File;

/**
 * Created by YZBbanban on 16/8/7.
 * app 下载更新后台服务
 */

public class UpdateService extends Service {
    private String apkURL;
    private String filepath;
    private NotificationManager notificationManager;
    private Notification mNotification;
    private PendingIntent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        filepath = Environment.getExternalStorageDirectory() + "/riti/elocation.apk";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            notifyUser("下载失败", "失败原因", 0);
            stopSelf();
        }
        apkURL = intent.getStringExtra("apkURL");
        notifyUser("下载", "下载开始", 0);
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload() {
        UpdateManager.getInstance().startDownload(apkURL, filepath, new UpdateDownloadListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgressChanged(int progress, String downloadUrl) {
                notifyUser("下载", "正在下载", progress);
            }


            @Override
            public void onFinished(int completeSize, String downloadUrl) {
                notifyUser("下载", "下载完成", 100);
                stopSelf();
            }

            @Override
            public void onFailure() {
                notifyUser("下载", "下载失败", 0);
                stopSelf();
            }
        });

    }

    private void notifyUser(String result, String reason, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("riti");
        if (progress > 0 && progress < 100) {
            builder.setProgress(100, progress, false);

        } else {
            builder.setProgress(0, 0, false);
        }
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(result);
        builder.setContentIntent(progress >= 100 ? getContentIntent() : PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
        mNotification = builder.build();
        notificationManager.notify(0, mNotification);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public PendingIntent getContentIntent() {
        File apkFile = new File(filepath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()), "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}

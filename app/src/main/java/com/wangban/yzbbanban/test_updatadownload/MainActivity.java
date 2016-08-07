package com.wangban.yzbbanban.test_updatadownload;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wangban.yzbbanban.test_updatadownload.update.UpdateService;

public class MainActivity extends AppCompatActivity {
    private Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkVision();

            }
        });
        checkVision();

    }

    private void checkVision() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher).setTitle("下载").setMessage("是否更新");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MainActivity.this, UpdateService.class);
                intent.putExtra("lastVersion", "V1.0");
                String path = "http://117.169.16.25/imtt.dd.qq.com/16891/5D3BB386A4EC7A66B55300C2BADED2AF.apk?mkey=57a71db6d7a2e842&f=2120&c=0&fsname=com.tencent.karaoke_3.6.8.278_67.apk&csr=4d5s&p=.apk";
//                Log.i("supergirl", "onClick: "+path);
                intent.putExtra("apkURL", path);
                startService(intent);

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}

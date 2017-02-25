package org.zyb.notificationtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 通知的基本用法：
 * 先获得一个NotificationManager对通知的发送进行管理
 * 创建一个Notification对象，用builder模式设置各种属性（标题，内容，大图标，小图标，发送时间，点击的Intent等）
 * 然后使用manager的notify方法将Notification对象发送出去即可
 *
 * 注意：
 * 1.通知的点击使用PendingIntent来指明意图。
 * 2.为了是通知兼容低版本的系统，使用NotificationCompat来build通知
 */

public class MainActivity extends AppCompatActivity {

    private Button btn_send,btn_go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_send = (Button)findViewById(R.id.id_btn_send);
        btn_go = (Button) findViewById(R.id.id_btn_go);


        btn_send.setOnClickListener(new MyClickListener());
        btn_go.setOnClickListener(new MyClickListener());
    }

    class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.id_btn_send:
                    //先创建一个NotificationManager，再创建一个Notification对象，然后用manager去发送这个对象
                    //如果要实现点击功能则要添加PendingIntent
                    Intent intent = new Intent(MainActivity.this, AnotherActivity.class);
                    Intent[] intents = {intent};
                    PendingIntent pi = PendingIntent.getActivities(MainActivity.this, 0, intents, 0);

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    //这里实际使用的v7包中的builder，但是调用的方法是v4包中的方法，因为v7的builder继承自v4的builder
                    Notification notification = new NotificationCompat.Builder(MainActivity.this)
                            .setContentTitle("title")
                            .setContentText("content")
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .build();
                    manager.notify(1,notification);
                    break;
                case R.id.id_btn_go:
                    Intent intent1 = new Intent(MainActivity.this, AnotherActivity.class);
                    startActivity(intent1);
                    break;
                default:
                    break;
            }
        }
    }
}

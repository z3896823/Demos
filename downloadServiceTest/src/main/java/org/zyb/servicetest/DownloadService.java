package org.zyb.servicetest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.zyb.servicetest.utils.DownLoadAsyncTask;
import org.zyb.servicetest.utils.DownloadListener;

/**
 * Created by Administrator on 2017/2/20.
 * 该服务作用相当于一个中间人，但是没有它的话下载也无法在后台进行
 *
 * DownloadListener：
 * onProgress()
 * 根据progress值创建带进度条的通知（非前台服务！）
 * onSuccess()
 * 将DownloadAsyncTask置空，取消前台服务，创建一个下载成功的通知
 * onFailed()
 * 将DownloadAsyncTask置空，取消前台服务，创建一个下载失败的通知
 * onPaused()
 * 保持前台服务和进度条通知不变，弹出Toast通知用户即可
 * onCanceled()
 * 将DownloadAsyncTask置空，取消前台服务，弹出Toast通知用户
 *
 * 注意：
 * 不管下载成功，失败，暂停还是删除，都要将DownloadAsyncTask置空，再重新开始下载的时候永远新建一个新的DownloadAsyncTask。
 * 如果不置空，重新开始下载的时候首先就得把DownloadAsyncTask里的isPaused或者isCancelled变量置false，然后再去执行doInBackground方法
 * 这种做法在理论上是可以的，但是这里不行。首先，我们不能去手动调用异步任务里的那几个方法，再者，同一个Task多次执行或出现异常。
 * 所以这里将DownloadAsyncTask置空是明智的选择
 *
 * DownloadBinder:
 * startDownload()
 * 新建DownloadAsyncTask，弹出前台服务的通知。异步任务启动后自动开始下载
 * pauseDownload()
 * 调用downloadAsyncTask的pauseDownload()方法
 * cancelDownload()
 * 调用downloadAsyncTask的cancelDownload()方法
 *
 */

public class DownloadService extends Service {

    private DownLoadAsyncTask downLoadAsyncTask;//一会将其实例化，然后调用execute()方法即可开始下载。
    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(2,getNotification("Downloading...",progress));
        }

        @Override
        public void onSuccess() {
            downLoadAsyncTask = null;
            stopForeground(true);
            getNotificationManager().notify(2,getNotification("Download success",-1));
            Toast.makeText(DownloadService.this, "Download success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downLoadAsyncTask = null;
            stopForeground(true);
            getNotificationManager().notify(2,getNotification("Download failed",-1));
            Toast.makeText(DownloadService.this, "Download failed", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPaused() {
            downLoadAsyncTask = null;
            Toast.makeText(DownloadService.this, "Download paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downLoadAsyncTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Download canceled", Toast.LENGTH_SHORT).show();
            Toast.makeText(DownloadService.this, "file deleted", Toast.LENGTH_SHORT).show();
        }
    };


    DownloadBinder downloadBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return downloadBinder;
    }


    class DownloadBinder extends Binder {

        public void startDownload(String url){
            //new一个DownloadAsyncTask出来就行
            if (downLoadAsyncTask == null){
                downLoadAsyncTask = new DownLoadAsyncTask(downloadListener);
                downLoadAsyncTask.execute(url);
                startForeground(1,getNotification("Downloading",0));
                Toast.makeText(DownloadService.this, "Download started", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload(){
            //调用downLoadAsyncTask的pause方法
            if (downLoadAsyncTask != null) {
                downLoadAsyncTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            //调用downloadAsyncTask的cancel方法
            if (downLoadAsyncTask !=null){
                downLoadAsyncTask.cancelDownload();
            }
        }
    }

    //获得NotificationManager
    private NotificationManager getNotificationManager(){
        return (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //获得Notification
    private Notification getNotification(String title,int progress){
        Intent intent = new Intent(this,MainActivity.class);
        Intent[] intents = {intent};
        PendingIntent pendingIntent = PendingIntent.getActivities(this,0,intents,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent);
        if (progress > 0) {
            builder.setContentText(progress +"%");
            builder.setProgress(100,progress,false);
        }//如果progress不是正数，则创建一个普通的通知

        Notification notification = builder.build();
        return notification;
    }
}

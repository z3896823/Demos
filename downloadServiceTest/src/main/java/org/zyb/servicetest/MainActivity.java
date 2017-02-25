package org.zyb.servicetest;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by zyb  2017.02.22
 *
 * 此demo将包含以下功能
 * 1.通过bindService启动服务，让服务与活动进行通信
 * 2.启动一个IntentService
 * 3.ServiceBestPractice
 * 3.1.异步任务(OkHttp)
 * 3.2.回调
 * 3.3.IO流
 * 3.4.前台服务(通知)
 * 3.5.运行时权限
 *
 * 注意1：
 * 服务中的onBind方法返回给ServiceConnection的onServiceConnected方法的参数是IBinder类型
 * 而在活动中我们要指挥服务的话必须使用MyBinder的对象，因此必须新建一个MyBinder的引用
 * 并且用过强制转型给这个引用赋予onBind方法返回的IBinder类型的对象
 * 不能直接使用方法参数中的iBinder对象，它是没有MyBinder中的方法的
 *
 * 注意2：
 * 对权限的处理要意识到onRequestPermissionsResult()这个方法是在用户做出了所有的权限选择之后才会回调的方法
 * 并且其中的int[] grantResult参数就是用户所作出的选择，所以即使这个方法中包含了某种操作，也是在用户完成
 * 所有的选择之后才会调用，不可能有三个权限要申请，结果用户判定到第二个的时候程序就执行了某种操作。
 * 可以认为，当用户开始选择权限结果到权限全部处理完，这段时间的程序是不受开发者控制的，开发者只能被动的接受用户选择的结果
 *
 *
 * Download功能模块主要框架：
 * 在活动中开启后台服务，在服务中使用异步任务进行下载功能的实现
 * 在服务中创建一个DownloadListener的匿名实现类的对象，并将该对象传入AsyncTask，供AsyncTask进行回调以通知服务下载进行的情况
 * 将服务与活动绑定，在活动中通过Binder对象来控制服务
 *
 *
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "ybz";

    private Button btn_bindService,btn_unBindService,btn_startIntentService,btn_bindDownloadService,btn_startDownload,btn_pause,btn_cancel,btn_delete;

    private MyService.MyBinder myBinder;

    private DownloadService.DownloadBinder downloadBinder;

    ServiceConnection serviceConnectionTest = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            myBinder = (MyService.MyBinder)binder;
            myBinder.fun1();
            myBinder.fun2();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: executed");
            //该方法在活动与服务失去联系的时候被call（参见源码的注释）
        }
    };

    ServiceConnection downloadServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_bindService = (Button) findViewById(R.id.id_btn_bindService);
        btn_unBindService = (Button) findViewById(R.id.id_btn_unBindService);
        btn_startIntentService = (Button) findViewById(R.id.id_btn_startIntentService);
        btn_bindDownloadService = (Button) findViewById(R.id.id_btn_bindDownloadService);
        btn_startDownload = (Button) findViewById(R.id.id_btn_startDownload);
        btn_pause = (Button) findViewById(R.id.id_btn_pause);
        btn_cancel = (Button) findViewById(R.id.id_btn_cancel);
        btn_delete = (Button) findViewById(R.id.id_btn_delete);

        btn_bindService.setOnClickListener(this);
        btn_unBindService.setOnClickListener(this);
        btn_startIntentService.setOnClickListener(this);
        btn_bindDownloadService.setOnClickListener(this);
        btn_startDownload.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        //region 权限的处理
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 0){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        //endregion
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.id_btn_bindService:
                Intent bindIntent = new Intent(this,MyService.class);
                bindService(bindIntent, serviceConnectionTest, BIND_AUTO_CREATE);
                break;
            case R.id.id_btn_unBindService:
                unbindService(serviceConnectionTest);
                break;
            case R.id.id_btn_startIntentService:
                Intent intent = new Intent(this,MyIntentService.class);
                startService(intent);
                break;


            case R.id.id_btn_bindDownloadService:
                Intent intent1 = new Intent(this,DownloadService.class);
                startService(intent1);//这里必须要启动一下服务，不能只绑定，暂不知为什么
                bindService(intent1,downloadServiceConnection,BIND_AUTO_CREATE);
                Toast.makeText(this, "绑定下载服务成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.id_btn_startDownload:
                downloadBinder.startDownload("http://ftp.yz.yamagata-u.ac.jp/pub/eclipse/oomph/epp/neon/R2a/eclipse-inst-win64.exe");
                break;
            case R.id.id_btn_pause:
                downloadBinder.pauseDownload();
                break;
            case R.id.id_btn_cancel:
                downloadBinder.cancelDownload();
                break;
            case R.id.id_btn_delete:
                break;
            default:
                break;
        }
    }

    //在该方法中，我们只能对用户已经做出的选择进行判断，而并不知道用户做出选择的时候执行的是哪段代码
    //注意区分“用户选择的时候”和“用户选择之后”这两个时间段的不同
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 通过requestCode来判断是哪个活动申请的哪个权限。
        // 因为同一个活动中不同的功能模块可能申请不同的权限，这时候就通过requestCode来区分
        if (requestCode ==1){
            if (grantResults.length>0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "权限被拒绝，下载无法进行", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

package org.zyb.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


/**
 * 1.新建一个内部类，继承自Binder，在其中编写服务要执行的方法
 * 2.在服务类中新建一个MyBinder对象，并在onBind()方法中将其返回
 */
public class MyService extends Service {


    public static final String TAG = "ybz";

    //注意这里是创建一个实例，不能创建一个引用，不然等会在onBind里返回给活动的将是一个空指针
    private MyBinder mBinder = new MyBinder();

    public MyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "myService onCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "myService onDestroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "myService onStartCommand");


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "myService onBinded");

        return mBinder;
    }

    class MyBinder extends Binder {

        //在这里编写服务要执行的方法
        public void fun1(){
            Log.d(TAG, "fun1: executed");
        }

        public void fun2(){
            Log.d(TAG, "fun2: executed");
        }

    }
}

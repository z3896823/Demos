package org.zyb.webviewtest;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/2/17.
 */

public class HttpUtil implements HttpCallbackListener {

    //使用HttpUrlConnection发起请求
    public static void sendHttpRequest(String address){

    }

    //使用OkHttp发起请求
    public static void sendOkHttpRequest(String address, Callback callback){
        //enqueue()方法中帮我们开好了线程，并且调用了回调方法onResponse和onFailure
        //因此在使用这个方法的时候不用手动开启线程
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onFinish(String response) {

    }
}

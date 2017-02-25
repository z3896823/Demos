package org.zyb.okhttptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp使用流程：
 * 1.创建一个OkHttpClient对象，供以对服务器发送请求
 * 2.创建一个Request对象，来包含请求的服务器地址
 * -2.1. Request对象的生成使用的是Builder模式
 * 3.用client发送请求，并使用Response对象接收请求结果
 *
 * 注意：
 * 1.网络请求的代码不要放在主线程中
 * 2.记得申请网络权限
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ybz";


    private Button btn_request;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_request = (Button) findViewById(R.id.id_btn_request);
        tv_result = (TextView) findViewById(R.id.id_tv_result);

        btn_request.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_btn_request:
                //开启子线程，发送HTTP请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //具体的业务逻辑，先创建一个OkHttpClient对象，然后创建一个Request对象用来包含要发送的请求，然后用client把request发送出去
                        try {
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                .url("http://www.baidu.com")
                                .build();//new一个Request类的子类Builder的对象，并调用其build()方法
                            Response response = client.newCall(request).execute();
                            String result = response.body().string();
                            showResponse(result);
                        } catch (IOException e) {
                            Log.d(TAG, "IOException");
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    //定义一个方法将服务器返回的请求在UI线程上更新
    private void showResponse(final String result){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_result.setText(result);
            }
        });
    }
}

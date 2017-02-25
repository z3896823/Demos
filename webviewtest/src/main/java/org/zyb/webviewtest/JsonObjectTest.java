package org.zyb.webviewtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/16.
 *
 * 两种不同的方式获得的Json文件内容不同，所以取数据的时候要修改parseJsonWithJsonObject()方法的方法体
 * 否则找不到键的话会报JSONException
 *
 * 同样，从网络中获取数据的话注意权限和线程问题
 *
 *
 * 本页面中实现三个功能：
 * 1.从本地读取json文件，将数据取出打印在日志中
 * 2.从网络获得json文件，将数据取出打印在日志中
 * 3.用HttpUtil工具类以回调的方式获取数据，并在UI线程中更新UI
 */

public class JsonObjectTest extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_result;
    private Button btn_local,btn_net,btn_httpUtil;
    private String TAG = "ybz";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);

        tv_result = (TextView) findViewById(R.id.id_tv_result);
        btn_local = (Button) findViewById(R.id.id_btn_local);
        btn_net = (Button) findViewById(R.id.id_btn_net);
        btn_httpUtil = (Button) findViewById(R.id.id_btn_httpUtil);


        btn_local.setOnClickListener(this);
        btn_net.setOnClickListener(this);
        btn_httpUtil.setOnClickListener(JsonObjectTest.this);

        Log.d(TAG, "main Thread id is: "+Thread.currentThread().getId());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.id_btn_local:
                getJsonFromLocal();
                break;
            case R.id.id_btn_net:
                getJsonFromNet();
                break;
            case R.id.id_btn_httpUtil:
                getJsonWithHttpUtil();
                break;
            default:
                break;
        }
    }

    private void getJsonFromLocal(){
        try {
            InputStreamReader isr = new InputStreamReader(getAssets().open("book.json"),"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine())!= null){
                builder.append(line);
            }
            String result = builder.toString();//这里读取到的结果是json文件的纯文本内容
            parseJsonWithJsonObject(result);//用这个方法将纯文本的json按照json的格式解析出来
        } catch (IOException e) {
            Log.d(TAG, "IOException caught");
        }
    }

    private void getJsonFromNet(){

        //自己通过OkHttp发起请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder().url("http://guolin.tech/api/china").build();
                try {
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    Log.d(TAG, result);//同样，这里打印的是服务器返回的纯文本内容（有可能不是标准json格式）
                    parseJsonWithJsonObject(result);
                } catch (IOException e) {
                    Log.d(TAG, "IOException caught");
                }
            }
        }).start();

    }

    private void getJsonWithHttpUtil(){
        //调用HttpUtil工具类的静态方法发起请求
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/china", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "current thread id is: "+ Thread.currentThread().getId());//打印线程id发现此方法运行在子线程中

                String result = response.body().string();
                showJsonOnText(result);
            }
        });
    }

    private void parseJsonWithJsonObject(String result){

        //由于传入JSONArray构造方法的字符串内容可能不是标准的json格式，所以此方法必须抛出异常
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String id = jsonObject.get("id").toString();
                Log.d(TAG, id);
                String name = (String) jsonObject.get("name");
                Log.d(TAG, name);
//                String title = jsonObject.get("title").toString();
//                Log.d(TAG, title);
//                String author = jsonObject.getString("author");
//                Log.d(TAG, author);
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException caught--parseError");
        }
    }

    private void showJsonOnText(final String result){

        Log.d(TAG, result);
        //从子线程中跳出来，在UI线程中更新
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //由于传入JSONArray构造方法的字符串内容可能不是标准的json格式，所以此方法是有可能抛出异常的，必须try catch
                try {
                    JSONArray jsonArray = new JSONArray(result);

                    StringBuilder builder = new StringBuilder();
                    for (int i = 0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);//和上面的get然后再强制转型是一样的效果
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        builder.append(id+" "+name+"\n");
                    }
                    tv_result.setText(builder.toString());

                } catch (JSONException e) {
                    Log.d(TAG, "JsonException caught");
                    e.printStackTrace();
                }
            }
        });
    }
}

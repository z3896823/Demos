package org.zyb.webviewtest;

import android.content.Intent;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * 本demo中用到了以下知识点：
 * 1.webView控件的使用
 * 2.json数据的解析
 * 3.使用HttpUrlConnection发起Http请求
 * 4.使用OkHttp开源库发起Http请求
 * 5.使用HttpUtil工具类封装Http请求功能
 * 6.**在HttpUtil中使用回调机制**
 * 7.深入理解实现OnClickListener接口的方法给View注册监听的机制
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private WebView wv_test;
    private Button btn_json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wv_test = (WebView) findViewById(R.id.id_wv_web);

        btn_json = (Button) findViewById(R.id.id_btn_json);

        wv_test.getSettings().setJavaScriptEnabled(true);
        wv_test.setWebViewClient(new WebViewClient());
        wv_test.loadUrl("http://www.baidu.com");

        btn_json.setOnClickListener(this);
        btn_json.setText("json相关操作");
        btn_json.setAllCaps(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_btn_json:
                Intent intent = new Intent(this,JsonObjectTest.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}

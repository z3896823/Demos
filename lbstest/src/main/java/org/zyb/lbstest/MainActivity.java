package org.zyb.lbstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ybz";

    private LocationClient client;

    private TextView tv_myLocation;

    private MapView mv_baiduMap ;//百度地图的地图控件

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    Bundle resultBundle = msg.getData();
                    String result = resultBundle.getString("result");
                    tv_myLocation.setText(result);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(this);
        setContentView(R.layout.activity_main);
        tv_myLocation = (TextView) findViewById(R.id.id_tv_myLocation);

        client = new LocationClient(this);//注意下这里的context
        client.registerLocationListener(new MyLocationListener());

        //region 权限处理
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,permissions,1);
        }
        //endregion

        LocationClientOption clientOption = new LocationClientOption();
        clientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        clientOption.setScanSpan(3000);
        client.setLocOption(clientOption);
        client.start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length != 0){
                    for (int i : grantResults){
                        if(i != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "有权限被拒绝，程序将退出", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }else {
                    Log.d(TAG, "未知错误");
                }
                break;
            default:
                break;
        }
    }


    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location){
            //定位到的参数全部放在location对象中
            final StringBuilder builder = new StringBuilder();
            builder.append("经度：").append(location.getLatitude()).append("\n");
            builder.append("纬度：").append(location.getLongitude()).append("\n");
            if (location.getLocType() == BDLocation.TypeGpsLocation){
                builder.append("定位方式：GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                builder.append("定位方式：网络");
            }
            Log.d(TAG, "current thread id is :"+Thread.currentThread().getId());
            Log.d(TAG, builder.toString());

            String result = builder.toString();
            Bundle bundle = new Bundle();
            bundle.putString("result",result);
            Message msg = new Message();
            msg.what = 1;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }

        @Override
        public void onConnectHotSpotMessage(String var1,int var2){

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        client.stop();
    }


}

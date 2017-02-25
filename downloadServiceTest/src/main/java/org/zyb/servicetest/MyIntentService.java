package org.zyb.servicetest;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2017/2/19.
 * 关于继承时构造函数的继承：
 * 1.子类不能继承父类的构造函数，如果父类只有有参数的构造函数，则子类在继承时必须使用super关键字调用父类的构造函数
 * 2.如果父类有无参的构造函数，则子类不一定需要使用super调用父类的构造函数，此时，系统将会自动调用父类的无参构造函数
 *
 * 在这里，IntentService没有无参的构造函数，所以MyIntentService必须使用super关键字调用其父类的有参构造函数
 */

public class MyIntentService extends IntentService {

    public static final String TAG = "ybz";

    //这个构造函数还非得这么写  不然AndroidManifest文件注册不了
    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: executed");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "IntentService onDestroy: executed");
    }
}

package org.zyb.webviewtest;

/**
 * Created by Administrator on 2017/2/17.
 */

public interface HttpCallbackListener {

    //定义两个方法让实现类去实现
    void onFinish(String response);
    void onError(Exception e);
}

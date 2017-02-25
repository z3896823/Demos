package org.zyb.chat;

import java.net.ConnectException;

/**
 * Created by Administrator on 2017/2/13.
 */

public class Msg {

    public static final int TYPE_RECEIVE = 0;
    public static final int TYPE_SEND = 1;
    String content ;
    int msgType;

    public Msg(int type, String content){
        this.content = content;
        this.msgType = type;
    }

    public int getMsgType(){
        return msgType;
    }

    public String getContent(){
        return content;
    }
}

package org.zyb.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * 两个不完善的地方：
 * 1.调出键盘的时候不会自动把最下面一行显示到键盘上方，即键盘会覆盖掉下面的消息
 * 2.滑动消息列表时输入框不会自动消失
 */
public class MainActivity extends AppCompatActivity {

    private List<Msg> welcome = new ArrayList<>();
    private EditText et_input;
    private Button btn_send;
    private RecyclerView rv_list;
    private MsgAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_input = (EditText) findViewById(R.id.id_et_input);
        btn_send = (Button) findViewById(R.id.id_btn_send);
        rv_list =(RecyclerView)findViewById(R.id.id_rv_content);

        init();//数据的初始化

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(layoutManager);

        adapter = new MsgAdapter(welcome);
        rv_list.setAdapter(adapter);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et_input.getText().toString();//获得输入的内容先
                //生成一个对象，往消息队列中添加，然后重新setAdapter
                Msg newMsg = new Msg(Msg.TYPE_SEND,s);
                welcome.add(newMsg);

                //我的方法：效率低
                //rv_list.setAdapter(new MsgAdapter(welcome));
                //郭霖的方法：
                adapter.notifyItemInserted(welcome.size() - 1);
                rv_list.scrollToPosition(welcome.size()-1);
                et_input.setText("");//每次发送完后将输入框清空
            }
        });


    }

    public void init(){
        //对hello进行装载部分数据

        Msg m1 = new Msg(Msg.TYPE_RECEIVE,"hello");
        Msg m2 = new Msg(Msg.TYPE_SEND,"hello you too");

        welcome.add(m1);
        welcome.add(m2);
    }
}

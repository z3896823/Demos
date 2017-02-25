package org.zyb.litepaltest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    private TextView tv_result;
    private Button btn_create,btn_insert,btn_delete,btn_select,btn_update,btn_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_create = (Button)findViewById(R.id.create);
        btn_insert = (Button)findViewById(R.id.insert);
        btn_delete = (Button)findViewById(R.id.delete);
        btn_select = (Button)findViewById(R.id.select);
        btn_update = (Button)findViewById(R.id.update);
        btn_call = (Button)findViewById(R.id.call);
        tv_result = (TextView)findViewById(R.id.content);

        btn_create.setOnClickListener(new MyClickListener());

        btn_insert.setOnClickListener(new MyClickListener());

        btn_select.setOnClickListener(new MyClickListener());

        btn_delete.setOnClickListener(new MyClickListener());

        btn_update.setOnClickListener(new MyClickListener());

        btn_call.setOnClickListener(new MyClickListener());


    }

    class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.create:
                    LitePal.getDatabase();
                    break;
                case R.id.insert:
                    //创建一个对象，调用save()方法
                    Book book = new Book("warrior","Alan", 15.8);
                    book.save();
                    break;
                case R.id.update:
                    //新建一个对象，并对这个对象进行部分赋值，然后调用updateAll()方法去对匹配的一直对象进行属性覆盖
                    Book book_update = new Book();
                    book_update.setAuthor("GuoShen");
//                    book_update.update(2);//如果知道id号，根据id号去寻找对应的对象
                    book_update.updateAll("title = ?","first line");//如果不知道id号，只能根据别的条件去寻找匹配的对象（们）
                    break;
                case R.id.delete:
                    DataSupport.deleteAll("book","price >?", "10");
                    break;
                case R.id.select:
                    List<Book> result = DataSupport.select("title","author").where("price > ?", "10").find(Book.class);
                    StringBuilder builder = new StringBuilder();
                    for (int i =0; i<result.size(); i++){
                        Book book_result = result.get(i);
                        builder.append(book_result.getTitle() +" by "+book_result.getAuthor()+"\n");
                    }
                    tv_result.setText(builder.toString());
                    break;
                case R.id.call:
                    //直接调用call会跑出异常，因此必须先进行权限判断

                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                        //检查权限是否已经授权，是
                        call();
                    } else {
                        //否则就动态申请权限
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.CALL_PHONE},1);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void call(){
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:10010"));
            startActivity(intent);
        }catch(SecurityException e){
            Log.d("ybz", "exception caught");
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    call();
                } else {
                    Toast.makeText(MainActivity.this, "permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }
}

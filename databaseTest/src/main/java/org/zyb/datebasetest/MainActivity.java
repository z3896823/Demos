package org.zyb.datebasetest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView result;
    private Button create_btn,insert_btn,delete_btn,select_btn,update_btn,transaction_btn;
    private MyDatabaseOpenHelper myOpenHelper;
    private SQLiteDatabase myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        create_btn = (Button)findViewById(R.id.create);
        insert_btn = (Button)findViewById(R.id.insert);
        delete_btn = (Button)findViewById(R.id.delete);
        select_btn = (Button)findViewById(R.id.select);
        update_btn = (Button)findViewById(R.id.update);
        transaction_btn = (Button)findViewById(R.id.transaction);
        result = (TextView)findViewById(R.id.content);

        myOpenHelper = new MyDatabaseOpenHelper(this,"Book.db",null,1);

        //建表
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb = myOpenHelper.getWritableDatabase();
                result.setText("创建成功");
            }
        });

        //增
        insert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ContentValues values = new ContentValues();
//                //insert into Book values (1,"郭霖",50.0,550,"first code")
//                //开始包装第一个数据对象
//                values.put("ID",1);
//                values.put("author","郭霖");
//                values.put("price",50.0);
//                values.put("pages",550);
//                values.put("name","first code");
//                myDb.insert("Book",null,values);
//                //开始包裹第二个数据对象
//                values.clear();
//                values.put("ID",2);
//                values.put("author","郭大神");
//                values.put("price",52.0);
//                values.put("pages",520);
//                values.put("name","second code");
//                myDb.insert("Book",null,values);
//                result.setText("插入成功");

                //除了以上使用SQLiteDatabase的insert()方法意外，还可以直接执行SQL语句来完成相同的操作
                myDb.execSQL("insert into Book (author,price,pages,name ) values(?,?,?,?)",
                            new String[]{"BigNerd","50.0","550","Android编程权威指南"});
            }
        });

        //删
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete from Book where pages > 530
                myDb.delete("Book","pages<?",new String[]{"530"});
                result.setText("删除成功");
            }
        });

        //查
        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = myDb.rawQuery("select * from Book",null);//得到结果包
                //遍历结果包，取出数据
                String r = "";
                if(cursor.moveToFirst()){
                    do{
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        double price = cursor.getDouble(cursor.getColumnIndex("price"));
                        int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                        r = "书名："+name+"  作者："+author+"  价格："+price+"  页数："+pages+"\n"+r;
                        result.setText(r);
                    }while(cursor.moveToNext());
                }
                cursor.close();//数据取完后要断开与数据库的连接，防止出现SQLiteException
            }
        });

        //改（更新）
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put("price",45.0);
                //update Book set values = 45.0 where name = "first code"
                myDb.update("Book",values,"name = ?",new String[]{"first code"});
                result.setText("更新成功");
            }
        });

        //事务
        transaction_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb.beginTransaction();
                try{
                    myDb.execSQL("delete from book where price >?",new String[]{"59"});
                    if (true){
                        throw new NullPointerException();
                    }
                    ContentValues values = new ContentValues();
                    values.put("name","third code");
                    values.put("price",30.0);
                    values.put("author","郭神");
                    myDb.insert("Book",null,values);
                    myDb.setTransactionSuccessful();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"事务失败",Toast.LENGTH_LONG).show();
                }finally {
                    myDb.endTransaction();
                    Toast.makeText(MainActivity.this,"事务成功",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

package org.zyb.datebasetest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/12/19.
 */

/**
 * 对onCreate和onUpdate方法的说明
 * 数据库的创建是在活动里创建一个SQLiteOpenHelper对象的时候生成的
 * 生成对象的时候要传入一些参数给构造函数，然后这个类根据传入的参数决定生成什么样的对象
 * 这个时候是没有创建数据库的
 * 接着调用OpenHelper的get方法，这个方法会检查构造的对象的数据库名和版本号，并判断当前是否存在同名的数据库以及其版本
 * 如果不存在，就会调用OpenHelper的onCreate方法，这时候才会生成数据库，同时也创建了一张表
 * 如果存在，且版本相同，则不会调用任何函数
 * 如果存在，但版本号比原来的大，则会调用onUpdate方法
 */

public class MyDatabaseOpenHelper extends SQLiteOpenHelper {

    private Context mContext;

    public static final String CREATE_BOOK= "create table Book(" +
            "ID integer primary key autoincrement," +
            "author nvarchar," +
            "price real," +
            "pages integer," +
            "name nvarchar)";

    public  static final String CREATE_CATEGORY = "create table Category(" +
            "id integer primary key," +
            "category_name nvarchar," +
            "category_code nvarchar)";

    public MyDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_BOOK);
        Toast.makeText(mContext,"create succeeded",Toast.LENGTH_LONG).show();
    }

    //upDate方法的存在是为了实现程序数据库更新时不会删除之前的本地数据库
    //同时还能保证跨版本更新时所有的更新都能被执行
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch(oldVersion){
            case 1:
                sqLiteDatabase.execSQL(CREATE_CATEGORY);
            case 2:
                sqLiteDatabase.execSQL("alter table Book add colum category_id integer");
            default:
        }
    }
}

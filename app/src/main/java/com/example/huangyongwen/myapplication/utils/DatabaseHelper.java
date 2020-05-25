package com.example.huangyongwen.myapplication.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.meizu.lastmile.Utils.ConstantUtils;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 15:55
 * @CreateDate:
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String SWORD="SWORD";
    //三个不同参数的构造函数
    //带全部参数的构造函数，此构造函数必不可少
    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);

    }
    //带两个参数的构造函数，调用的其实是带三个参数的构造函数
    public DatabaseHelper(Context context,String name){
        this(context,name,VERSION);
    }
    //带三个参数的构造函数，调用的是带所有参数的构造函数
    public DatabaseHelper(Context context,String name,int version){
        this(context, name,null,version);
    }
    //创建数据库
    public void onCreate(SQLiteDatabase db) {
        Log.i(SWORD,"create a Database");
        //创建数据库sql语句
//        db.execSQL("DROP TABLE IF NOT EXISTS " + "user1");
        String sql = "create table user1(id int,name varchar(20),test1 varchar(30))";
        db.execSQL("create table person (_id integer primary key autoincrement, " +
                "name char(10), phone char(20), money integer(20))");
        //执行创建数据库操作
        db.execSQL("insert into person (name, phone, money) values (?, ?, ?);",
                new Object[]{"张三", 15987461, 75000});
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //创建成功，日志输出提示
        Log.i(SWORD,"update a Database");
    }
}

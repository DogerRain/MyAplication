package com.meizu.lastmile.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/18 15:55
 * @CreateDate:
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String SWORD = "SWORD";

    private static final String databaseName = "lastmile";


    private SQLiteDatabase sqLiteDatabase = null;

    public void setSqLiteDatabase(SQLiteDatabase db) {
        this.sqLiteDatabase = db;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }


    //三个不同参数的构造函数
    //带全部参数的构造函数，此构造函数必不可少
    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    //带两个参数的构造函数，调用的其实是带三个参数的构造函数
    public DatabaseHelper(Context context) {
        this(context, databaseName, VERSION);
    }


    //带三个参数的构造函数，调用的是带所有参数的构造函数
    public DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(SWORD, "create a Database");
        //创建数据库sql语句
//        String sql = "create table user(id int,name varchar(20))";
        //执行创建数据库操作
//        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //创建成功，日志输出提示
        Log.i(SWORD, "update a Database");
    }


    public boolean IsTableExist(SQLiteDatabase db, String tableName) {
        String sql = "SELECT count(*) FROM sqlite_master  WHERE type='table' AND name=?";
        Cursor cursor = db.rawQuery(sql, new String[]{tableName});
        while (cursor.moveToNext()) {
            //遍历出表名
            if (cursor.getString(0) != null) { //不为空证明表存在
                return true;
            }
        }
        return false;
    }

    public void createTable(SQLiteDatabase db, String sql) {
        db.execSQL(sql);
    }

    /**
     * 插入sql
     * @param db
     * @param tableName
     * @param values
     */

    public void insert(SQLiteDatabase db, String tableName, ContentValues values) {
        //数据库执行插入命令
        db.insert(tableName, null, values);
    }

    /**
     * 更新
     *
     * @param db
     * @param tableName
     * @param values
     */
    public void update(SQLiteDatabase db, String tableName, ContentValues values) {

        db.update("user", values, "id=?", new String[]{"1"});
    }

    /**
     * 删除
     *
     * @param db
     * @param tableName
     * @param values
     */
    public void delete(SQLiteDatabase db, String tableName, String[] values) {

        db.delete(tableName, "id=? ", values);
    }

//    public void exceCustomSQL(SQLiteDatabase db, String sql) {
//        db.execSQL();
//    }


    public Boolean queryTaskIdSQL(SQLiteDatabase db, String sql, String[] selectionArgs) {
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        Boolean flag = false;
        if (cursor.getCount() > 0) {
            flag = true;
        }
        if (cursor != null) {
            cursor.close();
        }
        return flag;
    }

}

package com.example.huangyongwen.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.huangyongwen.myapplication.utils.DatabaseHelper;
import com.meizu.lastmile.LastmileClient;

public class Main2Activity extends Activity implements View.OnClickListener {

    private final static String SWORD = "SWORD";
    //声明五个控件对象
    Button createDatabase = null;
    Button updateDatabase = null;
    Button insert = null;
    Button update = null;
    Button query = null;
    Button delete = null;
    Button receiveTask = null;
    Button runTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViews();
    }


    private void findViews() {
        //根据控件ID得到控件
        createDatabase = (Button) this.findViewById(R.id.createDatabase);
        updateDatabase = (Button) this.findViewById(R.id.updateDatabase);
        insert = (Button) this.findViewById(R.id.insert);
        update = (Button) this.findViewById(R.id.update);
        query = (Button) this.findViewById(R.id.query);
        delete = (Button) this.findViewById(R.id.delete);


        //增加两个 接收任务 和 跑 任务
        receiveTask = (Button) this.findViewById(R.id.receiveTask);
        runTask = (Button) this.findViewById(R.id.runTask);


        //添加监听器
        createDatabase.setOnClickListener(this);
        updateDatabase.setOnClickListener(this);
        insert.setOnClickListener(this);
        update.setOnClickListener(this);
        query.setOnClickListener(this);
        delete.setOnClickListener(this);
        receiveTask.setOnClickListener(this);
        runTask.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
// 判断所触发的被监听控件，并执行命令
        Log.i("Tag:", "监控点击事件");

        switch (v.getId()) {
            //创建数据库
            case R.id.createDatabase:
                Log.i("Tag:", "创建数据库");
                //创建一个DatabaseHelper对象
                DatabaseHelper dbHelper1 = new DatabaseHelper(Main2Activity.this, "test_db");
                //取得一个只读的数据库对象
                SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
//                db1.execSQL("create table user(id int,name varchar(20))");
                break;
            //更新数据库
            case R.id.updateDatabase:
                DatabaseHelper dbHelper2 = new DatabaseHelper(Main2Activity.this, "test_db", 2);
                SQLiteDatabase db2 = dbHelper2.getReadableDatabase();
                break;
            //插入数据
            case R.id.insert:
                //创建存放数据的ContentValues对象
                ContentValues values = new ContentValues();
                //像ContentValues中存放数据
                values.put("id", 1);
                values.put("name", "zhangsan");
                DatabaseHelper dbHelper3 = new DatabaseHelper(Main2Activity.this, "test_db");
                SQLiteDatabase db3 = dbHelper3.getWritableDatabase();
                //数据库执行插入命令
                db3.insert("user", null, values);
                break;
            //更新数据信息
            case R.id.update:
                DatabaseHelper dbHelper4 = new DatabaseHelper(Main2Activity.this, "test_db");
                SQLiteDatabase db4 = dbHelper4.getWritableDatabase();
                ContentValues values2 = new ContentValues();
                values2.put("name", "xiaosan");
                db4.update("user", values2, "id=?", new String[]{"1"});
                break;
            //查询信息
            case R.id.query:
                DatabaseHelper dbHelper5 = new DatabaseHelper(Main2Activity.this, "test_db");
                SQLiteDatabase db5 = dbHelper5.getReadableDatabase();
                //创建游标对象
                Cursor cursor = db5.query("user", new String[]{"id", "name"}, "id=?", new String[]{"1"}, null, null, null, null);
                //利用游标遍历所有数据对象
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    //日志打印输出
                    Log.i(SWORD, "query-->" + name);
                }
                break;
            //删除记录
            case R.id.delete:
                DatabaseHelper dbHelper6 = new DatabaseHelper(Main2Activity.this, "test_db");
                SQLiteDatabase db6 = dbHelper6.getWritableDatabase();
                db6.delete("user", "id=?", new String[]{"1"});
                break;

            case R.id.receiveTask:

                Log.i(SWORD, "接收任务》》》》》》");
                String jsonString = "\n" +
                        "{\n" +
                        "\t\"taskId\": 1234566, \n" +
                        "  \"taskType\": \"ping\",\n" +
                        "\t\"groups\": [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"idc\": \"ns\",\n" +
                        "\t\t\t\"isp\": [\"telecom\", \"unicom\"],\n" +
                        "\t\t\t\"cities\": [\n" +
                        "\t\t\t\t\"zhuhai\",\n" +
                        "\t\t\t\t\"guangzho\"\n" +
                        "\t\t\t]\n" +
                        "\t\t},\n" +
                        "\t\t{\n" +
                        "\t\t\t\"idc\": \"bj\",\n" +
                        "\t\t\t\"isp\": [\"mobile\"],\n" +
                        "\t\t\t\"cities\": [\n" +
                        "\t\t\t\t\"beijing\",\n" +
                        "\t\t\t\t\"tianjin\"\n" +
                        "\t\t\t]\n" +
                        "\t\t}\n" +
                        "  ],\n" +
                        "        \n" +
                        "  \"host\": \"14.152.74.1\",\n" +
                        "  \"timeout\": 10,\n" +
                        "    \"size\": 32,\n" +
                        "    \"count\": 4,\n" +
                        "    \"tcpPing\": false,\n" +
                        "    \"interval\": 0.2,\n" +
                        "    \"supportIPv6\": 2,\n" +
                        "    \"dnsMatch\": 0\n" +
                        "}";
                new LastmileClient(Main2Activity.this).reviceInstructions(jsonString);
                break;
            case R.id.runTask:
                Log.i(SWORD, "执行本地任务》》》》》》");
                new LastmileClient(Main2Activity.this).startLocalTask(null);
                break;


            case R.id.receivePageTask:
                String jsonPageString = "{\n" +
                        "    \"taskId\":1234567,\n" +
                        "    \"taskType\":\"page\",\n" +
                        "    \"groups\":[\n" +
                        "        {\n" +
                        "            \"idc\":\"ns\",\n" +
                        "            \"isp\":[\n" +
                        "                \"telecom\",\n" +
                        "                \"unicom\"\n" +
                        "            ],\n" +
                        "            \"cities\":[\n" +
                        "                \"zhuhai\",\n" +
                        "                \"guangzho\"\n" +
                        "            ]\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"idc\":\"bj\",\n" +
                        "            \"isp\":[\n" +
                        "                \"mobile\"\n" +
                        "            ],\n" +
                        "            \"cities\":[\n" +
                        "                \"beijing\",\n" +
                        "                \"tianjin\"\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"url\":\"https://fms.res.meizu.com/dms/2020/05/08/041087f7-680e-40fe-a2cc-bcdb81931aa3.png\",\n" +
                        "    \"timeout\":120,\n" +
                        "    \"useRedirect\":true,\n" +
                        "    \"httpHeaders\":[\n" +
                        "        \"User-Agent:mz-lastmile\"\n" +
                        "    ],\n" +
                        "    \"hijacking\":false,\n" +
                        "    \"expectContaining\":null,\n" +
                        "    \"expireFrom\":\"2019-12-20 00:00:00\",\n" +
                        "    \"expireTo\":\"2020-12-20 23:59:59\"\n" +
                        "}";
                new LastmileClient(Main2Activity.this).reviceInstructions(jsonPageString);
                break;

            case R.id.runPageTask:


            default:
                Log.i(SWORD, "error");
                break;
        }
    }
}

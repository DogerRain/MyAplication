package com.example.lastmile.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lastmile.application.permission.PermissionsChecker;
import com.example.lastmile.application.utils.CommonUtil;
import com.meizu.lastmile.Utils.IPUtils;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    String TAG = "myTAG";


    //Android文件读写权限
    String[] permsLocation = {"android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    Button buttonGo, buttonPermissiom, buttonIP;
    TextView textView2, textViewIP;

    //    private PermissionsCheckerB mPermissionsCheckerB; // 权限检测器
//    private PermissionsChecker permissionsChecker; // 权限检测器
    private PermissionsChecker permissionsChecker;

    private final int RESULT_CODE_LOCATION = 0x001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate方法执行");
//        Application a = (Application) getApplicationContext();
//        System.out.println(a);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //调用finish就会杀死activity，或者翻转屏幕;会调用onDestory
//        finish();
//
        buttonPermissiom = findViewById(R.id.btn_request_permission);
        buttonGo = findViewById(R.id.buttonGo);

        textView2 = findViewById(R.id.textView2);
        textViewIP = findViewById(R.id.textViewIP);

        buttonIP = findViewById(R.id.getIP);

        permissionsChecker = new PermissionsChecker(MainActivity.this);

        buttonPermissiom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String s = textView2.getText().toString();
//                Boolean flag = new PermissionsChecker(MainActivity.this).checkPermission(MainActivity.this);
                Boolean isLackPerssion = new com.meizu.lastmile.Utils.PermissionsChecker(MainActivity.this).checkLackWritePermission();

                if (!isLackPerssion) {
                    //表示有权限
                    Log.d(TAG, "有权限: ");

                    //执行lastmile操作

                    Log.d(TAG, "执行lastmile操作: ");

                    //存储到本地
                    Log.d(TAG, "存储数据到用户本地: ");

                    Map<String, String> map = new HashMap<String, String>(); //本地保存数据
                    map.put("userid", "张三");
                    map.put("userpwd", "123456");
                    CommonUtil.saveSettingNote(MainActivity.this, "userinfo", map);//参数（上下文，userinfo为文件名，需要保存的数据）

                } else {
                    //没有权限
                    Log.d(TAG, "没有权限: ");
                }
//                mPermissionsCheckerB.check(MainActivity.this);

//                if (s.equals("你好世界")) {
//                    s = "HelloWorld";
//                } else {
//                    s = "你好世界";
//                }
//                textView2.setText(s);

            }


        });

        buttonIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ip = IPUtils.getIpAddress(MainActivity.this);

                textViewIP.setText(ip);


            }
        });


        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new PingNet().start();
                Log.i(TAG, "跳转");
                Intent intent = new Intent();
                //setClass函数的第一个参数是一个Context对象
                //Context是一个类，Activity是Context类的子类，也就是说，所有的Activity对象，都可以向上转型为Context对象
                //setClass函数的第二个参数是一个Class对象，在当前场景下，应该传入需要被启动的Activity类的class对象
                intent.setClass(MainActivity.this, Main2Activity.class);
                startActivity(intent);
//                new PingService().receiveInstructionAndStorage(null,MainActivity.this);
            }
        });


    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    //系统杀掉
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}

package com.example.huangyongwen.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.huangyongwen.myapplication.service.utils.CommonUtil;
import com.meizu.lastmile.LastmileClient;
import com.meizu.lastmile.permission.PermissionsChecker;

import java.util.HashMap;
import java.util.Map;

import permission.PermissionsCheckerB;

public class MainActivity extends Activity {

    String TAG = "myTAG";


    //Android文件读写权限
    String[] permsLocation = { "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    Button button2;
    TextView textView2;

    private PermissionsCheckerB mPermissionsCheckerB; // 权限检测器
    private PermissionsChecker permissionsChecker; // 权限检测器

    private final int RESULT_CODE_LOCATION = 0x001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //调用finish就会杀死activity，或者翻转屏幕;会调用onDestory
//        finish();
//
        new LastmileClient().getAllLastmileData();
        button2 = findViewById(R.id.btn_request_permission);
        Button button3= findViewById(R.id.button2);
        textView2 = findViewById(R.id.textView2);

        mPermissionsCheckerB= new PermissionsCheckerB(MainActivity.this);

        permissionsChecker = new PermissionsChecker(MainActivity.this);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1", "1111111111111");
//                String s = textView2.getText().toString();

                permissionsChecker.check(MainActivity.this);
//                mPermissionsCheckerB.check(MainActivity.this);

//                if (s.equals("你好世界")) {
//                    s = "HelloWorld";
//                } else {
//                    s = "你好世界";
//                }
//                textView2.setText(s);
                Map<String, String> map = new HashMap<String, String>(); //本地保存数据
                map.put("userid", "张三");
                map.put("userpwd", "123456");
                CommonUtil.saveSettingNote(MainActivity.this, "userinfo", map);//参数（上下文，userinfo为文件名，需要保存的数据）
            }


        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid= CommonUtil.getSettingNote(MainActivity.this, "userinfo", "userid");
                String userpwd= CommonUtil.getSettingNote(MainActivity.this, "userinfo", "userpwd");
                Log.d("userid:","userId:"+ userid);
                Log.d("userpwd:","userpwd:"+ userpwd);
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

package com.example.huangyongwen.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.huangyongwen.myapplication.service.InitService;

public class MainActivity extends Activity {

    String TAG = "myTAG";

    Button button2;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //调用finish就会杀死activity，或者翻转屏幕;会调用onDestory
//        finish();


        button2 = findViewById(R.id.button2);
        textView2 = findViewById(R.id.textView2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1", "1111111111111");
                String s = textView2.getText().toString();
                if (s.equals("你好世界")) {
                    s = "HelloWorld";
                }
                else {
                    s = "你好世界";
                }
                textView2.setText(s);
            }
        });
//        new InitService().init(button2,textView2);
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

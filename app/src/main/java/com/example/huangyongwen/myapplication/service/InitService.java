package com.example.huangyongwen.myapplication.service;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Administrator on 2020/5/10.
 */

public class InitService {


    public void init(Button button2, final TextView textView2) {
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1", "1111111111111");
                String s = textView2.getText().toString();
                String ss = textView2.getText().toString();
                System.out.println(ss);
                if (TextUtils.isEmpty("你好世界")) {
                    s = "HelloWorld";
                }
                else {
                    s = "你好世界";
                }
                textView2.setText(s);
            }
        });
    }
}

package com.meizu.lastmile;

import android.content.Context;

/**
 * @Author: huangyongwen
 * @CreateDate: 2020/5/25 10:57
 * @Description:
 */

public class ThreadExcuse extends Thread {

    String jsonString;

    Context context;

    public ThreadExcuse(String jsonString, Context context) {
        this.jsonString = jsonString;
        this.context = context;
    }

    @Override
    public void run() {

    }
}

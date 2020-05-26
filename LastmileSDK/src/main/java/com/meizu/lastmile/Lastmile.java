package com.meizu.lastmile;

import android.content.Context;

import com.meizu.lastmile.requestObj.Options;

/**
 * Created by huangyongwen on 2020/5/13.
 */

public interface Lastmile {


    public void reviceInstructions(String jsonString, Options options);

    /**
     * 参数待定
     *
     * @param content
     */
    public void getLastestTask();

    public void startLocalTask();
}

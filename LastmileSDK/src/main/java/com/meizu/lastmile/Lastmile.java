package com.meizu.lastmile;

import android.content.Context;

/**
 * Created by huangyongwen on 2020/5/13.
 */

public interface Lastmile {

    public void reviceInstructions(String content);

    /**
     * 参数待定
     *
     * @param content
     */
    public void getLastestTask(String content);

    public void startTask(Context context);
}

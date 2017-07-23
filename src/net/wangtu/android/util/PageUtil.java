package net.wangtu.android.util;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by zhangxz on 2017/7/19.
 */

class PageUtil {
    /**
     * 打开新页面
     * @param activity
     * @param targetClass
     */
    public static void startActivity(Activity activity,Class<?> targetClass){
        Intent intent = new Intent();
        intent.setClass(activity, targetClass);
        activity.startActivity(intent);
    }
}

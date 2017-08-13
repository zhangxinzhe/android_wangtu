package net.wangtu.android.util;

import android.util.Log;

/**
 * Created by zhangxz on 2017/7/19.
 */

public class LogUtil {
    public static void error(Throwable e,Class<?> cls){
        Log.e("wangtu",e.getMessage());
    }

    public static void error(Throwable e){
        Log.e("wangtu",e.getMessage());
    }

    /**
     *
     * @param message
     */
    public static void debug(String message){
        Log.d("wangtu",message);
    }

    /**
     *
     * @param message
     */
    public static void info(String message){
        Log.i("wangtu", message);
    }
}

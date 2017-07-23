package net.wangtu.android.util;

import android.app.Activity;

import net.wangtu.android.R;

/**
 * Created by zhangxz on 2017/7/19.
 */

public class ActivityUtil {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    /**
     * 结束动画
     * @param activity
     * @param direct
     */
    public static void finish(Activity activity, int direct){
        if(direct == LEFT){
            activity.overridePendingTransition(R.anim.activity_out, R.anim.activity_out_reset);// 从右往左
        }else{
            activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_in_reset);// 从左往右
        }
    }

    /**
     * activity启动动画
     * @param activity
     * @param direct
     */
    public static void startActivityForResult(Activity activity, int direct){
        if(direct == LEFT){
            activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_in_reset);// 从左往右
        }else{
            activity.overridePendingTransition(R.anim.activity_out, R.anim.activity_out_reset);// 从右往左
        }
    }
}

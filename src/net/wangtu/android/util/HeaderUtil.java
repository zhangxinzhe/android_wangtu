package net.wangtu.android.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import net.wangtu.android.R;
import net.wangtu.android.common.statusbar.SystemStatusManager;
import net.wangtu.android.common.util.Util;

/**
 * Created by zhangxz on 2017/7/16.
 */

public class HeaderUtil {
    public static void initFraggmentStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //bar改变颜色
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            win.setAttributes(winParams);

            SystemStatusManager tintManager = new SystemStatusManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(0);//状态栏无背景

            ViewGroup container = (ViewGroup)activity.findViewById(R.id.home_container);
            //container.setPadding(0, Util.getStatusHeight(activity), 0, 0);
            View backgroundStatusView = new View(activity);
            backgroundStatusView.setBackgroundColor(activity.getResources().getColor(R.color.background_color));
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Util.getStatusHeight(activity));
            backgroundStatusView.setLayoutParams(layoutParams);
            container.addView(backgroundStatusView);
            backgroundStatusView.bringToFront();
        }
    }

    public static void initRewardPublishStatusBar(Activity activity,View container){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //bar改变颜色
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            win.setAttributes(winParams);

            SystemStatusManager tintManager = new SystemStatusManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(0);//状态栏无背景

            container.setPadding(0,Util.getStatusHeightIfNeed(activity),0,activity.getResources().getDimensionPixelSize(R.dimen.home_reward_publish_container_padding_bottom));
        }
    }
}

package net.wangtu.android;

import android.app.Application;

import org.xutils.x;

/**
 * Created by zhangxz on 2017/7/2.
 */

public class WangTuApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        x.Ext.init(this);
        // 设置是否输出Debug
        x.Ext.setDebug(false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

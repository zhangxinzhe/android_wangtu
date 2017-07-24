package net.wangtu.android;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.igexin.sdk.PushManager;

import net.wangtu.android.activity.LoginActivity;
import net.wangtu.android.activity.MyNoticeActivity;
import net.wangtu.android.common.compoment.getui.GeTuiIntentService;
import net.wangtu.android.common.compoment.getui.GeTuiService;

import org.xutils.x;

/**
 * Created by zhangxz on 2017/7/2.
 */

public class WangTuApplication extends Application {
    private static DemoHandler handler;
    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        x.Ext.init(this);
        // 设置是否输出Debug
        x.Ext.setDebug(false);

        //初始化个推
        PushManager.getInstance().initialize(this.getApplicationContext(), GeTuiService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GeTuiIntentService.class);
        if (handler == null) {
            handler = new DemoHandler();
        }
    }

    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public class DemoHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    break;
                default:
                    Intent intent = new Intent(WangTuApplication.this,MyNoticeActivity.class);
                    startActivity(intent);
            }
        }
    }
}

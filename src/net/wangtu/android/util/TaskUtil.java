package net.wangtu.android.util;

import android.app.Activity;
import android.content.Context;

import com.igexin.sdk.PushManager;

import net.wangtu.android.Constants;
import net.wangtu.android.common.compoment.getui.GeTuiIntentService;
import net.wangtu.android.common.compoment.getui.GeTuiService;
import net.wangtu.android.common.util.AppUtil;
import net.wangtu.android.common.util.CookieUtil;
import net.wangtu.android.common.util.DataUtil;
import net.wangtu.android.common.util.JsonUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.UrlUtil;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.util.http.HttpUtil;
import net.wangtu.android.component.VersionManager;

import org.json.JSONObject;

import java.net.UnknownHostException;

/**
 * Created by zhangxz on 2017/7/19.
 */

public class TaskUtil {
    public static boolean loading = false; // 未初始化
    public static  boolean hasInit = false;
    public static void startAync(){
        if(loading || hasInit){
            return;
        }
        loading = true;

        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                start(getServerConfig());
            }
        });
    }

    /**
     * 初始化配置信息
     *
     */
    public static void start(JSONObject config) {
        loading = false;
        hasInit = true;

        //信息不完整，需要重新刷新数据
        Boolean refreshAgain = config.optBoolean("refreshAgain");
        if(refreshAgain != null && refreshAgain){
            TaskUtil.hasInit = false;
        }

        //校验是否处于登录状态
        Boolean isLogin = config.optBoolean("isLogin");
        if(isLogin == null || !isLogin){
            LoginUtil.logout();
        }

        // 版本升级检测
        VersionManager.checkVersionAndInstall(config);
    }

    /**
     * 获取配置信息
     *
     * @return
     */
    public static JSONObject getServerConfig() {
        String value = null;
        String cookie = CookieUtil.getAllCookies(WangTuUtil.getDomain(),ContextUtil.getContext());
        String configUrl = ContextUtil.getContext().getConfigUrl();
        String pushToken = DataUtil.getData(Constants.GE_TUI_CLIENT_ID);
        String deviceId = AppUtil.getDeviceId(null);
        if (!ValidateUtil.isBlank(pushToken)) {
            configUrl = UrlUtil.addParams(configUrl, "pushToken=" + pushToken);
            configUrl = UrlUtil.addParams(configUrl, "clientId=" + pushToken);
        }
        if (!ValidateUtil.isBlank(deviceId)) {
            configUrl = UrlUtil.addParams(configUrl, "deviceId=" + deviceId);
        }
        configUrl = UrlUtil.addParams(configUrl, "deviceType=ANDROID");
        for (int i = 0; i < 3; i++) {
            try {
                value = HttpUtil.getString(configUrl, cookie);
                return JsonUtil.parseJson(value);
            } catch (Exception e) {
                if(!(e instanceof UnknownHostException)){
                    LogUtil.error(e, TaskUtil.class);
                }
            }
        }
        return null;
    }
}

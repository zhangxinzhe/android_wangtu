package net.wangtu.android.util;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

import net.wangtu.android.Constants;
import net.wangtu.android.common.util.DataUtil;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.component.VersionManager;

/**
 * 获取app上下文
 * @author Administrator
 *
 */
public abstract class ContextUtil extends Application {
	protected static ContextUtil instance;
	protected static Handler handler = null;
    public Boolean isDebug;
	
	public static void register(ContextUtil contextUtil) {
		instance = contextUtil;
		handler = new Handler();
    }
	
    /**
     * 主线程完成
     * @param r
     */
    public static void post(Runnable r){
    	if(Looper.myLooper() == handler.getLooper()){
    		r.run();
    		return;
    	}
    	handler.post(r);
    }
    
    /**
     * 是否是主线程
     * @return
     */
    public static boolean isMainThread(){
    	return Looper.myLooper() == Looper.getMainLooper();
    }
    
    /**
	 * 是否是开发模式
	 * @return
	 */
	public static boolean isDebug(){
		if(instance.isDebug == null){
			try {
                instance.isDebug = (instance.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)!=0;
			} catch (Exception e) {
                instance.isDebug = false;
			}
		}
		return instance.isDebug;
	}
	
	/**
	 * 程序是否运行在后台
	 * @return
	 */
	public static boolean isBackground() {
		ActivityManager am = (ActivityManager) instance.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(instance.getPackageName())) {
                return true;
            }
        }
        return false; 
    }

    public  static  ContextUtil  getContext(){
        return instance;
    }
    
    /**
     * 获取当前运行的activity
     * @return
     */
    public Activity getCurrentActivity(){
        return null;
    }

    String appIdentification = null;
    public String getAppIdentification(){
        if(appIdentification == null){
            appIdentification = Constants.APP_IDENTIFICATION + "_" + VersionManager.getLocalVersion() + "_" + VersionManager.getLocalInnerVersion();
            appIdentification += "_0";

            appIdentification += "-device_" + android.os.Build.VERSION.SDK_INT + "_" + android.os.Build.MODEL + "_" + Constants.APP_IDENTIFICATION ;
            appIdentification += "_phone";
        }
        return appIdentification;
    }

    public String getConfigUrl(){
        return WangTuUtil.getConfigUrl();
    }
}

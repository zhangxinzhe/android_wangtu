package net.wangtu.android.common.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import net.wangtu.android.util.ContextUtil;

/**
 * 启动第三方app
 * @author Administrator
 *
 */
public class AppUtil {
	/**
	 * 启动指定的应用
	 * @param context
	 * @param action
	 * @param category 
	 * @param params
	 */
	public static void start(Context context,String action,String category,Map<String,String> params){
		String log = "AppUtil.start_1\n";
		try {
			Intent intent = new Intent(action);
			log += "AppUtil.start_2\n";
			intent.addCategory(category);
			log += "AppUtil.start_3\n";
			PackageManager packageManager = context.getPackageManager();
			log += "AppUtil.start_4\n";
			List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0); 
			log += "AppUtil.start_5\n";
			boolean isIntentSafe = activities.size() > 0;
			log += "AppUtil.start_7_" + activities.size() + "\n";
			if(isIntentSafe){
				log += "AppUtil.start_8\n";
				if(params != null){
					log += "AppUtil.start_9\n";
					Set<String> keys = params.keySet();
					log += "AppUtil.start_10\n";
					for (String key : keys) {
						intent.putExtra(key,params.get(key));  
					}
					log += "AppUtil.start_11\n";
				}
				//最小化
				log += "AppUtil.start_12\n";
				context.startActivity(intent);  //启动这个请求Intent。
				log += "AppUtil.start_13\n";
			}
			log += "AppUtil.start_14\n";
		} catch (Exception e) {
			log += "AppUtil.start_15_" + e.getMessage() + "\n";
		}finally{
		}
		
		
	}
	
	/**
	 * 是否安装了指定的应用
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isInstall( Context context, String packageName ){
		final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ ){
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName)){
            	return true;
            }
        }
        return false;
	}
	
	/**
	 * 获取应用的版本信息
	 * @param packageName
	 * @return
	 */
	public static int getVersionCode(String packageName){
	    try {
	        // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
	    	Context context = ContextUtil.getContext();
	        return context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
	    } catch (NameNotFoundException e){

	    }
	    return 0;
	}
	
	/**
	 * 卸载应用
	 * @param context
	 * @param packageName
	 */
	public static void uninstall(Context context, String packageName){
		Uri packageURI = Uri.fromParts("package", packageName, null); 
		Intent intent = new Intent(Intent.ACTION_DELETE,packageURI); 
		context.startActivity(intent);
	}
	
	/**
	 * 获取设备唯一标识
	 * @return
	 */
	public static String getDeviceId(Context context){
		return android.os.Build.SERIAL;

	}
}

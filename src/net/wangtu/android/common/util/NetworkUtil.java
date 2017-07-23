package net.wangtu.android.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络检查工具
 * @author zhangxz
 *
 */
public class NetworkUtil {
	/**
	 * 检查网络状态
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	/**
	 * 检查网络连接类型
	 * @param context
	 * @return
	 */
	public static String getNetType(Context context){
        try {  
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
            NetworkInfo info = cm.getActiveNetworkInfo();  
            String typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE  
            if (typeName.equalsIgnoreCase("wifi")) {  
            } else {  
                typeName = info.getExtraInfo().toLowerCase();  
                // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap  
            }  
            return typeName;  
        } catch (Exception e) {  
            return null;  
        }  
    }  
	
	public static class NetworkUtilException extends Exception{
		private static final long serialVersionUID = 4991132399464631011L;

		public NetworkUtilException(){
			super("网络未打开，数据请求失败");
		}
	}
}

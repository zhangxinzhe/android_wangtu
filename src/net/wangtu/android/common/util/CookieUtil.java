package net.wangtu.android.common.util;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


public class CookieUtil {
	/**
	 * 仅做参考，需要的时候再扩展
	 * @param key
	 * @param value
	 * @param domain
	 * @param context
	 */
	public static void setCookie(String key,String value,String domain,Context context){
		if(domain.indexOf("http://") == 0){
			domain = domain.substring(7);
		}else if(domain.indexOf("https://") == 0){
			domain = domain.substring(8);
		}
		
	    CookieSyncManager.createInstance(context);
	    CookieManager cookieManager = CookieManager.getInstance();
		int maxAge = 365 * 24 * 60 * 60 * 1000;
		value = key + "=" + value + ";Max-Age=" + maxAge + ";Domain=" + domain + ";Path=/";
		cookieManager.setAcceptCookie(true);
		cookieManager.setCookie(domain, value);
		CookieSyncManager.getInstance().sync();
	}
	
	/**
	 * 获取cookie值
	 * @param key
	 * @return
	 */
	public static String getCookie(String key,String domain,Context context){
//		if(domain.indexOf("http") == 0){
//			domain = domain.substring(7);
//		}
		
	    CookieSyncManager.createInstance(context);
	    CookieManager cookieManager = CookieManager.getInstance();
	    String cookies = cookieManager.getCookie(domain);
	    if(cookies == null){
	    	return null;
	    }
	    key = key + "=";
	    String[] arrays = cookies.split(";");
	    for (String keyValue : arrays) {
			if(keyValue.trim().indexOf(key) == 0){
				return keyValue.split("=")[1];
			}
		}
	    return null;
	}
	
	public static String getAllCookies(String domain,Context context){
	    CookieSyncManager.createInstance(context);
	    CookieManager cookieManager = CookieManager.getInstance();
	    return cookieManager.getCookie(domain);
	}
}

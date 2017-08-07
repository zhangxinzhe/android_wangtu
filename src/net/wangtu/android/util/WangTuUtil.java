package net.wangtu.android.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import net.wangtu.android.Constants;
import net.wangtu.android.common.util.UrlUtil;

/**
 * 业务类
 */
public class WangTuUtil {
	public static String getDomain(){
		return "http://192.168.2.101";
		//return "http://192.168.11.61";
	}

	/**
	 * 获取地址
	 * @param url
	 * @return
	 */
	public static String getPage(String url){
		if(url == null){
			return url;
		}

		if(url.indexOf("http") == 0 || url.indexOf("file:///") == 0){
			return url;
		}

		return UrlUtil.createUrl(getDomain(), url);
	}

	/**
	 * 获取配置信息地址
	 * @return
	 */
	public static String getConfigUrl(){
		return getPage(Constants.API_MOBILE_CONFIG);
	}

	/**
	 * 获取app标识
	 * @return
	 */
	public static String getAppIdentification(Activity activity){
		return "";
	}

	/**
	 * 截取长度
	 * @param msg
	 * @param length
	 * @return
	 */
	public static String substring(String msg,int length){
		if(msg == null || length <= 0){
			return "";
		}

		if(msg.length() < length){
			return msg;
		}

		return msg.substring(0,length) + "......";
	}

}

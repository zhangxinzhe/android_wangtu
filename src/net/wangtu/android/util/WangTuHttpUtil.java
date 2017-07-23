package net.wangtu.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import net.wangtu.android.common.util.CookieUtil;
import net.wangtu.android.common.util.JsonUtil;
import net.wangtu.android.common.util.NetworkUtil;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.util.http.HttpUtilException;
import net.wangtu.android.common.util.http.SSLContextUtil;

/**
 * NetstudyHttp请求类
 * @author zhangxz
 *
 */
public class WangTuHttpUtil {
	/**
	 * 返回字符串
	 * @param uri
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getString(String uri,final Context context) throws Exception{
		//请求配置参数
		JSONObject params = new JSONObject();
		params.put("cookie", CookieUtil.getAllCookies(WangTuUtil.getDomain(), context));
		params.put("canReturnCookies", true);
		params.put("userAgent", WangTuUtil.getAppIdentification((Activity)context));
		
		//网络请求不通
		if(!NetworkUtil.isNetworkConnected(context)){
			throw new HttpUtilException(HttpUtilException.HttpUtilStatus_NoWifi + "报错！",HttpUtilException.HttpUtilStatus_NoWifi);
		}
		
		//请求
		String value = getString(uri, params);
		
		//更新cookie
		JSONObject cookieJson = params.optJSONObject("returnCookies");
		if(cookieJson != null && cookieJson.length() > 0){
			Iterator<String> keys = cookieJson.keys();
			while (keys.hasNext()) {
				String key = keys.next();
	            CookieUtil.setCookie(key, cookieJson.optString(key), uri, context);
	        }
		}
		return value;
	}
	
	/**
	 * 返回Json对象
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getJson(String url,final Context context) throws Exception{
		String c = getString(url,context);
		if(ValidateUtil.isBlank(c)){
			return null;
		}

		JSONObject json = JsonUtil.parseJson(c);
		if(json != null){
			if(json.optBoolean("isNeedUpdateAppVersion")){
				TaskUtil.hasInit = false;
				TaskUtil.startAync(context);
			}
		}
		return json;
	}

	/**
	 * 获取字符串
	 * @param uri
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String getString(String uri,JSONObject params) throws Exception{
		String cookie = (String)params.opt("cookie");
		String userAgent = (String)params.opt("userAgent");
		
		//是否返回后台的cookie
		Boolean canReturnCookies = (Boolean)params.opt("canReturnCookies");
		canReturnCookies = canReturnCookies == null ? false : canReturnCookies;
		
		String result = "";
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		try{
			URL url = new URL(uri);
			conn = (HttpURLConnection) url.openConnection();
			
			// 设置SSLSocketFoactory
			if (conn instanceof HttpsURLConnection) { // 是Https请求
			    SSLContext sslContext = SSLContextUtil.getSSLContext();
			    if (sslContext != null) {
			        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			        ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
			        ((HttpsURLConnection) conn).setHostnameVerifier(SSLContextUtil.hostnameVerifier);
			    }
			}
			
			if(!ValidateUtil.isBlank(cookie)){
				conn.setRequestProperty("Cookie", cookie); 
			}
			conn.setUseCaches(false);
			if(!ValidateUtil.isBlank(userAgent)){
				conn.setRequestProperty("User-Agent","Android " + userAgent);
			}
			conn.connect();
			
			//验证连接状态
			int responseCode = conn.getResponseCode();
			
			//返回cookie
	        if(canReturnCookies){
	        	Map<String, String> returnCookies = new HashMap<String, String>();
	        	params.put("returnCookies", returnCookies);
	        	// 取到所用的Cookie
	        	Map<String, List<String>> headers = conn.getHeaderFields();
	        	if(headers != null && headers.size() > 0){
	        		List<String> cookies = headers.get("Set-Cookie");
	        		if(cookies != null && cookies.size() > 0){
	        			String[] c = null;
	        			int index = -1;
	        			for (String keyValue : cookies) {
	        				index =  keyValue.indexOf(";");
	        				if(index > -1){
	        					keyValue = keyValue.substring(0,index);
	        					c = keyValue.split("=");
	        					if(c != null && c.length > 1){
	        						returnCookies.put(c[0].trim(), c[1].trim());
	        					}
	        				}
	        			}
	        		}
	        	}
	        }
			
			//手动控制302跳转
			params.put("statusCode", responseCode);
			//大于400抛错
			if(responseCode >= 400){
				throw new HttpUtilException(responseCode + "报错！",responseCode);
			}

			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);// 读字符串用的
			String inputLine = null;
			result = reader.readLine();
	        while (((inputLine = reader.readLine()) != null)) {
	            result += "\n" + inputLine;
	        }
		}catch(Exception e){
			throw e;
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
			if(conn != null){
				conn.disconnect();
			}
		}
		
		return result;
	}
}

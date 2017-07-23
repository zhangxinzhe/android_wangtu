package net.wangtu.android.common.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UrlUtil {
	public static final String HTTP = "http://";
	public static final String HTTPS = "https://";
	public static final String SLASH = "/";

	/**
	 * 格式:http://www.baidu.com
	 * @param url
	 * @param isHttps 如果没有添加http头，默认是https还是http
	 * @return
	 */
	public static String getDomain(String url,boolean isHttps){
		if(ValidateUtil.isBlank(url)){
			return "";
		}
		
		url = url.toLowerCase(Locale.getDefault());
		
		//添加http头
		if(!url.startsWith(HTTP) && !url.startsWith(HTTPS)){
			if(isHttps){
				url = HTTPS + url;
			}else{
				url = HTTP + url;
			}
		}
		
		//获取第一个“/”的地址
		int i = url.indexOf(SLASH,HTTPS.length());
		if(i > 0){
			url = url.substring(0, i);
		}
		
		//?去掉参数
		i = url.indexOf("?");
		if(i > 0){
			url = url.substring(0, i);
		}
		
		return url;
	}
	
	/**
	 * 格式:http://www.baidu.com
	 * @param url
	 * @return
	 */
	public static String getDomain(String url){
		return getDomain(url, false);
	}
	
	/**
	 * url追加参数
	 * @param url
	 * @param params
	 * @return
	 */
	public static String addParams(String url,String params){
		if(url.indexOf("?") >= 0){
			return url + "&" + params;
		}else{
			return url + "?" + params;
		}
	}
	
	/**
	 * 创建链接 domain + page
	 * @param domain
	 * @param page
	 * @return
	 */
	public static String createUrl(String domain,String page){
		String url = getDomain(domain);
		if(page == null){
			return url;
		}
		if(!page.startsWith(SLASH)){
			url += SLASH;
		}
		url += page;
		return url;
	}
	
	/**
	 * 获取url里面的参数
	 * @param url
	 * @return
	 */
	public static Map<String, String> getParams(String url){
		Map<String, String> map = new HashMap<String, String>();
		if(!ValidateUtil.isBlank(url)){
			int index = url.indexOf("?");
			if(index > 0){
				url = url.substring(index + 1);
				String[] array = url.split("&");
				if(array != null && array.length > 0){
					String[] keyValue = null;
					for (String item : array) {
						if(item == null){
							continue;
						}
						keyValue = item.split("=");
						if(keyValue.length >= 2){
							map.put(keyValue[0], keyValue[1]);
						}
					}
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 获取相对地址
	 * @param url
	 * @return
	 */
	public static  String getRelativeUrl(String url){
		if(ValidateUtil.isBlank(url)){
			return url;
		}
		
		if(url.indexOf("http") == -1){
			int index = url.indexOf("?");
			if(index == -1){
				return url;
			}else{
				return url.substring(0, index);
			}
		}
		
		try {
			return new URL(url).getPath();
		} catch (MalformedURLException e) {
			return url;
		}
	}
	
	/**
	 * 获取文件扩展名
	 * @return
	 */
	public static String getFileExt(String url){
		//去除域名和后缀
		url = getRelativeUrl(url);
		
		if(ValidateUtil.isBlank(url)){
			return url;
		}
		
		//获取扩展名
		int beginIndex = url.indexOf(".");
        if (beginIndex >= 0) {
            url = url.substring(beginIndex + 1);
        }
		
		return url;
	}
}

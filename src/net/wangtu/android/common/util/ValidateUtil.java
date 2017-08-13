package net.wangtu.android.common.util;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;

public class ValidateUtil {
	
	/**
	 * 验证字符串是否为空
	 * @param value
	 * @return
	 */
	public static boolean isBlank(String value){
		if(value == null || value.trim().length() == 0){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 数组是否为空
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(List<?> collection){
		if(collection == null){
			return true;
		}
		
		if(collection.size() <= 0){
			return true;
		}
		
		return false;
	}

	/**
	 * map是否为空
	 * @param map
	 * @return
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		if (map == null) {
			return true;
		}

		if (map.size() <= 0) {
			return true;
		}

		return false;
	}

	/**
	 * Object[]是否为空
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Object[] array){
		if(array == null){
			return true;
		}

		if(array.length <= 0){
			return true;
		}

		return false;
	}
	
	/**
	 * JsonArray是否为空
	 * @return
	 */
	public static boolean isEmpty(JSONArray array){
		if(array == null){
			return true;
		}
		
		if(array.length() <= 0){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 验证是否是url
	 * @param url
	 * @return
	 */
	public static boolean isUrl(String url){
		if(isBlank(url)){
			return false;
		}
		
		final String REG_URL = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$"; 

		if(!url.matches(REG_URL)){
			return false;
		}
		
		return true;
	}
	
	public static boolean contain(String s,String[] args){
		if(args == null || s == null){
			return false;
		}
		
		for (String arg : args) {
			if(s.indexOf(arg) >= 0){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 是否是數字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}

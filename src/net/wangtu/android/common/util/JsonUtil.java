package net.wangtu.android.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Json转换成
 * @author zhangxz
 *
 */
public class JsonUtil {
	/**
	 * 将字符串转换成json
	 * @param value
	 * @return
	 * @throws JSONException 
	 */
	public static JSONObject parseJson(String value) throws JSONException{
		if(ValidateUtil.isBlank(value)){
			return null;
		}
		
		JSONTokener jsonParser = new JSONTokener(value);
		return (JSONObject) jsonParser.nextValue(); 
	}
	
	/**
	 * 转换成map
	 * @param value
	 * @return
	 * @throws JSONException 
	 */
	public static Map<String, String> parseToMap(String value) throws JSONException{
		if (ValidateUtil.isBlank(value)) {
			return null;
		}

		return JsonUtil.parseToMap(new JSONObject(value));
	}
	
	public static Map<String, String> parseToMap(JSONObject valueJson) throws JSONException{
		Iterator<String> keyIter = valueJson.keys();
		String key;
		Map<String, String> valueMap = new HashMap<String, String>();
		while (keyIter.hasNext()) {
			key = keyIter.next();
			valueMap.put(key, valueJson.getString(key));
		}
		return valueMap;
	}
}

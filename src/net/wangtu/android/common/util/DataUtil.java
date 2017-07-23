package net.wangtu.android.common.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import net.wangtu.android.util.ContextUtil;

public class DataUtil {
	public static final String USER_DATA = "user_data";//用户数据
	
	/**
	 * 获取数据
	 * @param key
	 * @return
	 */
	public static String getData(String key){
		SharedPreferences sp = ContextUtil.getContext().getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}
	
	/**
	 * 保存
	 * @param key
	 * @param data
	 */
	public static void setData(String key,String data){
		SharedPreferences sp =ContextUtil.getContext().getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
		Editor editor=sp.edit();
		editor.putString(key, data);
		editor.commit();
	}
}

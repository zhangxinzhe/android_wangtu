package net.wangtu.android.common.util;

import java.util.List;

/**
 * Json转换成
 * @author zhangxz
 *
 */
public class StringUtil {
	
	/**
	 * 将数组转成字符串
	 * @param array
	 * @param separator
	 * @return
	 */
	public static String join(List<String> array,String separator){
		String content = "";

		if(ValidateUtil.isEmpty(array)){
			return content;
		}
		
		for (String string : array) {
			content += string + separator;
		}
		return content.substring(0, content.length() - separator.length());
	}
}

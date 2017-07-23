package net.wangtu.android.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author zhangxz
 *
 */
public class PropertiesUtil {
	/**
	 * 获取属性
	 * @param key
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String getProperty(String key,String path) throws FileNotFoundException, IOException{
		Properties pro = getProperty(path);
		if(pro == null){
			return null;
		}
		return pro.getProperty(key);
	}
	
	/**
	 * 加载Properties文件
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Properties getProperty(String path) throws FileNotFoundException, IOException{
		Properties pro = new Properties();
		pro.load(PropertiesUtil.class.getResourceAsStream(path));
		return pro;
	}
}

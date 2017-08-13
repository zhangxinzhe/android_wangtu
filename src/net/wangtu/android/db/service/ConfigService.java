package net.wangtu.android.db.service;


import net.wangtu.android.db.dao.ConfigDao;

public class ConfigService{
	private ConfigDao configDao = new ConfigDao();
	
	/**
	 * 获取配置信息
	 * @param key
	 * @return
	 */
	public String getConfig(String key){
		return configDao.getConfig(key);
	}
	
	/**
	 * 更新配置信息
	 * @param value
	 * @param key
	 */
	public void updateConfig(String value,String key){
		if(configDao.hasConfig(key)){
			configDao.updateConfig(value, key);
		}else{
			configDao.addConfig(value, key);
		}
	}
}

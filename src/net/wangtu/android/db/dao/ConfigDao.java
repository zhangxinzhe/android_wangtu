package net.wangtu.android.db.dao;

import java.sql.SQLException;

import android.database.Cursor;

import net.wangtu.android.db.base.BasicDao;
import net.wangtu.android.db.base.callback.SingleRowMapper;

public class ConfigDao extends BasicDao {
	private final String get_config = "SELECT VALUE FROM T_CONFIG WHERE KEY = ?";
	private final String has_config = "SELECT COUNT(1) FROM T_CONFIG WHERE KEY =  ?";
	private final String update_config = "UPDATE T_CONFIG SET VALUE = ? WHERE KEY = ?";
	private final String insert_config = "INSERT INTO T_CONFIG(VALUE,KEY) VALUES(?,?)";
	
	/**
	 * 获取配置信息
	 * @param key
	 * @return
	 */
	public String getConfig(String key){
		return query(get_config, new String[]{ key }, new SingleRowMapper<String>() {
			@Override
			public String mapRow(Cursor paramCursor) throws SQLException{
				return paramCursor.getString(0);
			}
		});
	}
	
	/**
	 * 更新配置信息
	 * @param value
	 * @param key
	 */
	public void updateConfig(String value,String key){
		execSQL(update_config, new String[]{value,key});
	}
	
	/**
	 * 添加配置信息
	 * @param value
	 * @param key
	 */
	public void addConfig(String value,String key){
		execSQL(insert_config, new String[]{value,key});
	}
	
	/**
	 * 是否存在此参数
	 * @param key
	 * @return
	 */
	public boolean hasConfig(String key){
		return query(has_config, new String[]{ key }, new SingleRowMapper<Integer>(){
			public Integer mapRow(Cursor paramCursor) throws SQLException{
				return paramCursor.getInt(0);
			}
		}) > 0;
	}
}

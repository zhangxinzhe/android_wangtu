package net.wangtu.android.db.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import android.database.Cursor;

import net.wangtu.android.db.base.BasicDao;
import net.wangtu.android.db.base.callback.MultiRowMapper;
import net.wangtu.android.db.base.callback.SingleRowMapper;

public class CourseSearchLogDao extends BasicDao {
	private final String get_course_search_log = "SELECT CONTENT FROM T_COURSE_SEARCH_LOG ORDER BY TIME DESC";
	private final String get_course_search_log_num = "SELECT COUNT(1) FROM T_COURSE_SEARCH_LOG WHERE CONTENT = ?";
	private final String update_course_search_log = "UPDATE T_COURSE_SEARCH_LOG SET TIME = ? WHERE CONTENT = ?";
	private final String insert_course_search_log = "INSERT INTO T_COURSE_SEARCH_LOG(TIME,CONTENT) VALUES(?,?)";
	private final String clear_all_course_search_log = "DELETE FROM T_COURSE_SEARCH_LOG";
	private final String clear_course_search_log = "DELETE FROM T_COURSE_SEARCH_LOG WHERE ID IN(SELECT ID FROM T_COURSE_SEARCH_LOG ORDER BY TIME DESC LIMIT ?,100)";
	
	/**
	 * 获取课程查询日志
	 * @return
	 */
	public List<String> getCourseSearchLog(){
		return query(get_course_search_log, new String[]{}, new MultiRowMapper<String>() {
			public String mapRow(Cursor paramCursor, int paramInt) throws SQLException{
				return paramCursor.getString(0);
			}
		});
	}
	
	/**
	 * 添加课程查询日志
	 * @param content
	 * @param time
	 */
	public void addCourseSearchLog(String content,Date time){
		execSQL(insert_course_search_log, new Object[]{time,content});
	}
	
	/**
	 * 更新课程查询日志
	 * @param content
	 * @param time
	 */
	public void updateCourseSearchLog(String content,Date time){
		execSQL(update_course_search_log, new Object[]{time,content});
	}
	
	/**
	 * 获取数量
	 * @param content
	 * @return
	 */
	public int getgetCourseSearchLogNum(String content){
		return query(get_course_search_log_num, new String[]{content}, new SingleRowMapper<Integer>(){
			public Integer mapRow(Cursor paramCursor) throws SQLException{
				return paramCursor.getInt(0);
			}
		});
	}
	
	/**
	 * 清除全部历史记录
	 */
	public void clearAllCourseSearchLog(){
		update(clear_all_course_search_log);
	}
	
	/**
	 * 倒序清除超过范围之外的记录
	 * @param rowNum 
	 */
	public void clearCourseSearchLog(int rowNum){
		update(clear_course_search_log, new Object[]{rowNum});
	}
}

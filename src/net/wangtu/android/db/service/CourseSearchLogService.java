package net.wangtu.android.db.service;

import net.wangtu.android.db.dao.CourseSearchLogDao;

import java.util.Date;
import java.util.List;

public class CourseSearchLogService{
	private CourseSearchLogDao courseSearchLogDao = new CourseSearchLogDao();
	
	/**
	 * 获取课程查询日志
	 * @return
	 */
	public List<String> getCourseSearchLog(){
		return courseSearchLogDao.getCourseSearchLog();
	}
	
	/**
	 * 添加或更新课程查询日志
	 * @param content
	 * @param time
	 */
	public void updateOrAddCourseSearchLog(String content,Date time){
		if(courseSearchLogDao.getgetCourseSearchLogNum(content) > 0){
			courseSearchLogDao.updateCourseSearchLog(content, time);
		}else{
			courseSearchLogDao.addCourseSearchLog(content, time);
		}
	}
	
	/**
	 * 清除全部历史记录
	 */
	public void clearAllCourseSearchLog(){
		courseSearchLogDao.clearAllCourseSearchLog();
	}
	
	/**
	 * 倒序清除超过范围之外的记录
	 * @param rowNum 
	 */
	public void clearCourseSearchLog(int rowNum){
		courseSearchLogDao.clearCourseSearchLog(rowNum);
	}
}

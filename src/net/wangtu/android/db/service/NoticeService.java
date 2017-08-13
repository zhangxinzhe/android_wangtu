package net.wangtu.android.db.service;

import net.wangtu.android.db.dao.NoticeDao;

import java.util.List;

public class NoticeService{
	private NoticeDao noticeDao = new NoticeDao();
	
	/**
	 * 添加消息记录
	 * @param noticeId
	 * @param noticeType
	 */
	public void addNotice(long noticeId,int noticeType){
		noticeDao.addNotice(noticeId, noticeType);
	}
	
	/**
	 * 更新消息（仅用于系统通知和课程通知）
	 * @param noticeId
	 * @param noticeType
	 */
	public void updateNotice(long noticeId,int noticeType){
		noticeDao.updateNotice(noticeId, noticeType);
	}
	
	/**
	 * 获取与该用户的通话记录
	 */
	public List<Long> getNotice(String[] noticeIds,int noticeType){
		return noticeDao.getNotice(noticeIds, noticeType);
	}
	
	/**
	 * 获取与该用户的通话记录（仅用于系统通知和课程通知）
	 * @param noticeType
	 */
	public Long getNotice(String noticeType){
		return noticeDao.getNotice(noticeType);
	}
}

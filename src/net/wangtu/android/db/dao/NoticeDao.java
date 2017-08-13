package net.wangtu.android.db.dao;

import java.sql.SQLException;
import java.util.List;

import android.database.Cursor;

import net.wangtu.android.db.base.BasicDao;
import net.wangtu.android.db.base.callback.MultiRowMapper;
import net.wangtu.android.db.base.callback.SingleRowMapper;

public class NoticeDao extends BasicDao {
	private final String get_notice_id = "SELECT NOTICE_ID FROM T_NOTICE WHERE NOTICE_TYPE = ?";
	private final String get_notice_ids = "SELECT NOTICE_ID FROM T_NOTICE WHERE NOTICE_TYPE = ? AND NOTICE_ID IN";
	private final String update_notice_ids = "UPDATE T_NOTICE SET NOTICE_ID = ? WHERE NOTICE_TYPE = ?";
	private final String insert_notice_ids = "INSERT INTO T_NOTICE(NOTICE_ID,NOTICE_TYPE) VALUES(?,?)";

	/**
	 * 添加消息记录
	 * @param noticeId
	 * @param noticeType
	 */
	public void addNotice(long noticeId,int noticeType){
		execSQL(insert_notice_ids, new Object[]{noticeId,noticeType});
	}
	
	/**
	 * 更新消息（仅用于系统通知和课程通知）
	 * @param noticeId
	 * @param noticeType
	 */
	public void updateNotice(long noticeId,int noticeType){
		update(update_notice_ids, new Object[]{noticeId,noticeType});
	}
	
	/**
	 * 获取与该用户的通话记录
	 */
	public List<Long> getNotice(String[] noticeIds,int noticeType){
		return queryForInSQL(get_notice_ids, new String[]{noticeType + ""}, noticeIds, null, new MultiRowMapper<Long>() {
			public Long mapRow(Cursor paramCursor, int paramInt) throws SQLException{
				return paramCursor.getLong(0);
			}
		});
	}
	
	/**
	 * 获取与该用户的通话记录（仅用于系统通知和课程通知）
	 * @param noticeType
	 */
	public Long getNotice(String noticeType){
		return query(get_notice_id, new String[]{ noticeType }, new SingleRowMapper<Long>() {
			@Override
			public Long mapRow(Cursor paramCursor) throws SQLException{
				return paramCursor.getLong(0);
			}
		});
	}
}

package net.wangtu.android.common.util.permision;

import java.util.HashMap;
import java.util.Map;

public class PermisionValue {
	
	public static final Map<Integer, String> permisionMap = new HashMap<Integer, String>();
	
	public static final int OP_GRANTED = 0X01;//许可
	public static final int OP_NO_GRANTED = 0X02;//禁止
	public static final int OP_UNSURE_GRANTED = 0X03;//需要确认
	
	public static final int OP_READ_CONTACTS = 4;
	public static final int OP_WRITE_CONTACTS = 5;
	public static final int OP_READ_CALL_LOG = 6;
	public static final int OP_WRITE_CALL_LOG = 7;
	public static final int OP_CALL_PHONE = 13;
	public static final int OP_READ_SMS = 14;
	public static final int OP_WRITE_SMS = 15;
	public static final int OP_RECEIVE_SMS = 16;
	public static final int OP_SEND_SMS = 20;
	public static final int OP_SYSTEM_ALERT_WINDOW = 24;
	public static final int OP_CAMERA = 26;
	public static final int OP_RECORD_AUDIO = 27;
	public static final int OP_PLAY_AUDIO = 28;
	public static final int OP_WAKE_LOCK = 40;
	
	
	static{
		permisionMap.put(OP_GRANTED, "");
		permisionMap.put(OP_WRITE_SMS, "编辑短信权限");
		permisionMap.put(OP_RECEIVE_SMS, "接收短信权限");
		permisionMap.put(OP_READ_SMS, "读取短信权限");
		permisionMap.put(OP_SEND_SMS, "发送短信权限");
		permisionMap.put(OP_CAMERA, "拍照权限");
		permisionMap.put(OP_SYSTEM_ALERT_WINDOW, "悬浮窗权限");
		permisionMap.put(OP_READ_CONTACTS, "读取联系人权限");
		permisionMap.put(OP_WRITE_CONTACTS, "添加联系人权限");
		permisionMap.put(OP_CALL_PHONE, "拨号权限");
		permisionMap.put(OP_RECORD_AUDIO, "录音权限");
		permisionMap.put(OP_READ_CALL_LOG, "读取通话记录权限");
		permisionMap.put(OP_WRITE_CALL_LOG, "修改通话记录权限");
		permisionMap.put(OP_WAKE_LOCK, "休眠状态下可以保证sdk必要的任务执行");
	}
	
}

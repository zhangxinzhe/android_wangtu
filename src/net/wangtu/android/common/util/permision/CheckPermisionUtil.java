package net.wangtu.android.common.util.permision;

import java.lang.reflect.Method;

import net.wangtu.android.common.util.ReflectUtil;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;

public class CheckPermisionUtil {

	private static final String[] type = { "int", "int", "string" };

	/**
	 * 反射方法获取返回值结果 public int checkOp(int op, int uid, String packageName)
	 */
	@SuppressLint("InlinedApi")
	private static int getPermisionResult(Context context, int permValue,
			String methodName) {
		int check = 0;
		try {
			AppOpsManager aom = (AppOpsManager) context
					.getSystemService(Context.APP_OPS_SERVICE);
			Class<? extends AppOpsManager> c = aom.getClass();
			Class<?> paramsTypes[] = ReflectUtil.getMethodClass(type);
			Method method = c.getMethod(methodName, paramsTypes);
			String[] params = { "" + permValue, "" + Process.myUid(),
					context.getPackageName() };
			Object arglist[] = ReflectUtil.getMethodObject(type, params);
			check = (Integer) method.invoke(aom, arglist);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("TAG", "--->getPermisionResult exception");
		}
		return check;
	}

	/**
	 * 判断某个权限是否被授权
	 */
	@SuppressLint("InlinedApi")
	public static boolean isPermisionGranted(Context context, int permValue) {
		boolean result = true;
		switch (getPermisionResult(context, permValue, "checkOp")) {
		case AppOpsManager.MODE_IGNORED:// 被禁止掉
			result = false;
			break;
		default:
			result = true;// 允许和需要确认不需要做什么操作，故都返回true
			break;
		}
		return result;
	}

	/**
	 * 打开系统安全设置界面
	 */
	public static void openSecurityPager(Context context) {
		openSystemPager(context, Settings.ACTION_SECURITY_SETTINGS);
	}

	/**
	 * 打开设置界面
	 */
	public static void openSettingPager(Context context) {
		openSystemPager(context, Settings.ACTION_SETTINGS);
	}

	/**
	 * 打开系统界面
	 */
	public static void openSystemPager(Context context, String action) {
		Intent intent = new Intent();
		intent.setAction(action);
		context.startActivity(intent);
	}

	/**
	 * 检测短信编写权限是否被许可
	 */
	public static boolean isSmsWritePermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_WRITE_SMS);
	}

	/**
	 * 检测短信接收权限是否被许可
	 */
	public static boolean isSmsReceivePermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_RECEIVE_SMS);
	}

	/**
	 * 检测短信读取权限是否被许可
	 */
	public static boolean isSmsReadPermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_READ_SMS);
	}

	/**
	 * 检测短信发送是否被许可
	 */
	public static boolean isSmsSendPermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_SEND_SMS);
	}

	/**
	 * 检测打开相机权限是否被许可
	 */
	public static boolean isCameraPermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_CAMERA);
	}

	/**
	 * 检测悬浮窗权限是否被许可
	 */
	public static boolean isFloatWindowPermisionGranted(Context context) {
		return isPermisionGranted(context,
				PermisionValue.OP_SYSTEM_ALERT_WINDOW);
	}

	/**
	 * 检测读取联系人权限是否被许可
	 */
	public static boolean isReadContactPermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_READ_CONTACTS);
	}

	/**
	 * 检测添加联系人权限是否被许可
	 */
	public static boolean isWriteContactPermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_WRITE_CONTACTS);
	}

	/**
	 * 检测直接拨号权限是否被许可(无界面拨号)
	 */
	public static boolean isCallPhonePermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_CALL_PHONE);
	}

	/**
	 * 检测录制音频权限是否被许可
	 */
	public static boolean isRecordAudioPermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_RECORD_AUDIO);
	}

	/**
	 * 检测读取通话记录权限是否被许可
	 */
	public static boolean isReadCallLogPermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_READ_CALL_LOG);
	}

	/**
	 * 检测编写通话记录权限是否被许可
	 */
	public static boolean isWriteCallLogPermisionGranted(Context context) {
		return isPermisionGranted(context, PermisionValue.OP_WRITE_CALL_LOG);
	}

}

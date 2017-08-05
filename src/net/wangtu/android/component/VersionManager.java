package net.wangtu.android.component;

import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;

import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.util.http.HttpUtil;
import net.wangtu.android.common.view.dialog.AlertView;
import net.wangtu.android.common.view.dialog.BoxView;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.component.install.InstallManager;
import net.wangtu.android.util.ContextUtil;
import net.wangtu.android.util.LogUtil;

/**
 * 版本控制
 * 
 * @author zhangxz
 * 
 */
public class VersionManager {
	private static boolean showing;
	private static boolean notUpdate;
	private static boolean updating;

	/**
	 * 验证版本号，并安装apk
	 * 
	 * @param context
	 */
	public static void checkVersionAndInstall() {
		if (forgiveUpdate()) {
			return;
		}

		JSONObject json = null;
		try {
			json = HttpUtil.getJson(ContextUtil.getContext().getConfigUrl());
		} catch (Exception e) {
			if(!(e instanceof UnknownHostException)){
				LogUtil.error(e, VersionManager.class);
			}
			return;
		}
		VersionManager.checkVersionAndInstall(json);
	}

	/**
	 * 验证版本号，并安装apk
	 * 
	 * @param context
	 */
	public synchronized static void checkVersionAndInstall(final JSONObject json) {
		try {
			if (forgiveUpdate()) {
				return;
			}

			if (json == null) {
				return;
			}
			if (json.isNull("android")) {
				return;
			}

			boolean isUpdate = false;
			int newInnerVersion = json.getJSONObject("android").optInt(
					"innerVersion", -1);
			int nowInnerVersion = VersionManager.getLocalInnerVersion();
			final String nowVersion = VersionManager.getLocalVersion();
			String newVersion = json.getJSONObject("android").optString(
					"version");
			// 优先更具内部版本号更新
			if (newInnerVersion > 0) {
				if (nowInnerVersion < newInnerVersion) {
					isUpdate = true;
				}
			}
			// 兼容老的更新方式
			else if (!nowVersion.equals(newVersion)) {
				isUpdate = true;
			}

			// 更新
			if (isUpdate) {
				final String apkUrl = json.getJSONObject("android").optString(
						"name");
				final String updateInfo = json.getJSONObject("android")
						.optString("info");
				final String forceVersion = json.getJSONObject("android")
						.optString("forceVersion");
				ContextUtil.post(new Runnable() {
					@Override
					public void run() {
						if (showing) {
							return;
						}
						showing = true;

						// 提示信息
						String msg = "发现新版本，是否立即更新？";
						if (!ValidateUtil.isBlank(updateInfo)) {
							msg = updateInfo;
						}

						// activity已被销毁
						Activity context = ContextUtil.getContext().getCurrentActivity();
						if (context == null || context.isFinishing()) {
							return;
						}

						// 本地版本号 < 强制更新最低版本号-->强制更新
						Boolean versionForceUpdate = false;
						if (!ValidateUtil.isBlank(forceVersion)
								&& !ValidateUtil.isBlank(nowVersion)) {
							if (Integer.parseInt(nowVersion.replace(".", "")) < Integer
									.parseInt(forceVersion.replace(".", ""))) {
								versionForceUpdate = true;
							}
						}

						// 强制更新
						if (true == versionForceUpdate) {
							AlertView alert = new AlertView(context);
							alert.setTitleMsg("检测版本更新");
							alert.setContentMsg(msg);
							alert.setOkBtnName("现在更新");
							alert.setOkBtnListener(new CheckVersionAndInstall(
									context, apkUrl, alert, versionForceUpdate));
							alert.show();
						} else {
							// 非强制更新
							final ConfirmView confirm = new ConfirmView(context);
							confirm.setTitleMsg("检测版本更新");
							confirm.setContentMsg(msg);
							confirm.setOkBtnName("现在更新");
							confirm.setCancelBtnName("先不更新");
							confirm.setOkBtnListener(new CheckVersionAndInstall(
									context, apkUrl, confirm,
									versionForceUpdate));
							confirm.setCancelBtnListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									notUpdate = true;
									confirm.dismiss();
									showing = false;
								}
							});
							confirm.show();
						}

					}
				});
			}
		} catch (JSONException e) {
			LogUtil.error(e, VersionManager.class);
		}
	}

	private static class CheckVersionAndInstall implements View.OnClickListener {
		private BoxView dialog;
		private Context context;
		private String apkUrl;
		private Boolean versionForceUpdate;

		public CheckVersionAndInstall(Context context, String apkUrl,
				BoxView dialog, Boolean versionForceUpdate) {
			this.dialog = dialog;
			this.context = context;
			this.apkUrl = apkUrl;
			this.versionForceUpdate = versionForceUpdate;
		}

		@Override
		public void onClick(View v) {
			if (versionForceUpdate) {
				new InstallManager(context, apkUrl).instalAsy();
			} else {
				new InstallManager(context, apkUrl).instalTask();
			}
			dialog.dismiss();
			showing = false;
		}
	}

	/**
	 * 放弃更新
	 * 
	 * @return
	 */
	private static boolean forgiveUpdate() {
		if (notUpdate) {
			return true;
		}

		if (showing) {
			return true;
		}

		if (updating) {
			return true;
		}

		return false;
	}

	/**
	 * 获取本地版本
	 * 
	 * @return
	 */
	public static String getLocalVersion() {
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionName
			Context context = ContextUtil.getContext();
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			LogUtil.error(e, VersionManager.class);
		}
		return null;
	}

	/**
	 * 获取本地内部版本
	 * 
	 * @return
	 */
	public static int getLocalInnerVersion() {
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			Context context = ContextUtil.getContext();
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			LogUtil.error(e, VersionManager.class);
		}
		return -1;
	}

	/**
	 * 设置是否不检测版本更新
	 * 
	 * @param isUpdate
	 */
	public static void setNotUpdate(boolean isUpdate) {
		notUpdate = isUpdate;
	}

	/**
	 * 设置是否正在更新
	 * 
	 * @param isUpdating
	 */
	public static void setUpdating(boolean isUpdating) {
		updating = isUpdating;
	}
}

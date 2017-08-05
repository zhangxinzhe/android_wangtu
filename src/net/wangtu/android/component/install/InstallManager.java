package net.wangtu.android.component.install;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.component.VersionManager;
import net.wangtu.android.util.ContextUtil;

/**
 * 下载管理
 * 
 * @author zhangxz
 * 
 */
public class InstallManager {
	private Context context;
	private String url;
	private boolean autoHide;
	private static int autoDowloadNum = 0;

	public InstallManager(Context context, String url) {
		this.context = context;
		this.url = url;
	}

	public InstallManager(Context context, String url, boolean autoHide) {
		this.context = context;
		this.url = url;
		this.autoHide = autoHide;
	}

	/**
	 * 异步下载安装
	 */
	public void instalAsy() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				instal(true);
			}
		}).start();
	}

	/**
	 * 后台下载安装
	 */
	public void instalTask() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				instal(false);
			}
		}).start();
	}

	/**
	 * 同步下载安装
	 */
	public void instal(final Boolean isShowProgressBar) {
		// 设置正在在更新
		VersionManager.setUpdating(true);

		// 下载
		DownLoad.downLoadApk(url, new DownLoad.DownLoadEvent() {
			private ProgressView progressView;

			@Override
			public void start() {
				ContextUtil.post(new Runnable() {
					@Override
					public void run() {
						if (isShowProgressBar) {
							progressView = new ProgressView(context);
							progressView.show();
							progressView.updateProgress(0);
						}
					}
				});
			}

			@Override
			public void loading(final int progress) {
				ContextUtil.post(new Runnable() {
					@Override
					public void run() {
						if (isShowProgressBar) {
							progressView.updateProgress(progress);
						}
					}
				});
			}

			@Override
			public void success(String path) {
				installApk(path);
				if (autoHide && isShowProgressBar) {
					progressView.dismiss();
				}
				VersionManager.setUpdating(false);
			}

			@Override
			public void error(int status, final String msg) {
				ContextUtil.post(new Runnable() {
					@Override
					public void run() {
						if (isShowProgressBar) {
							if(progressView != null){
								progressView.dismiss();
							}
							final ConfirmView alert = new ConfirmView(context);
							alert.setContentMsg(msg);
							alert.setOkBtnName("重试");
							alert.setOkBtnListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									InstallManager.this.instalAsy();
									alert.dismiss();
								}
							});
							alert.show();
						} else {
							// 网络异常自动重新下载，最多3次
							if (autoDowloadNum < 3) {
								InstallManager.this.instalTask();
								autoDowloadNum++;
							} else {
								final ConfirmView alert = new ConfirmView(context);
								alert.setContentMsg("网络异常，更新失败！");
								alert.setOkBtnName("重试");
								alert.setCancelBtnName("放弃");
								alert.setOkBtnListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										autoDowloadNum = 0;
										InstallManager.this.instalTask();
										alert.dismiss();
									}
								});
								alert.setCancelBtnListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										VersionManager.setNotUpdate(true);
										VersionManager.setUpdating(false);
										alert.dismiss();
									}
								});
								alert.show();
							}
						}
					}
				});

			}
		});
	}

	/**
	 * 安装APK
	 */
	private void installApk(String path) {
		File apkfile = new File(path);
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 卸载应用
	 * 
	 * @param apk
	 */
	public static void uninstall(Context context, String apk) {
		Intent intent = new Intent();
		Uri uri = Uri.parse("package:" + apk);// 获取删除包名的URI
		intent.setAction(Intent.ACTION_DELETE);// 设置我们要执行的卸载动作
		intent.setData(uri);// 设置获取到的URI
		context.startActivity(intent);
	}
}

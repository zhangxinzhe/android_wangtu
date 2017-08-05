package net.wangtu.android.util.xhttp;

import java.io.File;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
import net.wangtu.android.util.LogUtil;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.Cancelable;
import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;
import android.os.Handler;
import android.os.Looper;

import net.wangtu.android.common.util.CookieUtil;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.util.ContextUtil;
import net.wangtu.android.util.WangTuUtil;

public class XHttpUtil {
	private volatile static XHttpUtil instance;
	private Handler handler;

	private XHttpUtil() {
		handler = new Handler(Looper.getMainLooper());
	}

	/**
	 * 单例模式
	 * @return
	 */
	public static XHttpUtil getInstance() {
		if (instance == null) {
			synchronized (XHttpUtil.class) {
				if (instance == null) {
					instance = new XHttpUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * 异步get请求
	 * @param url
	 * @param maps
	 * @param callBack
	 * @return 
	 */
	public Cancelable get(String url, Map<String, String> maps, final XCallBack callBack) {
		RequestParams params = new RequestParams(url);
		params.addHeader("Cookie", CookieUtil.getAllCookies(WangTuUtil.getDomain(), ContextUtil.getContext()));// cookie
		params.addHeader("User-Agent", "Android " + ContextUtil.getContext().getAppIdentification());// 身份

		if (!ValidateUtil.isEmpty(maps)) {
			for (Map.Entry<String, String> entry : maps.entrySet()) {
				params.addQueryStringParameter(entry.getKey(), entry.getValue());
			}
		}

		return x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				onSuccessResponse(result, callBack);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				LogUtil.error(ex, XHttpUtil.class);
				onErrorResponse(callBack);
			}

			@Override
			public void onCancelled(CancelledException cex) {
				LogUtil.error(cex, XHttpUtil.class);
			}

			@Override
			public void onFinished() {
			}
		});
	}

	/**
	 * 异步文件上传
	 * @param url
	 * @param maps
	 * @param files
	 * @param callback
	 * @return 
	 */
	public Cancelable upLoadFile(String url, Map<String, String> maps, Map<String, List<File>> files, final XCallBack callback) {
		RequestParams params = new RequestParams(url);
		params.addHeader("Cookie", CookieUtil.getAllCookies(WangTuUtil.getDomain(), ContextUtil.getContext()));// cookie
		params.addHeader("User-Agent", "Android " + ContextUtil.getContext().getAppIdentification());// 身份
		params.addBodyParameter("device", "ANDROID");// 平台
		params.setConnectTimeout(3000);// 超时
		params.setMultipart(true);// 使用multipart表单上传文件
		
		if (!ValidateUtil.isEmpty(maps)) {
			for (Map.Entry<String, String> entry : maps.entrySet()) {
				params.addBodyParameter(entry.getKey(), entry.getValue());
			}
		}
		if (!ValidateUtil.isEmpty(files)) {
			for (Map.Entry<String, List<File>> entry : files.entrySet()) {
				List<File> fileList = entry.getValue();
				if (!ValidateUtil.isEmpty(fileList)) {
					for (File file : fileList) {
						params.addBodyParameter(entry.getKey(), file.getAbsoluteFile());
					}
				}
			}
		}
		return x.http().post(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				onSuccessResponse(result, callback);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				LogUtil.error(ex, XHttpUtil.class);
				onErrorResponse(callback);
			}

			@Override
			public void onCancelled(CancelledException cex) {
				LogUtil.error(cex, XHttpUtil.class);
			}

			@Override
			public void onFinished() {}
		});
	}

	/**
	 * 异步文件下载
	 * @param url
	 * @param filePath
	 * @param maps
	 * @param callBack
	 * @return
	 */
	public Cancelable downLoadFile(String url, String filePath, Map<String, String> maps, final XDownLoadCallBack callBack) {
		RequestParams params = new RequestParams(url);
		params.addHeader("Cookie", CookieUtil.getAllCookies(WangTuUtil.getDomain(), ContextUtil.getContext()));// cookie
		params.addHeader("User-Agent", "Android " + ContextUtil.getContext().getAppIdentification());// 身份
		params.setAutoResume(true);// 是否断点续传
		params.setAutoRename(false);// 是否根据头信息自动命名文件
		params.setCancelFast(true);// 是否可以被立即停止
		params.setSaveFilePath(filePath);
		
		if (!ValidateUtil.isEmpty(maps)) {
			for (Map.Entry<String, String> entry : maps.entrySet()) {
				params.addBodyParameter(entry.getKey(), entry.getValue());
			}
		}
		
		return x.http().get(params, new Callback.ProgressCallback<File>() {
			@Override
			public void onWaiting() {}

			@Override
			public void onStarted() {}
			
			@Override
			public void onLoading(final long total, final long current, final boolean isDownloading) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (callBack != null) {
							callBack.onLoading(total, current, isDownloading);
						}
					}
				});
			}
			
			@Override
			public void onSuccess(File result) {
				onSuccessResponse(result, callBack);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				LogUtil.error(ex, XHttpUtil.class);
				onErrorResponse(callBack);
			}

			@Override
			public void onCancelled(CancelledException cex) {
				LogUtil.error(cex, XHttpUtil.class);
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (callBack != null) {
							callBack.onCancelled();
						}
					}
				});
			}

			@Override
			public void onFinished() {}
		});
	}

	/**********************************************S——公共处理——S************************************************/
	/**
	 * 异步请求成功处理
	 */
	private void onSuccessResponse(final Object result, final XCallBack callBack) {
		// 更新cookie
		List<HttpCookie> cookies = DbCookieStore.INSTANCE.getCookies();
		if (!ValidateUtil.isEmpty(cookies)) {
			for (HttpCookie httpCookie : cookies) {
				CookieUtil.setCookie(httpCookie.getName(), httpCookie.getValue(), WangTuUtil.getDomain(), ContextUtil.getContext());
			}
		}
		
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (callBack != null) {
					callBack.onSuccess(result);
				}
			}
		});
	}
	
	/**
	 * 异步请求失败处理
	 */
	private void onErrorResponse(final XCallBack callBack) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (callBack != null) {
					callBack.onError();
				}
			}
		});
	}
	/**********************************************E——公共处理——E************************************************/

	/**********************************************S——接口——S****************************************************/
	public interface XCallBack {
		void onSuccess(Object result);
		void onError();
	}

	public interface XDownLoadCallBack extends XCallBack {
		void onLoading(long total, long current, boolean isDownloading);
		void onCancelled();
	}
	/**********************************************E——接口——E****************************************************/
}
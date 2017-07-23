package net.wangtu.android.common.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.WindowManager;

import net.wangtu.android.util.ContextUtil;

public class Util {
	private static Boolean isPad;
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		//一个屏幕密度为160dpi的手机屏幕上density的值为1，而在120dpi的手机上为0.75等等
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);//0.5是为了四舍五入
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		//一个屏幕密度为160dpi的手机屏幕上density的值为1，而在120dpi的手机上为0.75等等
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);//0.5是为了四舍五入
	}
	
	/**
	 * 获取不包括状态栏的高度
	 * @param context
	 * @return
	 */
	public static int getWindowHeight(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight() - Util.getStatusHeight(context);
	}
	
	public static int getWindowWidth(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}
	
	/**
	 * 获取手机状态栏的高度
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context){
		Class<?> c =  null; 
		Object obj =  null; 
		Field field =  null; 
		int  x = 0; 
		try  { 
		    c = Class.forName("com.android.internal.R$dimen");
		    obj = c.newInstance();
		    field = c.getField("status_bar_height");
		    x = Integer.parseInt(field.get(obj).toString());
		    return context.getResources().getDimensionPixelSize(x);
		} catch(Exception e) {
		    return 0;
		}
	}
	
	/**
	 * 获取手机状态栏的高度
	 * @param context
	 * @return
	 */
	public static int getStatusHeightIfNeed(Context context){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			return getStatusHeight(context);
		}
		return 0;
	}
	
	/**
	 * 照相机权限
	 * @return
	 */
	public static boolean checkCameraPermission(){
		PackageManager pkm = ContextUtil.getContext().getPackageManager();
		int permit = pkm.checkPermission("android.permission.CAMERA", ContextUtil.getContext().getPackageName());
		return permit == PackageManager.PERMISSION_GRANTED;
	}
	
	/**
	 * 计算md5
	 * @param string
	 * @return
	 */
	public static String md5(String string) {
	    byte[] hash;
	    try {
	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString();
	}
	
}

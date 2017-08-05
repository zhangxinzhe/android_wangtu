package net.wangtu.android.common.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.wangtu.android.util.ContextUtil;
import net.wangtu.android.util.LogUtil;

/**
 * 图片加载工具
 * 
 * @author kuanghf
 */
public class ImageCacheUtil {
	
	/**
	 * 加载图片
	 * @param imageView
	 * @param url
	 * @param defaltImageId
	 * @param useAnimate 是否需要动画（解决单张图片有动画第二次才显示问题）
	 * @return
	 */
	public static void lazyLoad(ImageView imageView, String url, int defaltImageId, boolean useAnimate) {
		if (useAnimate) {
			Glide.with(imageView.getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaltImageId).into(imageView);
		} else {
			Glide.with(imageView.getContext()).load(url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaltImageId).into(imageView);
		}
	}
	
	/**
	 * 清除缓存
	 */
	public void clearCache(){
		try {
			//清除磁盘缓存
			Glide.get(ContextUtil.getContext()).clearDiskCache();
			//清除内存缓存
			Glide.get(ContextUtil.getContext()).clearMemory();
		} catch (Exception e) {
			LogUtil.error(e, ImageCacheUtil.class);
		}
	}
}
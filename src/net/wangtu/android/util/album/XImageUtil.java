package net.wangtu.android.util.album;

import net.wangtu.android.R;
import org.xutils.x;
import org.xutils.image.ImageOptions;

import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 图片加载工具
 * @author kuanghf
 */
public class XImageUtil {
	private static XImageUtil instance;

	private XImageUtil() {}

	/**
	 * 单例模式
	 */
	public static XImageUtil getInstance() {
		if (instance == null) {
			synchronized (XImageUtil.class) {
				if (instance == null) {
					instance = new XImageUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * 加载图片
	 * @param imageView
	 * @param url
	 */
	public void loadImage(ImageView imageView, String url) {
		ImageOptions options = new ImageOptions.Builder().setIgnoreGif(false)
				.setUseMemCache(true).setAutoRotate(true).setFadeIn(true)
				.setImageScaleType(ScaleType.CENTER_CROP)
				.setLoadingDrawableId(R.drawable.icon_camera_no_pictures)
				.setFailureDrawableId(R.drawable.icon_camera_no_pictures).build();
		x.image().bind(imageView, url, options);
	}
}
package net.wangtu.android.util.album;

import java.util.ArrayList;
import java.util.List;

/**
 * 照片选择操作
 * @author kuanghf
 */
public class AlbumOpt {

	// 选择的图片的临时列表
	public static List<ImageItem> tempSelectImages = new ArrayList<ImageItem>();

	// 选择的图片的最终列表
	public static List<ImageItem> selectImages = new ArrayList<ImageItem>();

	// 所有相册列表
	public static List<ImageBucket> imageBuckets = new ArrayList<ImageBucket>();
}
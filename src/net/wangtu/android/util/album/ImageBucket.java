package net.wangtu.android.util.album;

import java.util.List;

/**
 * 相册
 * @author kuanghf
 */
public class ImageBucket {
	private String bucketName;
	private List<ImageItem> imageList;

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public List<ImageItem> getImageList() {
		return imageList;
	}

	public void setImageList(List<ImageItem> imageList) {
		this.imageList = imageList;
	}
}
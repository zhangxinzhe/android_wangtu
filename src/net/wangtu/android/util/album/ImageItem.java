package net.wangtu.android.util.album;


/**
 * 相片
 * @author kuanghf
 */
public class ImageItem {
	private String imageId;
	private String imagePath;
	private boolean isLocal = true;

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean getIsLocal() {
		return isLocal;
	}

	public void setIsLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}
}
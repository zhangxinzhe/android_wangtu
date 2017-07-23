package net.wangtu.android.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.statusbar.StatusManager;
import net.wangtu.android.common.util.FileUtil;
import net.wangtu.android.common.view.clip.ClipViewLayout;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.XImageUtil;

import java.io.IOException;

/**
 * 选择相册
 * @author kuanghf
 */
public class ClipImageActivity extends Activity {
	//common_clip_image
	private ClipViewLayout clipViewLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.common_clip_image);

		init();
	}

	/**
	 * 初始化ui
	 */
	private void init() {
		clipViewLayout = (ClipViewLayout) findViewById(R.id.clipViewLayout);

		// 设置图片资源
		clipViewLayout.setImageSrc(getIntent().getStringExtra("path"));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}

	/**
	 * 取消
	 * @param view
	 */
	public void cancel(View view) {
		finish();
	}

	/**
	 * 完成
	 * @param view
	 */
	public void confirm(View view){
		// 调用返回剪切图
		Bitmap zoomedCropBitmap = clipViewLayout.clip();
		String clipPath;
		try {
			clipPath = FileUtil.saveBitmap(zoomedCropBitmap, null);
		} catch (IOException e) {
			clipPath = null;
		}
		// 关闭当前页
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		intent.putExtra("clipPath", clipPath);
		finish();
	}
}
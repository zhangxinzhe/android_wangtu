package net.wangtu.android.activity.common;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.dialog.ActionSheet;
import net.wangtu.android.util.album.AlbumHelper;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageBucket;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.album.XImageUtil;

/**
 * 选择相册
 * @author kuanghf
 */
public class AlbumActivity extends BaseActivity {
	private static final int REQUEST_PICK = 0x000001;
	
	//private AlbumHeaderView headerView;
	private ListView lv_album;
	private AlbumAdapter albumAdapter;
	
	private int imageNum;
	private AlbumHelper helper;

	@Override
	protected void toCreate(Bundle savedInstanceState) {
		super.toCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.common_camera_album);
		initHeader("选择相册",true);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 获取图片个数
		imageNum = getIntent().getExtras().getInt("imageNum");
		// 初始化view
		lv_album = (ListView) findViewById(R.id.lv_album);
		// 获取相册数
		helper = AlbumHelper.getHelper();
		helper.init(this);
		AlbumOpt.imageBuckets = helper.getImagesBucketList(false);
		// 清空信息
		AlbumOpt.tempSelectImages.clear();
		AlbumOpt.tempSelectImages.addAll(AlbumOpt.selectImages);
		//设置适配器
		albumAdapter = new AlbumAdapter(this);
		lv_album.setAdapter(albumAdapter);
		// 设置点击事件
		lv_album.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<ImageItem> imageList = AlbumOpt.imageBuckets.get(position).getImageList();
				if (ValidateUtil.isEmpty(imageList)) {
					return;
				}
				
				Intent intent = new Intent(AlbumActivity.this, PhotoActivity.class);
				intent.putExtra("imageNum", imageNum);
				intent.putExtra("position", position);
				startActivityForResult(intent, REQUEST_PICK);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_PICK:
			if (resultCode == RESULT_OK) {
				for (ImageItem imageItem : AlbumOpt.tempSelectImages) {
					if (!AlbumOpt.selectImages.contains(imageItem)) {
						AlbumOpt.selectImages.add(imageItem);
					}
				}
				AlbumOpt.tempSelectImages.clear();
				
				// 关闭当前页
				setResult(RESULT_OK);
				finish();
			} else if (resultCode == RESULT_FIRST_USER) {
				setResult(RESULT_FIRST_USER);
				finish();
			}
			break;
		}
	}
	
	private class AlbumAdapter extends BaseAdapter {
		private Context mContext;
		
		public AlbumAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			if (ValidateUtil.isEmpty(AlbumOpt.imageBuckets)) {
				return 0;
			}
			return AlbumOpt.imageBuckets.size();
		}

		@Override
		public ImageBucket getItem(int position) {
			if (ValidateUtil.isEmpty(AlbumOpt.imageBuckets)) {
				return null;
			}
			return AlbumOpt.imageBuckets.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(mContext, R.layout.common_camera_album_item, null);
				holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
				holder.tv_album_name = (TextView) convertView.findViewById(R.id.tv_album_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			ImageBucket imageBucket = getItem(position);
			if (ValidateUtil.isEmpty(imageBucket.getImageList())) {
				holder.iv_image.setImageResource(R.drawable.icon_camera_no_pictures);
				holder.tv_album_name.setText(imageBucket.getBucketName() + "（0）");
			} else {
				ImageItem imageItem = imageBucket.getImageList().get(0);
				XImageUtil.getInstance().loadImage(holder.iv_image, imageItem.getImagePath());
				holder.tv_album_name.setText(imageBucket.getBucketName() + "（" + imageBucket.getImageList().size() + "）");
			}
			
			return convertView;
		}

		private class ViewHolder {
			public ImageView iv_image;
			public TextView tv_album_name;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && headerView != null) {
			if(headerBack != null){
				headerBack.performClick();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void finish() {
		// 清空信息
		AlbumOpt.tempSelectImages.clear();
		super.finish();
	}
}
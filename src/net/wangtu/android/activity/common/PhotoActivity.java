package net.wangtu.android.activity.common;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.album.XImageUtil;
import net.wangtu.android.common.view.dialog.AlertView;

public class PhotoActivity extends BaseActivity {
	private GridView gv_image;
	private Button optBtn;
	
	private int imageNum;// 最多选择图片个数
	private int position;
	private boolean multiselect;// 是否多选
	private List<ImageItem> photoList;
	private PhotoAdapter photoAdapter;
	
	@Override
	protected void toCreate(Bundle savedInstanceState) {
		super.toCreate(savedInstanceState);
		setContentView(R.layout.common_camera_photo);
		optBtn = (Button)findViewById(R.id.opt_btn);

		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 获取传递数据
		Bundle bundle = getIntent().getExtras();
		imageNum = bundle.getInt("imageNum");
		position = bundle.getInt("position");
		photoList = AlbumOpt.imageBuckets.get(position).getImageList();
		// 初始化控件
		gv_image = (GridView) findViewById(R.id.gv_image);
		// 是否多选
		multiselect = imageNum > 1;
		// 设置适配器
		photoAdapter = new PhotoAdapter(this, photoList, AlbumOpt.tempSelectImages);
		gv_image.setAdapter(photoAdapter);
		// 设置条目点击事件
		gv_image.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (multiselect) {
					ImageView iv_check = (ImageView) ((ViewGroup)view).getChildAt(2);
					boolean isChecked = !iv_check.isShown();
					if (AlbumOpt.tempSelectImages.size() >= imageNum) {
						if (!isChecked) {
							iv_check.setVisibility(View.GONE);
							AlbumOpt.tempSelectImages.remove(photoList.get(position));
						} else {
							final AlertView alertView = new AlertView(PhotoActivity.this);
							alertView.setContentMsg("最多只能选择" + imageNum + "张照片");
							alertView.setOkBtnListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertView.dismiss();
								}
							});
							alertView.show();
						}
					} else if (isChecked) {
						iv_check.setVisibility(View.VISIBLE);
						AlbumOpt.tempSelectImages.add(photoList.get(position));
					} else {
						iv_check.setVisibility(View.GONE);
						AlbumOpt.tempSelectImages.remove(photoList.get(position));
					}
				} else {
					AlbumOpt.tempSelectImages.add(photoList.get(position));
					// 关闭当前页
					setResult(RESULT_OK);
					finish();
				}
			}
		});

		initHeader("选择照片",true);

		// 完成按钮
		opt(multiselect);
	}

	private void opt(final boolean multiselect){
		//btn_choosed
		if (!multiselect) {
			optBtn.setText("取消");
		}
		optBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (multiselect) {
					setResult(Activity.RESULT_OK);
				} else {
					setResult(Activity.RESULT_FIRST_USER);
				}
				// 关闭当前页
				finish();
			}
		});
	}
	
	private class PhotoAdapter extends BaseAdapter {
		private Context context;
		private List<ImageItem> dataList;
		private List<ImageItem> selectedDataList;
		
		public PhotoAdapter(Context context, List<ImageItem> dataList, List<ImageItem> selectedDataList) {
			this.context = context;
			this.dataList = dataList;
			this.selectedDataList = selectedDataList;
		}

		@Override
		public int getCount() {
			if (ValidateUtil.isEmpty(dataList)) {
				return 0;
			}
			return dataList.size();
		}

		@Override
		public ImageItem getItem(int position) {
			if (ValidateUtil.isEmpty(dataList)) {
				return null;
			}
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(context, R.layout.common_camera_photo_item, null);
				viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
				viewHolder.iv_multiselect = (ImageView) convertView.findViewById(R.id.iv_multiselect);
				viewHolder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			ImageItem imageItem = getItem(position);
			XImageUtil.getInstance().loadImage(viewHolder.iv_image, imageItem.getImagePath());
			if (multiselect) {
				if (selectedDataList.contains(imageItem)) {
					viewHolder.iv_check.setVisibility(View.VISIBLE);
				} else {
					viewHolder.iv_check.setVisibility(View.GONE);
				}
				viewHolder.iv_multiselect.setVisibility(View.VISIBLE);
			} else {
				viewHolder.iv_check.setVisibility(View.GONE);
				viewHolder.iv_multiselect.setVisibility(View.GONE);
			}
			
			return convertView;
		}

		private class ViewHolder {
			public ImageView iv_check;
			public ImageView iv_multiselect;
			public ImageView iv_image;
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
}

package net.wangtu.android.activity.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
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

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.FileUtil;
import net.wangtu.android.common.util.JsonUtil;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.dialog.ActionSheet;
import net.wangtu.android.common.view.dialog.BoxView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.album.AlbumHelper;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageBucket;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.album.XImageUtil;
import net.wangtu.android.util.xhttp.XHttpUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择相册
 * @author kuanghf
 */
public class AvatarActivity extends BaseActivity {
	public static final int imageNum = 1;// 图片个数
	public static final int REQUEST_CAPTURE = 0x000001;
	public static final int REQUEST_PICK = 0x000002;
	public static final int REQUEST_CLIP = 0x000003;

	private ImageView mAvatar;
	private String avatarFile;

	@Override
	protected void toCreate(Bundle savedInstanceState) {
		super.toCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.avatar);
		initHeader("头像修改",true);
		Bundle bundle = getIntent().getExtras();
		avatarFile = bundle.getString("avatarFile");
		init();
		initHeader();
	}

	public void init(){
		mAvatar = (ImageView) findViewById(R.id.iv_avatar);
		// 显示头像
		if(!ValidateUtil.isBlank(avatarFile)){
			XImageUtil.getInstance().loadImage(mAvatar, avatarFile);
		}
		// 清除信息
		AlbumOpt.selectImages.clear();
	}

	private ActionSheet actionSheet;
	public void initHeader(){
		actionSheet = new ActionSheet(this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
				.addSheetItem("拍照", ActionSheet.SheetItemColor.Blue,
						new ActionSheet.OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								photo();
							}
						})
				.addSheetItem("从相册中选择", ActionSheet.SheetItemColor.Blue,
						new ActionSheet.OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								album();
							}
						});
	}
	/**
	 * 拍照
	 */
	private void photo(){
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), AvatarActivity.REQUEST_CAPTURE);
	}

	/**
	 * 相册
	 */
	private void album(){
		Intent intent = new Intent(this, AlbumActivity.class);
		intent.putExtra("imageNum", AvatarActivity.imageNum);
		startActivityForResult(intent, AvatarActivity.REQUEST_PICK);
	}
	public void moreOnClick(View view){
		actionSheet.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CAPTURE:// 拍照返回
				if (resultCode == RESULT_OK) {
					Bitmap bm = (Bitmap) data.getExtras().get("data");
					String imagePath;
					try {
						imagePath = FileUtil.saveBitmap(bm, null);
					} catch (IOException e) {
						imagePath = null;
					}

					gotoClipActivity(imagePath);
				}
				break;
			case REQUEST_PICK:// 相册返回
				if (resultCode == RESULT_OK) {
					gotoClipActivity(AlbumOpt.selectImages.get(0).getImagePath());
					AlbumOpt.selectImages.clear();
				}
				break;
			case REQUEST_CLIP:// 截图返回
				if (resultCode == RESULT_OK) {
					String clipPath = data.getStringExtra("clipPath");
					editAvatar(clipPath);
				}
				break;
		}
	}

	/**
	 * 打开截图界面
	 * @param imagePath
	 */
	public void gotoClipActivity(String imagePath) {
		Intent intent = new Intent(this, ClipImageActivity.class);
		intent.putExtra("path", imagePath);
		startActivityForResult(intent, REQUEST_CLIP);
	}

	/**
	 * 上传头像到服务器
	 * @param clipPath
	 */
	public void editAvatar(String clipPath){
		if (ValidateUtil.isBlank(clipPath)) {
			ToastUtil.error(this, "图片不存在");
			return;
		}

		XImageUtil.getInstance().loadImage(mAvatar, clipPath);

		String url = WangTuUtil.getPage(Constants.API_CHANGE_AVATAR);
		Map<String, List<File>> files = new HashMap<String, List<File>>();
		List<File> fileList = new ArrayList<File>();
		fileList.add(new File(clipPath));
		files.put("avatar", fileList);

		ToastUtil.startLoading(this);
		XHttpUtil.getInstance().upLoadFile(url, null, files, new XHttpUtil.XCallBack() {
			@Override
			public void onSuccess(Object result) {
				ToastUtil.stopLoading(AvatarActivity.this);
				try {
					JSONObject dataJson = JsonUtil.parseJson((String) result);
					if ("success".equals(dataJson.optString("msg"))) {
						ToastUtil.alert(AvatarActivity.this, "头像修改成功！", new ToastUtil.DialogOnClickListener() {
							@Override
							public void onClick(BoxView dialog) {
								finish();
							}
						});
					} else {
						ToastUtil.error(AvatarActivity.this, dataJson.optString("msg"));
					}
				}catch (Exception e){
					ToastUtil.error(AvatarActivity.this, e.getMessage());
				}
			}

			@Override
			public void onError() {
				ToastUtil.stopLoading(AvatarActivity.this);
				ToastUtil.error(AvatarActivity.this, "修改失败");
			}
		});
	}

	@Override
	public void finish() {
		// 清除信息
		AlbumOpt.selectImages.clear();
		super.finish();
	}
}
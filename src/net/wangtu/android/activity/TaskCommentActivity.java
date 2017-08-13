package net.wangtu.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.activity.base.BaseFragmentActivity;
import net.wangtu.android.activity.common.AlbumActivity;
import net.wangtu.android.common.util.FileUtil;
import net.wangtu.android.common.util.ImageCacheUtil;
import net.wangtu.android.common.util.JsonUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.HorizontalListView;
import net.wangtu.android.common.view.dialog.ActionSheet;
import net.wangtu.android.common.view.dialog.BoxView;
import net.wangtu.android.util.LoginUtil;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.album.XImageUtil;
import net.wangtu.android.util.xhttp.XHttpUtil;
import net.wangtu.android.view.RewardEditView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class TaskCommentActivity extends BaseActivity {
    private static final int imageNum = 9;// 图片个数
    private static final int REQUEST_CAPTURE = 0x000001;
    private static final int REQUEST_PICK = 0x000002;
    
    private ImageView commentDesignerImage;
    private TextView commentDesignerName;
    private ImageView[] serviceQualitys = new ImageView[5];
    private ImageView[] serviceAttitudes = new ImageView[5];
    private TextView serviceAttitudeComment;
    private TextView serviceQualityComment;
    private ImageView commentAnonymousCheck;
    private HorizontalListView photoView;
    private TextView photoNumTv;
    private ActionSheet actionSheet;
    private MyAdapter adapter;

    private int qualityScore;
    private int attitudeScore;
    private boolean isAnonymous;
    private JSONObject dataJson;

    private String rewardId;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.task_comment);
        Bundle bundle = getIntent().getExtras();
        rewardId = bundle.getString("rewardId");

        initUI();
        initEvent();
        initData();
    }

    private void initUI(){
        commentDesignerImage = (ImageView) findViewById(R.id.comment_designer_img);
        commentDesignerName = (TextView) findViewById(R.id.comment_designer_name);

        serviceQualitys[0] = (ImageView) findViewById(R.id.service_quality_star_1);
        serviceQualitys[1] = (ImageView) findViewById(R.id.service_quality_star_2);
        serviceQualitys[2] = (ImageView) findViewById(R.id.service_quality_star_3);
        serviceQualitys[3] = (ImageView) findViewById(R.id.service_quality_star_4);
        serviceQualitys[4] = (ImageView) findViewById(R.id.service_quality_star_5);

        serviceAttitudes[0] = (ImageView) findViewById(R.id.service_attitude_star_1);
        serviceAttitudes[1] = (ImageView) findViewById(R.id.service_attitude_star_2);
        serviceAttitudes[2] = (ImageView) findViewById(R.id.service_attitude_star_3);
        serviceAttitudes[3] = (ImageView) findViewById(R.id.service_attitude_star_4);
        serviceAttitudes[4] = (ImageView) findViewById(R.id.service_attitude_star_5);

        serviceAttitudeComment = (TextView) findViewById(R.id.service_attitude_content);
        serviceQualityComment = (TextView) findViewById(R.id.service_quality_content);
        commentAnonymousCheck = (ImageView) findViewById(R.id.comment_anonymous_check);

        photoView = (HorizontalListView) findViewById(R.id.photoView);
        photoNumTv = (TextView) findViewById(R.id.tv_photo_num);
    }

    private void initEvent(){
        //图片选择
        photoView.setAdapter(adapter = new MyAdapter(this,null));
        photoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == AlbumOpt.selectImages.size()) {
                    actionSheet.show();
                }
            }
        });
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

    private void initData(){
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_GET_COMMENT_USER_INFO) + "?rewardId=" + rewardId;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,TaskCommentActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(TaskCommentActivity.this);
                            JSONObject userInfo = dataJson.optJSONObject("userInfo");
                            ImageCacheUtil.lazyLoad(commentDesignerImage,WangTuUtil.getPage(userInfo.optString("avatarFile")),R.drawable.icon_header,false);
                            commentDesignerName.setText(userInfo.optString("realName"));
                        }
                    });

                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(TaskCommentActivity.this);
                            showError();
                        }
                    });
                }
            }
        });
    }

    public void serviceQualityOnClick(View view){
        int imgId = R.drawable.icon_big_star_yellow;
        qualityScore = 0;
        for (ImageView serviceQuality : serviceQualitys){
            serviceQuality.setImageResource(imgId);
            if(imgId == R.drawable.icon_big_star_yellow){
                qualityScore++;
            }
            if(serviceQuality == view){
                imgId = R.drawable.icon_big_star_gray;
            }
        }
    }

    public void serviceAttitudeOnClick(View view){
        int imgId = R.drawable.icon_big_star_yellow;
        attitudeScore = 0;
        for (ImageView serviceAttitude : serviceAttitudes){
            serviceAttitude.setImageResource(imgId);
            if(imgId == R.drawable.icon_big_star_yellow){
                attitudeScore++;
            }
            if(serviceAttitude == view){
                imgId = R.drawable.icon_big_star_gray;
            }
        }
    }

    public void commentAnonymousOnClick(View view){
        if(isAnonymous){
            commentAnonymousCheck.setImageResource(R.drawable.icon_user_info_check);
            isAnonymous= false;
        }else{
            commentAnonymousCheck.setImageResource(R.drawable.icon_user_info_checked);
            isAnonymous= true;
        }
    }

    public void commentApplyOnClick(View view){
        Map<String,String> params = new HashMap<String,String>();
        if(qualityScore <= 0){
            ToastUtil.error(this,"请给服务质量打分！");
            return;
        }
        params.put("comment.serviceQuality",qualityScore + "");
        if(attitudeScore <= 0){
            ToastUtil.error(this,"请给服务态度打分！");
            return;
        }
        params.put("comment.serviceAttitude",attitudeScore + "");

        params.put("comment.serviceQualityContent",serviceQualityComment.getText().toString());
        params.put("comment.serviceAttitudeContent",serviceAttitudeComment.getText().toString());
        params.put("comment.rewardId",rewardId);
        params.put("comment.isAnonymous",isAnonymous?"true":"false");

        List<File> fileList = new ArrayList<File>();
        for (int i = 0; i < AlbumOpt.selectImages.size(); i++) {
            ImageItem imageItem = AlbumOpt.selectImages.get(i);
            fileList.add(new File(imageItem.getImagePath()));
        }
        Map<String, List<File>> files = new HashMap<String, List<File>>();
        files.put("commentFiles", fileList);

        String url = WangTuUtil.getPage(Constants.API_ADD_COMMENT);

        startLoading();
        XHttpUtil.getInstance().upLoadFile(url, params, files, new XHttpUtil.XCallBack() {
            @Override
            public void onSuccess(Object result) {
                stopLoading();
                try {
                    JSONObject dataJson = JsonUtil.parseJson((String) result);
                    if("success".equals(dataJson.optString("msg"))){
                        ToastUtil.alert(TaskCommentActivity.this, "提交成功", new ToastUtil.DialogOnClickListener() {
                            @Override
                            public void onClick(BoxView dialog) {
                                finish();
                            }
                        });
                    }else{
                        ToastUtil.error(TaskCommentActivity.this, dataJson.optString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                stopLoading();
                ToastUtil.error(TaskCommentActivity.this, "网络错误");
            }
        });
    }

    public void closeCommentOnClick(View view){
        finish();
    }

    /**
     * 拍照
     */
    private void photo(){
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAPTURE);
    }

    /**
     * 相册
     */
    private void album(){
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("imageNum", imageNum);
        startActivityForResult(intent, REQUEST_PICK);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAPTURE:
                if (AlbumOpt.selectImages.size() < imageNum && resultCode == RESULT_OK) {
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    String imagePath;
                    try {
                        imagePath = FileUtil.saveBitmap(bm, null);
                    } catch (IOException e) {
                        imagePath = null;
                    }
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setImagePath(imagePath);
                    AlbumOpt.selectImages.add(takePhoto);

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    setPhotoNum();
                }
                break;
            case REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    setPhotoNum();
                }
                break;
        }
    }

    private void setPhotoNum() {
        if (AlbumOpt.selectImages.size() > 0) {
            photoNumTv.setText("已选" + AlbumOpt.selectImages.size() + "张，还可选" + (imageNum - AlbumOpt.selectImages.size()) + "张");
        } else {
            photoNumTv.setText("可选" + imageNum + "张");
        }
    }

    private class MyAdapter extends BaseAdapter {
        private Context context;

        public MyAdapter(Context context,JSONArray jsonArray) {
            this.context = context;
            if(jsonArray != null && jsonArray.length() > 0){
                AlbumOpt.selectImages.clear();
                ImageItem imageItem = null;
                for (int i = 0;i<jsonArray.length();i++ ){
                    imageItem = new ImageItem();
                    imageItem.setIsLocal(false);
                    imageItem.setImagePath(jsonArray.optJSONObject(i).optString("filePath"));
                    imageItem.setImageId(jsonArray.optJSONObject(i).optString("id"));
                    AlbumOpt.selectImages.add(imageItem);
                }
            }
        }

        @Override
        public int getCount() {
            if (AlbumOpt.selectImages.size() == imageNum) {
                return imageNum;
            }
            return (AlbumOpt.selectImages.size() + 1);
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = new FrameLayout(context);
                convertView.setLayoutParams(new AbsListView.LayoutParams(Util.dip2px(context, 65), Util.dip2px(context, 65)));

                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ((FrameLayout)convertView).addView(imageView);

                ImageView delBtn = new ImageView(context);
                FrameLayout.LayoutParams delParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                delParams.gravity = Gravity.RIGHT;
                delBtn.setLayoutParams(delParams);
                delBtn.setImageResource(R.drawable.icon_button_del);
                ((FrameLayout)convertView).addView(delBtn);

                holder.image = imageView;
                holder.delBtn = delBtn;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == AlbumOpt.selectImages.size()) {
                holder.delBtn.setVisibility(View.GONE);
                if (position == imageNum) {
                    holder.image.setVisibility(View.GONE);
                }
                holder.image.setImageResource(R.drawable.icon_button_add);
            } else {
                holder.image.setVisibility(View.VISIBLE);
                holder.delBtn.setVisibility(View.VISIBLE);

                ImageItem item = AlbumOpt.selectImages.get(position);
                if(item.getIsLocal()){
                    XImageUtil.getInstance().loadImage(holder.image, item.getImagePath());
                }else{
                    XImageUtil.getInstance().loadImage(holder.image, WangTuUtil.getPage(item.getImagePath()));
                }

                holder.delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlbumOpt.selectImages.remove(position);
                        notifyDataSetChanged();
                        setPhotoNum();
                    }
                });
            }

            return convertView;
        }

        private class ViewHolder {
            public ImageView image;
            public ImageView delBtn;
        }
    }
}

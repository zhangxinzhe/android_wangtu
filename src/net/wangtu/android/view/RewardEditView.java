package net.wangtu.android.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.common.AlbumActivity;
import net.wangtu.android.common.util.FileUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.view.HorizontalListView;
import net.wangtu.android.common.view.dialog.ActionSheet;
import net.wangtu.android.util.HeaderUtil;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.album.XImageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * 悬赏发布
 */

public class RewardEditView extends LinearLayout{
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd号");

    private static final int imageNum = 9;// 图片个数
    private static final int REQUEST_CAPTURE = 0x000001;
    private static final int REQUEST_PICK = 0x000002;
    private boolean inited;

    private HorizontalListView photoView;
    private TextView photoNumTv;
    private ActionSheet actionSheet;
    private RewardEditView.MyAdapter adapter;

    private EditText txtRewardTitle;
    private TextView txtRewardCatalog;
    private EditText txtRewardPrice;
    private TextView txtRewardDeadline;
    private EditText txtRewardLocation;
    private EditText txtRewardDescription;

    private SelectPopupWindow popupWindow;
    private String catalogId;
    private JSONObject catalogJson;
    private JSONObject dataJson;


    public RewardEditView(Context context) {
        this(context, null);
    }

    public RewardEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RewardEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(inited){
            return;
        }
        inited = true;
        initUI();
        initEvent();
    }

    private void initUI(){
        //状态栏透明
        HeaderUtil.initRewardPublishStatusBar((Activity) getContext(),this);

        photoView = (HorizontalListView) findViewById(R.id.photoView);
        photoNumTv = (TextView) findViewById(R.id.tv_photo_num);
        txtRewardTitle = (EditText) findViewById(R.id.reward_title);
        txtRewardCatalog = (TextView) findViewById(R.id.reward_catalog);
        txtRewardPrice = (EditText) findViewById(R.id.reward_price);
        txtRewardDeadline = (TextView) findViewById(R.id.reward_deadline);
        txtRewardLocation = (EditText) findViewById(R.id.reward_location);
        txtRewardDescription = (EditText) findViewById(R.id.reward_description);
    }

    private void initEvent(){
        //分类选择
        txtRewardCatalog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(catalogJson != null){
                    if(catalogId != null){
                        popupWindow.setValue(catalogId);
                    }
                    popupWindow.show();
                    return;
                }

                ToastUtil.startLoading(getContext());
                txtRewardCatalog.setEnabled(false);
                ThreadUtils.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            catalogJson = WangTuHttpUtil.getJson(WangTuUtil.getPage(Constants.API_LIST_CATALOG),getContext());
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.stopLoading(getContext());
                                    if(catalogJson == null){
                                        return;
                                    }
                                    if(popupWindow == null){
                                        try {
                                            popupWindow = new SelectPopupWindow((View) getParent(),txtRewardCatalog);
                                            JSONArray dataList = catalogJson.optJSONArray("catalogs");
                                            JSONObject cataLog = null;
                                            for (int i=0;i < dataList.length();i++) {
                                                cataLog = dataList.getJSONObject(i);
                                                cataLog.put("key",cataLog.optString("cname"));
                                                cataLog.put("value",cataLog.optString("id"));
                                            }
                                            popupWindow.initData(catalogJson.optJSONArray("catalogs"));
                                            popupWindow.setOnItemCheckListener(new SelectPopupWindow.OnItemCheckListener() {
                                                @Override
                                                public void onItemChecked(JSONObject data) throws JSONException {
                                                    txtRewardCatalog.setText(data.getString("key"));
                                                    catalogId = data.getString("value");
                                                }
                                            });
                                        } catch (JSONException e) {
                                            ToastUtil.error(getContext(),e.getMessage());
                                            catalogJson = null;
                                            txtRewardCatalog.setEnabled(true);
                                        }
                                    }
                                    if(catalogId != null){
                                        popupWindow.setValue(catalogId);
                                    }
                                    popupWindow.show();
                                    txtRewardCatalog.setEnabled(true);
                                }
                            });
                        } catch (final Exception e) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.error(getContext(),e.getMessage());
                                    txtRewardCatalog.setEnabled(true);
                                }
                            });
                            catalogJson = null;
                        }
                    }
                });
            }
        });

        //图片选择
        photoView.setAdapter(adapter = new RewardEditView.MyAdapter(getContext(),null));
        photoView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == AlbumOpt.selectImages.size()) {
                    actionSheet.show();
                }
            }
        });
        actionSheet = new ActionSheet(getContext()).builder().setCancelable(true).setCanceledOnTouchOutside(true)
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

        txtRewardDeadline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
                            txtRewardDeadline.setText(year + "年" + monthOfYear + "月" + dayOfMonth + "号");
                        }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    /**
     * 拍照
     */
    private void photo(){
        Activity activity = (Activity)getContext();
        activity.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAPTURE);
    }

    /**
     * 相册
     */
    private void album(){
        Intent intent = new Intent(getContext(), AlbumActivity.class);
        intent.putExtra("imageNum", imageNum);
        Activity activity = (Activity)getContext();
        activity.startActivityForResult(intent, REQUEST_PICK);
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

    public void initData(final JSONObject dataJson){
        this.dataJson = dataJson;
        txtRewardTitle.setText(dataJson.optString("title"));
        txtRewardCatalog.setText(dataJson.optString("cataName"));
        catalogId = dataJson.optString("catalogId");
        txtRewardLocation.setText(dataJson.optString("location"));
        Date deadline = new Date(dataJson.optLong("deadline"));
        txtRewardDeadline.setText(dateFormat.format(deadline));
        String description = dataJson.optString("description");
        if(description.length() > 8){
            description = description.substring(0,8) + "......";
        }
        txtRewardDescription.setText(description);
        txtRewardPrice.setText(dataJson.optString("price"));

        //图片展示
        JSONArray images = dataJson.optJSONArray("pictures");
        photoView.setAdapter(adapter = new MyAdapter(getContext(),dataJson.optJSONArray("pictures")));
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
            RewardEditView.MyAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new RewardEditView.MyAdapter.ViewHolder();
                convertView = new FrameLayout(context);
                convertView.setLayoutParams(new AbsListView.LayoutParams(Util.dip2px(context, 100), Util.dip2px(context, 100)));

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
                holder = (RewardEditView.MyAdapter.ViewHolder) convertView.getTag();
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

                holder.delBtn.setOnClickListener(new OnClickListener() {
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

    public Map<String,String> getReward() throws JSONException {
        Map<String,String> rewardJson = new HashMap<String,String>();
        if(dataJson != null){
            rewardJson.put("reward.id",dataJson.optString("id"));
        }
        rewardJson.put("reward.title",txtRewardTitle.getText().toString());
        rewardJson.put("reward.catalogId",catalogId);
        rewardJson.put("reward.price",txtRewardPrice.getText().toString());
        rewardJson.put("reward.deadline",txtRewardDeadline.getText().toString().replaceAll("(年|月)","-").replace("号",""));
        rewardJson.put("reward.location",txtRewardLocation.getText().toString());
        rewardJson.put("reward.description",txtRewardDescription.getText().toString());
        return rewardJson;
    }

    public void clearReward() throws JSONException {
        txtRewardTitle.setText("");
        txtRewardCatalog.setText("请选择");
        txtRewardPrice.setText("0");
        txtRewardDeadline.setText("");
        txtRewardLocation.setText("不限");
        txtRewardDescription.setText("");
    }
}

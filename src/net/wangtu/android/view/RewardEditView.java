package net.wangtu.android.view;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.HomeActivity;
import net.wangtu.android.activity.MyRewardDetailPublishActivity;
import net.wangtu.android.activity.MyTaskActivity;
import net.wangtu.android.activity.common.AlbumActivity;
import net.wangtu.android.common.util.FileUtil;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.view.HorizontalListView;
import net.wangtu.android.common.view.dialog.ActionSheet;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.util.HeaderUtil;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.album.XImageUtil;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * 悬赏发布
 */

public class RewardEditView extends LinearLayout{
    private static final int imageNum = 9;// 图片个数
    private static final int REQUEST_CAPTURE = 0x000001;
    private static final int REQUEST_PICK = 0x000002;
    private boolean inited;

    private HorizontalListView photoView;
    private TextView photoNumTv;
    private ActionSheet actionSheet;
    private RewardEditView.MyAdapter adapter;

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

        //状态栏透明
        HeaderUtil.initRewardPublishStatusBar((Activity) getContext(),this);

        photoView = (HorizontalListView) findViewById(R.id.photoView);
        photoNumTv = (TextView) findViewById(R.id.tv_photo_num);

        initPhone();
    }

    public void initPhone(){
        photoView.setAdapter(adapter = new RewardEditView.MyAdapter(getContext()));
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

    private class MyAdapter extends BaseAdapter {
        private Context context;

        public MyAdapter(Context context) {
            this.context = context;
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
                XImageUtil.getInstance().loadImage(holder.image, AlbumOpt.selectImages.get(position).getImagePath());
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
}

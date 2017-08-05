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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.common.AlbumActivity;
import net.wangtu.android.common.util.FileUtil;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.view.HorizontalListView;
import net.wangtu.android.common.view.dialog.ActionSheet;
import net.wangtu.android.util.HeaderUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.album.XImageUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * 悬赏发布
 */

public class RewardReadView extends View{
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    private JSONObject dataJson;
    private static final int imageNum = 9;// 图片个数
    private boolean inited;

    private TextView txtRewardTitle;
    private ImageView txtRewardTitleImage;
    private TextView txtRewardCatalog;
    private TextView txtRewardLocation;
    private TextView txtRewardDeadline;
    private TextView txtRewardDescription;
    private ImageView txtRewardDescriptionImage;
    private RelativeLayout rewardDescriptionRelative;
    private RelativeLayout rewardTitleRelative;
    private HorizontalListView photoView;
    private TextView txtRewardPrice;
    private RelativeLayout rewardPhoneRelative;
    private TextView txtRewardPhone;

    private RewardReadView.MyAdapter adapter;

    public RewardReadView(Context context) {
        this(context, null);
    }

    public RewardReadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RewardReadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //模拟数据后面需要删除
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(inited){
            return;
        }
        inited = true;

        initUI();
    }

    private void initUI(){
        final View containerView = (View)getParent();
        rewardTitleRelative =  (RelativeLayout) containerView.findViewById(R.id.reward_title_relative);
        txtRewardTitle = (TextView) containerView.findViewById(R.id.reward_title);
        txtRewardTitleImage = (ImageView) containerView.findViewById(R.id.reward_title_image);
        txtRewardCatalog = (TextView) containerView.findViewById(R.id.reward_catalog);
        txtRewardLocation = (TextView) containerView.findViewById(R.id.reward_location);
        txtRewardDeadline = (TextView) containerView.findViewById(R.id.reward_deadline);
        rewardDescriptionRelative =  (RelativeLayout) containerView.findViewById(R.id.reward_description_relative);
        txtRewardDescription = (TextView) containerView.findViewById(R.id.reward_description);
        txtRewardDescriptionImage = (ImageView) containerView.findViewById(R.id.reward_description_image);
        txtRewardPrice = (TextView) containerView.findViewById(R.id.reward_price);
        photoView = (HorizontalListView) containerView.findViewById(R.id.photoView);
        rewardPhoneRelative = (RelativeLayout) containerView.findViewById(R.id.reward_phone_relative);
        txtRewardPhone = (TextView) containerView.findViewById(R.id.reward_phone);
    }

    public void initData(final JSONObject dataJson){
        this.dataJson = dataJson;
        txtRewardTitle.setText(dataJson.optString("title"));
        txtRewardCatalog.setText(dataJson.optString("cataName"));
        txtRewardLocation.setText(dataJson.optString("location"));
        Date deadline = new Date(dataJson.optLong("deadline"));
        txtRewardDeadline.setText(dateFormat.format(deadline));
        txtRewardDescription.setText(dataJson.optString("description"));
        txtRewardPrice.setText(dataJson.optString("price"));
        txtRewardPhone.setText(dataJson.optString("phone"));

        //图片展示
        JSONArray images = dataJson.optJSONArray("pictures");
        if(images == null || images.length() <= 0){
            photoView.setVisibility(View.GONE);
        }else{
            photoView.setVisibility(View.VISIBLE);
            photoView.setAdapter(adapter = new RewardReadView.MyAdapter(getContext(),dataJson.optJSONArray("pictures")));
        }

        //title
        rewardTitleRelative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtRewardTitleImage.getVisibility() == View.VISIBLE){
                    txtRewardTitle.setSingleLine(false);
                    txtRewardTitleImage.setVisibility(View.INVISIBLE);
                }else{
                    txtRewardTitle.setSingleLine(true);
                    txtRewardTitleImage.setVisibility(View.VISIBLE);
                }
            }
        });
        //detail
        rewardDescriptionRelative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtRewardDescriptionImage.getVisibility() == View.VISIBLE){
                    txtRewardDescription.setSingleLine(false);
                    txtRewardDescriptionImage.setVisibility(View.INVISIBLE);
                }else{
                    txtRewardDescription.setSingleLine(true);
                    txtRewardDescriptionImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        private Context context;
        private JSONArray jsonArray;

        public MyAdapter(Context context,JSONArray jsonArray) {
            this.context = context;
            this.jsonArray = jsonArray;
        }

        @Override
        public int getCount() {
            if(jsonArray == null){
                return 0;
            }
            return jsonArray.length();
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
            RewardReadView.MyAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new RewardReadView.MyAdapter.ViewHolder();
                convertView = new FrameLayout(context);
                convertView.setLayoutParams(new AbsListView.LayoutParams(Util.dip2px(context, 100), Util.dip2px(context, 100)));

                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ((FrameLayout)convertView).addView(imageView);

                holder.image = imageView;
                convertView.setTag(holder);
            } else {
                holder = (RewardReadView.MyAdapter.ViewHolder) convertView.getTag();
            }

            try {
                XImageUtil.getInstance().loadImage(holder.image, WangTuUtil.getPage(jsonArray.optJSONObject(position).optString("filePath")));
            }catch (Exception e){

            }
            return convertView;
        }

        private class ViewHolder {
            public ImageView image;
        }
    }

    public void showPhone(){
        rewardPhoneRelative.setVisibility(View.VISIBLE);
    }
}

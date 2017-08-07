package net.wangtu.android.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.HomeActivity;
import net.wangtu.android.activity.LoginActivity;
import net.wangtu.android.common.util.ImageCacheUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.HorizontalListView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.album.XImageUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 评论列表
 */

public class UserInfoCommentItemView extends LinearLayout{
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private UserInfoCommentView userInfoCommentView;

    private ImageView userPhoto;
    private TextView userName;
    private TextView publishTime;
    private TextView btnReply;
    private UserInfoStarView serviceAttitude;
    private TextView serviceAttitudeTxt;
    private UserInfoStarView serviceQuality;
    private TextView serviceQualityTxt;
    private TextView textReply;
    private LinearLayout linearLayoutReply;
    private HorizontalListView photoView;


    private JSONObject dataJson;
    private String commentId;
    private MyAdapter adapter;

    public UserInfoCommentItemView(Context context) {
        this(context, null);
    }

    public UserInfoCommentItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserInfoCommentItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initUI(){
        userPhoto = (ImageView) findViewById(R.id.item_user_photo);
        userName = (TextView) findViewById(R.id.item_user_name);
        publishTime = (TextView)findViewById(R.id.item_publish_time);
        btnReply = (TextView)findViewById(R.id.item_btn_reply);
        serviceAttitude = (UserInfoStarView)findViewById(R.id.item_service_attitude);
        serviceAttitudeTxt = (TextView)findViewById(R.id.item_service_attitude_txt);
        serviceQuality = (UserInfoStarView)findViewById(R.id.item_service_quality);
        serviceQualityTxt = (TextView)findViewById(R.id.item_service_quality_txt);
        textReply = (TextView) findViewById(R.id.item_text_reply);
        linearLayoutReply = (LinearLayout) findViewById(R.id.item_linearLayout_reply);
        photoView = (HorizontalListView) findViewById(R.id.item_photos);

        //回复
        btnReply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup container = (ViewGroup)((Activity)getContext()).findViewById(android.R.id.content);
                UserInfoCommentReplyView replyView = new UserInfoCommentReplyView(container);
                replyView.initData(commentId);
                replyView.setCloseListner(new Runnable() {
                    @Override
                    public void run() {
                        userInfoCommentView.post(new Runnable() {
                            @Override
                            public void run() {
                                userInfoCommentView.refreshData();
                            }
                        });
                    }
                });
                replyView.show();
            }
        });
    }

    public void initData(JSONObject dataJson,UserInfoCommentView userInfoCommentView,int position){
        this.userInfoCommentView = userInfoCommentView;
        commentId = dataJson.optString("id");
        ImageCacheUtil.lazyLoad(userPhoto,WangTuUtil.getPage(dataJson.optString("avatarFile")),R.drawable.icon_user_info_person2,false);
        userName.setText(dataJson.optString("userName"));
        publishTime.setText( dateFormat.format(new Date(dataJson.optLong("commentTime"))));
        serviceAttitude.initData(dataJson.optDouble("serviceAttitude"));
        serviceAttitudeTxt.setText(dataJson.optString("serviceAttitudeContent"));
        serviceQuality.initData(dataJson.optDouble("serviceQuality"));
        serviceQualityTxt.setText(dataJson.optString("serviceQualityContent"));
        if(!dataJson.isNull("replyContent")){
            textReply.setText(dataJson.optString("replyContent"));
            textReply.setEnabled(false);
            btnReply.setVisibility(GONE);
        }else{
            textReply.setEnabled(true);
            btnReply.setVisibility(VISIBLE);
        }

        //图片展示
        JSONArray images = dataJson.optJSONArray("commentPictures");
        if(images == null || images.length() <= 0){
            photoView.setVisibility(View.GONE);
        }else{
            photoView.setVisibility(View.VISIBLE);
            photoView.setAdapter(adapter = new MyAdapter(getContext(),dataJson.optJSONArray("commentPictures")));
        }
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
            MyAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new MyAdapter.ViewHolder();
                convertView = new FrameLayout(context);
                convertView.setLayoutParams(new AbsListView.LayoutParams(Util.dip2px(context, 100), Util.dip2px(context, 100)));

                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ((FrameLayout)convertView).addView(imageView);

                holder.image = imageView;
                convertView.setTag(holder);
            } else {
                holder = (MyAdapter.ViewHolder) convertView.getTag();
            }

            try {
                XImageUtil.getInstance().loadImage(holder.image, WangTuUtil.getPage(jsonArray.optJSONObject(position).optString("picturePath")));
            }catch (Exception e){

            }
            return convertView;
        }

        private class ViewHolder {
            public ImageView image;
        }
    }

    public int getItemHeight() {
        measure(0,0);
        int width = Util.getWindowWidth(getContext()) - Util.dip2px(getContext(),80);
        int height = 0;
        int textHeight = Util.dip2px(getContext(),16);
        int minTextHeight = Util.dip2px(getContext(),40);

        int vPadding = Util.dip2px(getContext(),20);
        vPadding += Util.dip2px(getContext(),40);
        serviceAttitudeTxt.measure(0,0);
        height = ( serviceAttitudeTxt.getMeasuredWidth() / width  + ((serviceAttitudeTxt.getMeasuredWidth() % width) > 0 ? 1 : 0)) * textHeight;
        if(height < minTextHeight){
            height = minTextHeight;
        }
        Log.d("getItemHeight","serviceAttitudeTxt:" + height);
        vPadding += height;
        vPadding += Util.dip2px(getContext(),30);
        serviceQualityTxt.measure(0,0);
        height = ( serviceQualityTxt.getMeasuredWidth() / width  + ((serviceQualityTxt.getMeasuredWidth() % width) > 0 ? 1 : 0)) * textHeight;
        if(height < minTextHeight){
            height = minTextHeight;
        }
        Log.d("getItemHeight","serviceQualityTxt:" + height + "_" + serviceQualityTxt.getMeasuredWidth());
        vPadding += height;
        if(photoView.getVisibility() == VISIBLE){
            vPadding += Util.dip2px(getContext(),100);
        }
        if(linearLayoutReply.getVisibility() == VISIBLE){
            textReply.measure(0,0);
            height = ( textReply.getMeasuredWidth() / width  + ((textReply.getMeasuredWidth() % width) > 0 ? 1 : 0)) * textHeight;
            if(height < minTextHeight){
                height = minTextHeight;
            }
            Log.d("getItemHeight","textReply:" + height);
            vPadding += height;
        }
        return vPadding;
    }
}

package net.wangtu.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.common.util.ImageCacheUtil;
import net.wangtu.android.util.WangTuUtil;

import org.json.JSONObject;

import java.util.Date;

/**
 * 我的悬赏
 */

public class HomeFragmentItemView extends RelativeLayout{
    private JSONObject dataJson;
    private ImageView txtRewardImg;
    private TextView txtRewardTitle;
    private TextView txtRewardPrice;
    private TextView txtRewardCatalog;
    private TextView txtCreateTime;

    public HomeFragmentItemView(Context context) {
        this(context, null);
    }

    public HomeFragmentItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeFragmentItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initUI(){
        txtRewardImg = (ImageView)findViewById(R.id.reward_img);
        txtRewardTitle = (TextView)findViewById(R.id.reward_title);
        txtRewardPrice = (TextView)findViewById(R.id.reward_price);
        txtRewardCatalog = (TextView)findViewById(R.id.reward_catalog);
        txtCreateTime = (TextView)findViewById(R.id.reward_create_time);
    }

    public void initData(JSONObject dataJson,int posision){
        ImageCacheUtil.lazyLoad(txtRewardImg, WangTuUtil.getPage(dataJson.optString("picturePath")),R.drawable.icon_reward_pic,false);
        txtRewardTitle.setText("主题：" + dataJson.optString("title"));
        txtRewardPrice.setText("悬赏金额：" + dataJson.optString("price"));
        txtRewardCatalog.setText("分类：" + dataJson.optString("cataName"));
        txtCreateTime.setText("发布时间：" + getCreateTime(dataJson.optLong("createTime")));
    }

    long year = 365 * 24 * 60 * 60 * 1000;
    long month = 30 * 24 * 60 * 60 * 1000;
    long day =  24 * 60 * 60 * 1000;
    long hour =   60 * 60 * 1000;
    long minute =   60 * 60 * 1000;
    private String getCreateTime(long time){
        long timeDiff = new Date().getTime() - time;
        long diff = timeDiff / year;
        if(diff > 0){
            return diff + "年之前";
        }
        diff = timeDiff / month;
        if(diff > 0){
            return diff + "月之前";
        }
        diff = timeDiff / day;
        if(diff > 0){
            return diff + "天之前";
        }
        diff = timeDiff / hour;
        if(diff > 0){
            return diff + "小时之前";
        }
        diff = timeDiff / minute;
        if(diff <= 0){
            diff = 1;
        }
        return diff + "分钟之前";
    }
}

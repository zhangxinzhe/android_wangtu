package net.wangtu.android.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.UserInfoActivity;
import net.wangtu.android.common.util.ImageCacheUtil;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.util.WangTuUtil;

import org.json.JSONObject;

/**
 * Created by zhangxz on 2017/7/9.
 */

public class MyRewardDetailBiddingItemView extends RelativeLayout{
    private ImageView itemRadio;
    private ImageView itemDesignerImg;
    private TextView itemExpectPrice;
    private TextView itemDesigner;
    private TextView itemMoreInfo;
    private JSONObject dataJson;
    private String designerUserId;

    public MyRewardDetailBiddingItemView(Context context) {
        this(context, null);
    }

    public MyRewardDetailBiddingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRewardDetailBiddingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initUI(){
        itemRadio = (ImageView)findViewById(R.id.item_radio);
        itemDesignerImg = (ImageView)findViewById(R.id.item_designer_img);
        itemExpectPrice = (TextView)findViewById(R.id.item_expect_price);
        itemDesigner = (TextView)findViewById(R.id.item_designer);
        itemMoreInfo = (TextView)findViewById(R.id.item_more_info);
        itemMoreInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),UserInfoActivity.class);
                if(!ValidateUtil.isBlank(designerUserId)){
                    intent.putExtra("userId",designerUserId);
                    getContext().startActivity(intent);
                }
            }
        });
    }

    public void initData(JSONObject dataJson,int position){
        designerUserId = dataJson.optString("userId");
        itemDesigner.setText(dataJson.optString("userName"));
        itemExpectPrice.setText("期望赏金："+dataJson.optString("price"));
        ImageCacheUtil.lazyLoad(itemDesignerImg, WangTuUtil.getPage(dataJson.optString("avatarFile")),R.drawable.reward_image,true);
    }

    public void checked(boolean checked){
        if(checked){
            itemRadio.setImageResource(R.drawable.icon_radio_checked);
        }else{
            itemRadio.setImageResource(R.drawable.icon_radio);
        }
    }
}

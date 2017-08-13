package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ImageCacheUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.dialog.BoxView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.view.RewardReadView;

import org.json.JSONObject;

/**
 * 悬赏任务结果
 */

public class MyRewardDetailResultActivity extends BaseActivity{
    private String rewardId;
    private RewardReadView rewardReadView;
    private TextView txtRewardExpectPrice;
    private TextView txtRewardDesigner;
    private ImageView txtRewardDesignerImg;
    private TextView txtRewardResult;
    private LinearLayout txtRewardResultRelative;
    private LinearLayout txtRewardBtnRelative;
    private JSONObject dataJson;
    private String designerUserId;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_reward_detail_result);
        initHeader("详情",true);

        Bundle bundle = getIntent().getExtras();
        rewardId = bundle.getString("rewardId");
        initUI();
        initData();
    }

    private void initUI(){
        rewardReadView = (RewardReadView)findViewById(R.id.reward_read_view);
        txtRewardExpectPrice = (TextView)findViewById(R.id.reward_expect_price);
        txtRewardDesigner = (TextView)findViewById(R.id.reward_designer);
        txtRewardDesignerImg = (ImageView)findViewById(R.id.reward_designer_img);
        txtRewardResult = (TextView)findViewById(R.id.reward_result);
        txtRewardResultRelative = (LinearLayout) findViewById(R.id.reward_result_relative);
        txtRewardBtnRelative = (LinearLayout) findViewById(R.id.reward_btn_relative);
    }

    private void initData(){
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_BIDDING_DETAIL) + "?rewardId=" + rewardId;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,MyRewardDetailResultActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyRewardDetailResultActivity.this);
                            JSONObject reward = dataJson.optJSONObject("reward");
                            if(reward != null){
                                rewardReadView.initData(reward);
                                rewardReadView.showPhone();
                                JSONObject biddingDetail = reward.optJSONObject("biddingDetail");
                                if(biddingDetail != null){
                                    designerUserId =  biddingDetail.optString("userId");
                                    txtRewardDesigner.setText(biddingDetail.optString("userName"));
                                    txtRewardExpectPrice.setText("期望赏金：" + biddingDetail.optString("price"));
                                }

                                ImageCacheUtil.lazyLoad(txtRewardDesignerImg, WangTuUtil.getPage(dataJson.optString("avatarFile")),R.drawable.reward_image,true);

                                String rewardStatus = reward.optString("status");
                                if(Constants.REWARD_STATUS_CREATE.equals(rewardStatus)){
                                    txtRewardResult.setText("悬赏未发布");
                                    txtRewardResultRelative.setVisibility(View.VISIBLE);
                                }else if(Constants.REWARD_STATUS_PUBLISH.equals(rewardStatus)){
                                    txtRewardResult.setText("悬赏发布中");
                                    txtRewardResultRelative.setVisibility(View.VISIBLE);
                                }else if(Constants.REWARD_STATUS_DOING.equals(rewardStatus)){
                                    txtRewardBtnRelative.setVisibility(View.VISIBLE);
                                }else if(Constants.REWARD_STATUS_FINISH.equals(rewardStatus)){
                                    txtRewardResult.setText("任务已完成");
                                    txtRewardResultRelative.setVisibility(View.VISIBLE);
                                }else if(Constants.REWARD_STATUS_CANCEL.equals(rewardStatus)){
                                    txtRewardResult.setText("悬赏已撤销");
                                    txtRewardResultRelative.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyRewardDetailResultActivity.this);
                            showError();
                        }
                    });
                }
            }
        });
    }

    public void finishRewardOnClick(View v){
        ToastUtil.confirm(this, "确定将此任务设置成已完成？", new ToastUtil.DialogOnClickListener() {
            @Override
            public void onClick(BoxView dialog) {
                startLoading();
                ThreadUtils.schedule(new Runnable() {
                    @Override
                    public void run() {
                        String url = WangTuUtil.getPage(Constants.API_REWARD_FINISH) + "?rewardId=" + rewardId;
                        try {
                            final JSONObject dataJson = WangTuHttpUtil.getJson(url,MyRewardDetailResultActivity.this);
                            if(dataJson != null){
                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopLoading();
                                        if("success".equals(dataJson.optString("msg"))){
                                            ToastUtil.alert(MyRewardDetailResultActivity.this, "操作成功！", new ToastUtil.DialogOnClickListener() {
                                                @Override
                                                public void onClick(BoxView dialog) {
                                                    Intent intent = new Intent(MyRewardDetailResultActivity.this,TaskCommentActivity.class);
                                                    intent.putExtra("rewardId",rewardId);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }else{
                                            ToastUtil.error(getApplicationContext(),dataJson.optString("msg"));
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    stopLoading();
                                    ToastUtil.error(getApplicationContext(),"操作失败");
                                }
                            });
                        }
                    }
                });
            }
        },null);
    }

    public void backOnClick(View v){
        finish();
    }

    public void showDesignerInfoOnclick(View v){
        Intent intent = new Intent(this,UserInfoActivity.class);
        if(!ValidateUtil.isBlank(designerUserId)){
            intent.putExtra("userId",designerUserId);
            startActivity(intent);
        }
    }
}

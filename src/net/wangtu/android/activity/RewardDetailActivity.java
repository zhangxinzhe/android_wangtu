package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.dialog.BoxView;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.view.RewardReadView;

import org.json.JSONObject;

/**
 * Created by zhangxz on 2017/7/6.
 */
public class RewardDetailActivity extends BaseActivity{
    private EditText txtRewardExpectPrice;
    private RewardReadView rewardReadView;
    private String rewardId;
    private JSONObject dataJson;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.reward_detail);

        Bundle bundle = getIntent().getExtras();
        rewardId = bundle.getString("rewardId");
        initUI();
        initData();
    }

    private void initUI(){
        txtRewardExpectPrice = (EditText)findViewById(R.id.reward_expect_price);
        rewardReadView = (RewardReadView)findViewById(R.id.reward_read_view);
        initHeader("详情",true);
    }

    private void initData(){
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_REWARD_DETAIL) + "?rewardId=" + rewardId;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,RewardDetailActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(RewardDetailActivity.this);
                            rewardReadView.initData(dataJson.optJSONObject("reward"));
                        }
                    });

                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(RewardDetailActivity.this);
                            showError();
                        }
                    });
                }
            }
        });
    }

    public void biddingOnClick(View view){
        final ConfirmView confirmView = new ConfirmView(RewardDetailActivity.this);
        confirmView.setTitleMsg("竞价申请提示！");
        confirmView.setContentMsg("点击继续，将提交申请？");
        confirmView.setOkBtnName("继续");
        confirmView.setOkBtnListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmView.dismiss();
                applyBidding();
            }
        });
        confirmView.show();
    }

    public void applyBidding(){
        final String expectPrice = txtRewardExpectPrice.getText().toString();
        if(ValidateUtil.isBlank(expectPrice)){
            ToastUtil.error(this,"请填写期望赏金金额！");
            return;
        }
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_ADD_REWARD_BIDDING) + "?rewardId=" + rewardId + "&price=" + expectPrice;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,RewardDetailActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            stopLoading();
                            if("success".equals(dataJson.optString("msg"))){
                                ToastUtil.confirm(RewardDetailActivity.this,"提示", "支付完平台使用费，才能开始竞价！","支付","取消", new ToastUtil.DialogOnClickListener() {
                                    @Override
                                    public void onClick(BoxView dialog) {
                                        //跳去支付平台使用费
                                        Intent intent = new Intent(RewardDetailActivity.this,PlatPayActivity.class);
                                        intent.putExtra("rewardId",rewardId);
                                        intent.putExtra("rewardPrice",dataJson.optString("rewardPrice"));
                                        intent.putExtra("platPrice",dataJson.optString("platPrice"));
                                        intent.putExtra("userBalance",dataJson.optString("userBalance"));
                                        intent.putExtra("platPercent",dataJson.optString("platPercent"));
                                        startActivity(intent);
                                        finish();
                                    }
                                },null);
                            }else{
                                ToastUtil.error(RewardDetailActivity.this,dataJson.optString("msg"));
                            }
                        }
                    });
                } catch(final Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(RewardDetailActivity.this);
                            ToastUtil.error(RewardDetailActivity.this,e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void cancelOnClick(View view){
        finish();
    }
}

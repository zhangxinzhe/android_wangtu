package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ThreadUtils;
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

public class MyTaskDetailBiddingActivity extends BaseActivity{
    private String rewardId;
    private EditText txtRewardExpectPrice;
    private RewardReadView rewardReadView;
    private JSONObject dataJson;
    private String biddingId;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_task_detail_bidding);
        initHeader("详情",true);

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
                String url = WangTuUtil.getPage(Constants.API_BIDDING_DETAIL) + "?rewardId=" + rewardId;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,MyTaskDetailBiddingActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyTaskDetailBiddingActivity.this);
                            JSONObject reward = dataJson.optJSONObject("reward");
                            if(reward != null){
                                rewardReadView.initData(reward);
                                JSONObject biddingDetail = reward.optJSONObject("biddingDetail");
                                biddingId = biddingDetail.optString("id");
                                txtRewardExpectPrice.setText(biddingDetail.optString("price"));
                            }
                        }
                    });
                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyTaskDetailBiddingActivity.this);
                            showError();
                        }
                    });
                }
            }
        });
    }

    /**
     * 撤销竞价
     * @param view
     */
    public void cancelTaskOnclick(View view){
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_CANCEL_BIDDING) + "?biddingId=" + biddingId;
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,MyTaskDetailBiddingActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyTaskDetailBiddingActivity.this);
                            if("success".equals(dataJson.optString("msg"))){
                                ToastUtil.alert(MyTaskDetailBiddingActivity.this, "撤销成功！", new ToastUtil.DialogOnClickListener() {
                                    @Override
                                    public void onClick(BoxView dialog) {
                                        finish();
                                    }
                                });
                            }else{
                                ToastUtil.error(getApplicationContext(),dataJson.optString("msg"));
                            }
                        }
                    });
                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyTaskDetailBiddingActivity.this);
                            ToastUtil.error(MyTaskDetailBiddingActivity.this,"处理失败！");
                        }
                    });
                }
            }
        });
    }

    public void finishTaskOnclick(View view){
        finish();
    }
}

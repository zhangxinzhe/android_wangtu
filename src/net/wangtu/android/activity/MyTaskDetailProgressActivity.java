package net.wangtu.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.view.RewardReadView;

import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * 任务完成进度
 */

public class MyTaskDetailProgressActivity extends BaseActivity{
    private String rewardId;
    private EditText txtRewardExpectPrice;
    private TextView txtRewardResult;
    private RewardReadView rewardReadView;
    private JSONObject dataJson;
    private String biddingId;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_task_detail_progress);
        initHeader("详情",true);

        Bundle bundle = getIntent().getExtras();
        rewardId = bundle.getString("rewardId");
        initUI();
        initData();
    }

    private void initUI(){
        txtRewardExpectPrice = (EditText)findViewById(R.id.reward_expect_price);
        rewardReadView = (RewardReadView)findViewById(R.id.reward_read_view);
        txtRewardResult = (TextView) findViewById(R.id.reward_result);
        initHeader("详情",true);
    }

    private void initData(){
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_BIDDING_DETAIL) + "?rewardId=" + rewardId;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,MyTaskDetailProgressActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyTaskDetailProgressActivity.this);
                            JSONObject reward = dataJson.optJSONObject("reward");
                            if(reward != null){
                                rewardReadView.initData(reward);
                                rewardReadView.showPhone();
                                JSONObject biddingDetail = reward.optJSONObject("biddingDetail");
                                biddingId = biddingDetail.optString("id");
                                txtRewardExpectPrice.setText(biddingDetail.optString("price"));

                                //进价状态
                                String rewardStatus = biddingDetail.optString("status");
                                // 未支付竞价
                                if(Constants.Bidding_STATUS_UNPAY.equals(rewardStatus)){
                                    txtRewardResult.setText("未支付平台使用费");
                                    // 已支付，竞价中
                                }else if(Constants.Bidding_STATUS_PAY.equals(rewardStatus)){
                                    txtRewardResult.setText("竞价中");
                                    // 竞价成功
                                }else if(Constants.Bidding_STATUS_SUCCESS.equals(rewardStatus)){
                                    txtRewardResult.setText("任务进行中");
                                    // 任务完成
                                }else if(Constants.Bidding_STATUS_FINISH.equals(rewardStatus)){
                                    txtRewardResult.setText("任务完成");
                                    // 竞价失败
                                }else if(Constants.Bidding_STATUS_FAIL.equals(rewardStatus)){
                                    txtRewardResult.setText("竞价失败");
                                    // 任务撤销
                                }else if(Constants.Bidding_USER_CANCEL.equals(rewardStatus)){
                                    txtRewardResult.setText("放弃竞价");
                                    // 发布撤销
                                }else if(Constants.Bidding_USER_CANCEL.equals(rewardStatus)){
                                    txtRewardResult.setText("发布撤销");
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyTaskDetailProgressActivity.this);
                            showError();
                        }
                    });
                }
            }
        });
    }

    public void taskFinishedOnclick(View view){
        finish();
    }

    public void cancelOnclick(View view){
        finish();
    }
}

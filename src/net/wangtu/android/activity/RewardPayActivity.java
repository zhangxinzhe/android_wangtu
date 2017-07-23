package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.view.dialog.AlertView;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.util.pay.WechatPayUtil;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class RewardPayActivity extends BaseActivity{
    private WechatPayUtil.WechatPayCallBack callBack;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.reward_pay);

        //titleText = (TextView)findViewById(R.id.titleText);

        initHeader("在线支付",true);
    }

    public void payOnClick(View view){
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                WechatPayUtil.pay(RewardPayActivity.this,null,callBack = new WechatPayUtil.WechatPayCallBack(){
                    @Override
                    public void callback(BaseResp resp){
                        //成功
                        if(resp.errCode == 0){
                            final ConfirmView confirmView = new ConfirmView(RewardPayActivity.this);
                            confirmView.setTitleMsg("支付成功！");
                            confirmView.setContentMsg("您已成功支付悬赏保证金");
                            confirmView.setOkBtnName("确定");
                            confirmView.setOkBtnListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    confirmView.dismiss();
                                    Intent intent = new Intent(RewardPayActivity.this,MyTaskActivity.class);
                                    intent.putExtra("type","myReward");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            confirmView.show();

                        }
                        //取消
                        else if(resp.errCode == -2){
                            final AlertView alertView = new AlertView(RewardPayActivity.this);
                            alertView.setTitleMsg("取消！");
                            alertView.setContentMsg("您已成功支付悬赏保证金");
                            alertView.setOkBtnName("确定");
                            alertView.setOkBtnListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    alertView.dismiss();
                                    Intent intent = new Intent(RewardPayActivity.this,MyTaskActivity.class);
                                    intent.putExtra("type","myReward");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alertView.show();
                        }
                        //错误
                        else{
                            final AlertView alertView = new AlertView(RewardPayActivity.this);
                            alertView.setTitleMsg("错误！");
                            alertView.setContentMsg("您已成功支付悬赏保证金");
                            alertView.setOkBtnName("确定");
                            alertView.setOkBtnListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    alertView.dismiss();
                                    Intent intent = new Intent(RewardPayActivity.this,MyTaskActivity.class);
                                    intent.putExtra("type","myReward");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alertView.show();
                        }
                    }
                });
            }
        });
    }

}

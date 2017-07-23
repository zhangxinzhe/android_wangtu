package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.util.pay.AlipayUtil;
import net.wangtu.android.util.pay.WechatPayUtil;

/**
 * 违约金支付
 *
 */

public class LiquidatedDamagesPayActivity extends BaseActivity{
    private ImageView alipayView;
    private ImageView wechatView;
    private int payType;//0:alipay 1:wechat

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.liquidated_damages_pay);

        initHeader("在线支付",true);
        alipayView = (ImageView)findViewById(R.id.radio_alipay);
        wechatView = (ImageView)findViewById(R.id.radio_wechat);
    }

    public void alipayOnClick(View view){
        payType = 0;
        alipayView.setImageResource(R.drawable.icon_pay_radio_checked);
        wechatView.setImageResource(R.drawable.icon_pay_radio);
    }

    public void wechatOnClick(View view){
        payType = 1;
        alipayView.setImageResource(R.drawable.icon_pay_radio);
        wechatView.setImageResource(R.drawable.icon_pay_radio_checked);
    }

    public void payOnClick(final View view){
        if(payType == 0){
            AlipayUtil.pay(this,"https://m.kehou.com/mobileConfig.htm", new AlipayUtil.AlipayCallBack(){
                @Override
                public void callback(String result) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            payCallBack();
                        }
                    });
                }
            });
        }else{
            WechatPayUtil.pay(this,"https://m.kehou.com/mobileConfig.htm", new  WechatPayUtil.WechatPayCallBack(){

                @Override
                public void callback(BaseResp resp) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            payCallBack();
                        }
                    });
                }
            });
        }
    }

    private void payCallBack(){
        final ConfirmView confirmView = new ConfirmView(LiquidatedDamagesPayActivity.this);
        confirmView.setTitleMsg("支付成功！");
        confirmView.setContentMsg("您已成功支付平台使用费，您将寻找新的任务还是查看我的任务？");
        confirmView.setOkBtnName("回到主页");
        confirmView.setOkBtnListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmView.dismiss();
                Intent intent = new Intent(LiquidatedDamagesPayActivity.this,HomeActivity.class);
                intent.putExtra("type","myTask");
                startActivity(intent);
                finish();
            }
        });
        confirmView.setCancelBtnName("我的任务");
        confirmView.setCancelBtnListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmView.dismiss();
                Intent intent = new Intent(LiquidatedDamagesPayActivity.this,MyTaskActivity.class);
                intent.putExtra("type","myTask");
                startActivity(intent);
                finish();
            }
        });
        confirmView.show();
    }
}

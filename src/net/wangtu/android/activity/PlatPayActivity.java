package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.JsonUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.view.dialog.BoxView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.pay.AlipayUtil;
import net.wangtu.android.util.pay.WechatPayUtil;
import net.wangtu.android.util.xhttp.XHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 平台使用费支付
 *
 */
public class PlatPayActivity extends BaseActivity{
    private ImageView balanceView;
    private ImageView alipayView;
    private ImageView wechatView;
    private TextView orderPrice;
    private TextView orderInfo;
    private int payType;//0:balance 1:alipay 2:wechat
    private String rewardId;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.plat_pay);

        Bundle bundle = getIntent().getExtras();
        rewardId = bundle.getString("rewardId");
        String rewardPrice = bundle.getString("rewardPrice");
        String platPrice = bundle.getString("platPrice");
        String userBalance = bundle.getString("userBalance");
        String platPercent = bundle.getString("platPercent");

        initHeader("在线支付",true);
        orderPrice = (TextView)findViewById(R.id.order_price);
        orderInfo = (TextView)findViewById(R.id.order_info);
        balanceView = (ImageView)findViewById(R.id.radio_balance);
        alipayView = (ImageView)findViewById(R.id.radio_alipay);
        wechatView = (ImageView)findViewById(R.id.radio_wechat);

        orderInfo.setText("请支付平台使用费完成接单(平台使用费为赏金" + rewardPrice + "的" + platPercent + ")。");
        orderPrice.setText("￥" + platPrice);
    }

    public void balanceOnClick(View view){
        payType = 0;
        balanceView.setImageResource(R.drawable.icon_pay_radio_checked);
        alipayView.setImageResource(R.drawable.icon_pay_radio);
        wechatView.setImageResource(R.drawable.icon_pay_radio);
    }

    public void alipayOnClick(View view){
        payType = 1;
        balanceView.setImageResource(R.drawable.icon_pay_radio);
        alipayView.setImageResource(R.drawable.icon_pay_radio_checked);
        wechatView.setImageResource(R.drawable.icon_pay_radio);
    }

    public void wechatOnClick(View view){
        payType = 2;
        balanceView.setImageResource(R.drawable.icon_pay_radio);
        alipayView.setImageResource(R.drawable.icon_pay_radio);
        wechatView.setImageResource(R.drawable.icon_pay_radio_checked);
    }

    public void payOnClick(final View view){
        final String url = WangTuUtil.getPage(Constants.API_CREATE_BIDDING_ORDER) + "?rewardId=" + rewardId + "&payType=" + payType;
        startLoading();
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,PlatPayActivity.this);
                    if(dataJson != null){
                        post(new Runnable() {
                            @Override
                            public void run() {
                                stopLoading();
                                if("success".equals(dataJson.optString("msg"))){
                                    String payContent = dataJson.optString("payContent");
                                    //支付宝支付
                                    if(payType == 1){
                                        AlipayUtil.pay(PlatPayActivity.this, payContent, new AlipayUtil.AlipayCallBack(){
                                            @Override
                                            public void callback(final String result) {
                                                view.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        payCallBack(result);
                                                    }
                                                });
                                            }
                                        });
                                    //微信支付
                                    }else if(payType == 2){
                                        WechatPayUtil.pay(PlatPayActivity.this,payContent, new  WechatPayUtil.WechatPayCallBack(){
                                            @Override
                                            public void callback(final BaseResp resp) {
                                                view.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12&index=2
                                                        //0	成功	展示成功页面
                                                        //-1	错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
                                                        //-2	用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
                                                        payCallBack(resp.errCode + "");
                                                    }
                                                });
                                            }
                                        });
                                    //余额支付
                                    }else{
                                        ToastUtil.alert(PlatPayActivity.this, "支付成功", new ToastUtil.DialogOnClickListener() {
                                            @Override
                                            public void onClick(BoxView dialog) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(PlatPayActivity.this,MyTaskActivity.class);
                                                intent.putExtra("type","myTask");
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(),dataJson.optString("msg"),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            stopLoading();
                            Toast.makeText(getApplicationContext(),"提交失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void payCallBack(String payResult){
        Map<String,String> params = new HashMap<String,String>();
        params.put("rewardId",rewardId);
        params.put("payType",payType + "");
        params.put("payResult",payResult);
        String url = WangTuUtil.getPage(Constants.API_FINISH_BIDDING_ORDER);
        startLoading();
        XHttpUtil.getInstance().upLoadFile(url, params, null, new XHttpUtil.XCallBack() {
            @Override
            public void onSuccess(Object result) {
                stopLoading();
                try {
                    JSONObject dataJson = JsonUtil.parseJson((String) result);
                    if("success".equals(dataJson.optString("msg"))){
                        ToastUtil.alert(PlatPayActivity.this, "支付成功", new ToastUtil.DialogOnClickListener() {
                            @Override
                            public void onClick(BoxView dialog) {
                                dialog.dismiss();
                                Intent intent = new Intent(PlatPayActivity.this,MyTaskActivity.class);
                                intent.putExtra("type","myTask");
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else{
                        ToastUtil.error(PlatPayActivity.this, dataJson.optString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                stopLoading();
                ToastUtil.error(PlatPayActivity.this, "网络错误");
            }
        });
    }

}

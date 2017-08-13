package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ImageCacheUtil;
import net.wangtu.android.common.util.JsonUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.ValidateUtil;
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
public class UserBalanceActivity extends BaseActivity{
    private ImageView alipayView;
    private ImageView wechatView;
    private TextView balancePrice;
    private EditText userName;
    private EditText userPhone;
    private EditText userAccount;
    private int payType = 1;//1:alipay 2:wechat
    private String rewardId;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.plat_pay);
        initHeader("在线支付",true);
        initUI();
        initData();
    }

    private void initUI(){
        balancePrice = (TextView)findViewById(R.id.balance_price);
        alipayView = (ImageView)findViewById(R.id.radio_alipay);
        wechatView = (ImageView)findViewById(R.id.radio_wechat);
        userName = (EditText)findViewById(R.id.user_name);
        userPhone = (EditText)findViewById(R.id.user_phone);
        userAccount = (EditText)findViewById(R.id.user_account);
    }

    private void initData(){
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_ACCOUNT);
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,UserBalanceActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(UserBalanceActivity.this);
                            balancePrice.setText("￥" + dataJson.optString("funds"));
                        }
                    });
                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(UserBalanceActivity.this);
                            showError();
                        }
                    });
                }
            }
        });
    }

    public void alipayOnClick(View view){
        payType = 1;
        alipayView.setImageResource(R.drawable.icon_pay_radio_checked);
        wechatView.setImageResource(R.drawable.icon_pay_radio);
    }

    public void wechatOnClick(View view){
        payType = 2;
        alipayView.setImageResource(R.drawable.icon_pay_radio);
        wechatView.setImageResource(R.drawable.icon_pay_radio_checked);
    }

    public void refundOnClick(final View view){
        Map<String,String> params = new HashMap<String,String>();

        if(payType <= 0){
            ToastUtil.error(this,"请选择退款方式！");
            return;
        }
        if(payType == 1){
            params.put("applyType","alipay");
        }else{
            params.put("applyType","wechat");
        }

        String account = userAccount.getText().toString();
        if(ValidateUtil.isBlank(account)){
            ToastUtil.error(this,"用户帐号不能为空！");
            return;
        }
        params.put("fundsAccount",account);

        String name = userName.getText().toString();
        if(ValidateUtil.isBlank(name)){
            ToastUtil.error(this,"用户姓名不能为空！");
            return;
        }
        params.put("fundsName",name);

        String phone = userPhone.getText().toString();
        if(ValidateUtil.isBlank(phone)){
            ToastUtil.error(this,"用户手机号不能为空！");
            return;
        }
        params.put("userPhone",phone);

        ToastUtil.startLoading(this);
        XHttpUtil.getInstance().upLoadFile(WangTuUtil.getPage(Constants.API_ACCOUNT_APPLY), params, null, new XHttpUtil.XCallBack() {
            @Override
            public void onSuccess(Object result) {
                stopLoading();
                try {
                    JSONObject dataJson = JsonUtil.parseJson((String) result);
                    if("success".equals(dataJson.optString("msg"))){
                        ToastUtil.alert(UserBalanceActivity.this, "提现申请成功，三个工作日内打到您指定的账户中，请耐心等待", new ToastUtil.DialogOnClickListener() {
                            @Override
                            public void onClick(BoxView dialog) {
                                finish();
                            }
                        });
                    }else{
                        ToastUtil.error(UserBalanceActivity.this, dataJson.optString("msg"));
                    }
                } catch (JSONException e) {
                    ToastUtil.error(UserBalanceActivity.this, result + "无法正常解析");
                }
            }

            @Override
            public void onError() {
                stopLoading();
                ToastUtil.error(UserBalanceActivity.this, "网络错误");
            }
        });
    }

}

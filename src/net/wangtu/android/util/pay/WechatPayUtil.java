package net.wangtu.android.util.pay;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import net.wangtu.android.Constants;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.http.HttpUtil;
import net.wangtu.android.util.WangTuHttpUtil;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by zhangxz on 2017/7/18.
 */

public class WechatPayUtil {
    private static WeakReference<WechatPayCallBack> callBackWeakReference;

    /**
     * 支付
     * @param activity
     * @param url
     * @param callBack
     */
    public static void pay(final Activity activity, final String url,WechatPayCallBack callBack){
        callBackWeakReference = new WeakReference<WechatPayCallBack>(callBack);
        final IWXAPI api = WXAPIFactory.createWXAPI(activity, Constants.APP_ID);
        api.registerApp(Constants.APP_ID);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject json = WangTuHttpUtil.getJson(url,activity);
                    //JSONObject json = createTestParams();
                    if (json != null) {
                        if(null != json && !json.has("retcode") ){
                            PayReq req = new PayReq();
                            //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                            req.appId			= json.getString("appid");
                            req.partnerId		= json.getString("partnerid");
                            req.prepayId		= json.getString("prepayid");
                            req.nonceStr		= json.getString("noncestr");
                            req.timeStamp		= json.getString("timestamp");
                            req.packageValue	= json.getString("package");
                            req.sign			= json.getString("sign");
                            req.extData			= "app data"; // optional
                            //Toast.makeText(activity, "正常调起支付", Toast.LENGTH_SHORT).show();
                            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                            api.sendReq(req);
                        }else{
                            Log.d("PAY_GET", "返回错误"+json.getString("retmsg"));
                            Toast.makeText(activity, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Log.d("PAY_GET", "服务器请求错误");
                        Toast.makeText(activity, "服务器请求错误", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Log.e("PAY_GET", "异常："+e.getMessage());
                    Toast.makeText(activity, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 支付结构返回
     * @param resp
     */
    public static void payCallBack(BaseResp resp){
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            WechatPayCallBack callBack = callBackWeakReference.get();
            if(callBack != null){
                callBack.callback(resp);
            }
        }
    }

    private static JSONObject createTestParams() throws Exception{
        JSONObject p = new JSONObject();
        p.put("appid","wxd930ea5d5a258f4f");
        p.put("partnerid","1900000109");
        p.put("prepayid","1101000000140415649af9fc314aa427");
        p.put("noncestr","1101000000140429eb40476f8896f4c9");
        p.put("timestamp","1398746574");
        p.put("package","Sign=WXPay");
        p.put("sign","7FFECB600D7157C5AA49810D2D8F28BC2811827B");
        return p;
    }

    public interface WechatPayCallBack{
        //resp.errCode == 0表示成功
        public void callback(BaseResp resp);
    }
}

package net.wangtu.android.util.pay;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import net.wangtu.android.Constants;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.http.HttpUtil;
import net.wangtu.android.util.WangTuHttpUtil;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by zhangxz on 2017/7/18.
 */

public class AlipayUtil {
    /**
     * 支付
     * @param activity
     * @param url
     * @param callBack
     */
    public static void pay(final Activity activity, final String url,final AlipayCallBack callBack){
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    String payInfo = WangTuHttpUtil.getString(url,activity);
                    PayTask alipay = new PayTask(activity);
                    final String result = alipay.pay(payInfo,true);

                    //返回执行结果
                    callBack.callback(result);
                } catch (Exception e) {
                    //执行失败
                    callBack.callback("fail");
                }
            }
        });
    }

    public interface AlipayCallBack{
        //resp.errCode == 0表示成功
        public void callback(String result);
    }
}

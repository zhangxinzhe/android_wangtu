package net.wangtu.android.util;

import android.app.Activity;

import net.wangtu.android.Constants;
import net.wangtu.android.activity.UserBalanceActivity;
import net.wangtu.android.common.util.DataUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.ValidateUtil;

import org.json.JSONObject;

/**
 * Created by zhangxz on 2017/7/19.
 */

public class BalanceUtil {
    private static double balance = -1.0;

    public static void initBalance(final Activity activity,final Runnable runnable){
        if(balance < 0){
            ThreadUtils.schedule(new Runnable() {
                @Override
                public void run() {
                    String url = WangTuUtil.getPage(Constants.API_ACCOUNT);
                    try {
                        final JSONObject dataJson = WangTuHttpUtil.getJson(url,activity);
                        runnable.run();
                        balance = dataJson.optDouble("funds");
                    } catch (Exception e) {
                        LogUtil.error(e);
                    }
                }
            });
        }else{
            runnable.run();
        }
    }

    public static void setBalance(double balance){
        BalanceUtil.balance = balance;
    }

    public static double getBalance(){
        return balance;
    }
}

package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;

import org.json.JSONObject;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class SettingActivity extends BaseActivity {

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.setting);

        initHeader("设置",true);
    }

    public void logoutOnClick(View view){
        startLoading();
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_LOGOUT);
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,SettingActivity.this);
                    if(dataJson != null){
                        post(new Runnable() {
                            @Override
                            public void run() {
                                stopLoading();
                                if("success".equals(dataJson.optString("msg"))){
                                    Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    finish();;
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
                            Toast.makeText(getApplicationContext(),"退出失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}

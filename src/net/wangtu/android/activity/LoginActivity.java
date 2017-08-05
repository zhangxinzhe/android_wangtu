package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.StringUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.dialog.AlertView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;

import org.json.JSONObject;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class LoginActivity extends BaseActivity {
    private EditText txtUserName;
    private EditText txtPassword;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        statusColor = R.color.white;
        setContentView(R.layout.login);

        txtUserName = (EditText)findViewById(R.id.txt_userName);
        txtPassword = (EditText)findViewById(R.id.txt_password);
    }

    public void loginOnClick(View view){
        final String userName = txtUserName.getText().toString();
        final String password = txtPassword.getText().toString();

        if(ValidateUtil.isBlank(userName) || ValidateUtil.isBlank(password)){
            Toast.makeText(getApplicationContext(), "用户名密码不能为空！",  Toast.LENGTH_SHORT).show();
            return;
        }

        startLoading();
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_LOGIN);
                url += "?userName=" + userName;
                url += "&password=" + password;
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,LoginActivity.this);
                    if(dataJson != null){
                        txtPassword.post(new Runnable() {
                            @Override
                            public void run() {
                                stopLoading();
                                if("success".equals(dataJson.optString("msg"))){
                                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                    finish();;
                                }else{
                                    ToastUtil.error(getApplicationContext(),dataJson.optString("msg"));
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    txtPassword.post(new Runnable() {
                        @Override
                        public void run() {
                            stopLoading();
                            ToastUtil.error(getApplicationContext(),"登录失败");
                        }
                    });
                }
            }
        });
    }

    public void registerOnClick(View view){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    public void forgetPwdOnClick(View view){
        final AlertView alertView = new AlertView(this);
        alertView.setTitleMsg("提示");
        alertView.setContentMsg("请联系管理员电话：124234535345！");
        alertView.setOkBtnName("确定");
        alertView.setOkBtnListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                alertView.dismiss();
            }
        });
        alertView.show();
    }
}

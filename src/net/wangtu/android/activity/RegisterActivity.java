package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.HomeActivity;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;

import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class RegisterActivity extends BaseActivity {
    private EditText txtUserName;
    private EditText txtPassword;
    private EditText txtRealName;
    private EditText txtPasswordAgain;
    private EditText txtPhone;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.register);

        initHeader("注册",false);

        txtUserName = (EditText)findViewById(R.id.txt_userName);
        txtRealName = (EditText)findViewById(R.id.txt_realName);
        txtPassword = (EditText)findViewById(R.id.txt_password);
        txtPasswordAgain = (EditText)findViewById(R.id.txt_password_again);
        txtPhone = (EditText)findViewById(R.id.txt_phone);
    }

    public void register(View view){
        final String userName = txtUserName.getText().toString();
        final String password = txtPassword.getText().toString();
        final String realName = txtRealName.getText().toString();
        final String passwordAgain = txtPasswordAgain.getText().toString();
        final String phone = txtPhone.getText().toString();

        if(ValidateUtil.isBlank(userName)){
            Toast.makeText(this,"用户名不能为空！",Toast.LENGTH_LONG).show();
            return;
        }

        if(ValidateUtil.isBlank(realName)){
            Toast.makeText(this,"用户姓名不能为空！",Toast.LENGTH_LONG).show();
            return;
        }

        if(ValidateUtil.isBlank(password)){
            Toast.makeText(this,"密码不能为空！",Toast.LENGTH_LONG).show();
            return;
        }

        if(ValidateUtil.isBlank(passwordAgain)){
            Toast.makeText(this,"确认密码不能为空！",Toast.LENGTH_LONG).show();
            return;
        }

        if(!passwordAgain.equals(password)){
            Toast.makeText(this,"两次输入密码不一致！",Toast.LENGTH_LONG).show();
            return;
        }

        if(ValidateUtil.isBlank(phone)){
            Toast.makeText(this,"手机号码不能为空！",Toast.LENGTH_LONG).show();
            return;
        }

        startLoading();
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = WangTuUtil.getPage(Constants.API_REGISTER);
                    url += "?userName=" + URLEncoder.encode(userName,"utf-8");
                    url += "&password=" + URLEncoder.encode(password,"utf-8");
                    url += "&realName=" + URLEncoder.encode(realName,"utf-8");
                    url += "&rePassword=" + URLEncoder.encode(passwordAgain,"utf-8");
                    url += "&telephone=" + URLEncoder.encode(phone,"utf-8");
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,RegisterActivity.this);
                    if(dataJson != null){
                        txtPassword.post(new Runnable() {
                            @Override
                            public void run() {
                                stopLoading();
                                if("success".equals(dataJson.optString("msg"))){
                                    Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                    finish();;
                                }else{
                                    Toast.makeText(getApplicationContext(),dataJson.optString("msg"),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    txtPassword.post(new Runnable() {
                        @Override
                        public void run() {
                            stopLoading();
                            Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    public void cancel(View view){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}

package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.view.dialog.AlertView;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class LoginActivity extends BaseActivity {

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        statusColor = R.color.white;
        setContentView(R.layout.login);

        Toast.makeText(getApplicationContext(), "服务器请求失败！",
                Toast.LENGTH_SHORT).show();
    }

    public void loginOnClick(View view){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();;
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

package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.HomeActivity;
import net.wangtu.android.activity.base.BaseActivity;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class RegisterActivity extends BaseActivity {

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.register);

        initHeader("注册",false);
    }

    public void register(View view){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    public void cancel(View view){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}

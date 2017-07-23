package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class SettingActivity extends BaseActivity {

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.setting);

        initHeader("我",true);
    }

}

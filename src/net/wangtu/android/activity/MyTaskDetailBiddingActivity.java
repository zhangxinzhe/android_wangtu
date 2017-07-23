package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.view.dialog.ConfirmView;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class MyTaskDetailBiddingActivity extends BaseActivity{


    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_task_detail_bidding);
        initHeader("详情",true);


    }

    public void cancelTaskOnclick(View view){
        finish();
    }

    public void finishTaskOnclick(View view){
        finish();
    }
}

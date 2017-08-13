package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.view.dialog.ConfirmView;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class MyTaskDetailPayActivity extends BaseActivity{

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_task_detail_pay);
        initHeader("详情",true);
    }

    public void makeSureOnclick(View view){
        final ConfirmView confirmView = new ConfirmView(this);
        confirmView.setTitleMsg("确认接单提醒！");
        confirmView.setContentMsg("您将前往支付平台使用费？");
        confirmView.setOkBtnName("继续");
        confirmView.setOkBtnListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmView.dismiss();
                Intent intent = new Intent(MyTaskDetailPayActivity.this,PlatPayActivity.class);
                startActivity(intent);
            }
        });
        confirmView.show();
    }

    public void finishTaskOnclick(View view){

    }
}

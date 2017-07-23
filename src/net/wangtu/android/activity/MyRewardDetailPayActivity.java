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

public class MyRewardDetailPayActivity extends BaseActivity{

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_reward_detail_pay);
        initHeader("详情",true);

        getDataFromServer();
    }

    public void makeSureOnClick(View view){
        final ConfirmView confirmView = new ConfirmView(this);
        confirmView.setTitleMsg("操作提醒！");
        confirmView.setContentMsg("您将前往支付页面，进行悬赏保证金支付？");
        confirmView.setOkBtnName("确定");
        confirmView.setOkBtnListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmView.dismiss();
                Intent intent = new Intent(MyRewardDetailPayActivity.this,RewardPayActivity.class);
                startActivity(intent);
            }
        });
        confirmView.show();
    }

    public void waitOnClick(View view){
        finish();
    }

    private void getDataFromServer(){
    }
}

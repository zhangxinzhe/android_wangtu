package net.wangtu.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.view.MyRewardView;
import net.wangtu.android.view.MyTaskView;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class MyTaskActivity extends BaseActivity implements View.OnClickListener{
    private TextView myReward;
    private TextView myTask;
    private TextView targetSel;
    private MyTaskView myTaskView;
    private MyRewardView myRewardView;
    private RelativeLayout container;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_task);
        myReward = (TextView)findViewById(R.id.myReward);
        myReward.setOnClickListener(this);
        myTask = (TextView)findViewById(R.id.myTask);
        myTask.setOnClickListener(this);

        container = (RelativeLayout)findViewById(R.id.container);
        initHeader("我参与的悬赏",true);

        String value = getIntent().getStringExtra("type");
        if("myReward".equals(value)){
            //targetSel = myReward;
            onClick(myReward);
        }else{
            //targetSel = myTask;
            onClick(myTask);
        }

        //showContent();
    }

    public void showContent(){
        if(targetSel == myTask){
            if(myTaskView == null){
                myTaskView = (MyTaskView) ((ViewStub) findViewById(R.id.myTaskStub)).inflate();
            }else{
                myTaskView.setVisibility(View.VISIBLE);
                myTaskView.getDataFromServer(0,true);
            }
            if(myRewardView != null){
                myRewardView.setVisibility(View.GONE);
            }
        }else{
            if(myRewardView == null){
                myRewardView = (MyRewardView) ((ViewStub) findViewById(R.id.myRewardStub)).inflate();
            }else{
                myRewardView.setVisibility(View.VISIBLE);
                myRewardView.getDataFromServer(0,true);
            }
            if(myTaskView != null){
                myTaskView.setVisibility(View.GONE);
            }
        }
    }

    public void onClick(View v){
        if(v == targetSel){
            return;
        }

        if(targetSel != null){
            targetSel.setTextAppearance(this,R.style.task_tabbar_unsel);
            targetSel.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_task_tab_unsel));
        }
        targetSel = (TextView)v;
        targetSel.setTextAppearance(this,R.style.task_tabbar_sel);
        targetSel.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_task_tab_sel));
        showContent();
    }
}

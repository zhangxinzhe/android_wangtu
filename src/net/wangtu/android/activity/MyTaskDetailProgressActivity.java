package net.wangtu.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;

/**
 * 任务完成进度
 */

public class MyTaskDetailProgressActivity extends BaseActivity{
    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_task_detail_progress);
        initHeader("详情",true);


    }

    public void taskFinishedOnclick(View view){
        finish();
    }

    public void cancelOnclick(View view){
        finish();
    }
}

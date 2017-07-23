package net.wangtu.android.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;

/**
 * 悬赏任务结果
 */

public class MyRewardDetailResultActivity extends BaseActivity{
    private TextView titleText;
    private ImageView titleImage;
    private TextView contentText;
    private ImageView contentImage;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_reward_detail_result);

        titleText = (TextView)findViewById(R.id.titleText);
        titleImage = (ImageView)findViewById(R.id.titleImage);
        contentText = (TextView)findViewById(R.id.contentText);
        contentImage = (ImageView)findViewById(R.id.contentImage);

        initHeader("详情",true);
    }
}

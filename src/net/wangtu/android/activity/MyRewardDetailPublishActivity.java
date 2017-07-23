package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.view.RewardEditView;

/**
 * 赏金发布或保存
 */

public class MyRewardDetailPublishActivity extends BaseActivity{
    private TextView titleText;
    private ImageView titleImage;
    private TextView contentText;
    private ImageView contentImage;
    private RewardEditView rewardEditView;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_reward_detail_publish);

        rewardEditView = (RewardEditView) findViewById(R.id.reward_edit);
        titleText = (TextView)findViewById(R.id.titleText);
        titleImage = (ImageView)findViewById(R.id.titleImage);
        contentText = (TextView)findViewById(R.id.contentText);
        contentImage = (ImageView)findViewById(R.id.contentImage);

        initHeader("发布",true);
    }

    public void publishRewardOnClick(View view){
        finish();
    }

    public void saveRewardOnClick(View view){
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        rewardEditView.onActivityResult(requestCode,resultCode,data);
    }
}

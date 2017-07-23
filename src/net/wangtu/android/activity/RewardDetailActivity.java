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

public class RewardDetailActivity extends BaseActivity{
    private TextView titleText;
    private ImageView titleImage;
    private TextView contentText;
    private ImageView contentImage;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.reward_detail);

        titleText = (TextView)findViewById(R.id.titleText);
        titleImage = (ImageView)findViewById(R.id.titleImage);
        contentText = (TextView)findViewById(R.id.contentText);
        contentImage = (ImageView)findViewById(R.id.contentImage);

        initHeader("详情",true);
    }

    public void titleDetailOnClick(View view){
        String title = "设计一个银行年会主设计一个银行年会主";
        if(titleImage.getVisibility() == View.VISIBLE){
            titleText.setText(title);
            titleImage.setVisibility(View.INVISIBLE);
        }else{
            if(title.length() > 10){
                title = title.substring(0,10) + "...";
            }
            titleText.setText(title);
            titleImage.setVisibility(View.VISIBLE);
        }

    }

    public void contentDetailOnClick(View view){
        String title = "设计一个银行年会主设计一个银行年会主设计一个银行年会主设计一个银行年会主设计一个银行年会主设计一个银行年会主设计一个银行年会主设计一个银行年会主";
        if(contentImage.getVisibility() == View.VISIBLE){
            contentText.setText(title);
            contentImage.setVisibility(View.INVISIBLE);
        }else{
            if(title.length() > 10){
                title = title.substring(0,10) + "...";
            }
            contentText.setText(title);
            contentImage.setVisibility(View.VISIBLE);
        }

    }

    public void bidding(View view){
        final ConfirmView confirmView = new ConfirmView(this);
        confirmView.setTitleMsg("竞价申请提交成功！");
        confirmView.setContentMsg("您可以到我的任务中查看竞价结果，继续寻找任务还是查看我的任务？");
        confirmView.setOkBtnName("继续寻找");
        confirmView.setOkBtnListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmView.dismiss();
                finish();
            }
        });
        confirmView.setCancelBtnName("查看我的");
        confirmView.setCancelBtnListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmView.dismiss();
                Intent intent = new Intent(RewardDetailActivity.this,MyTaskActivity.class);
                intent.putExtra("type","myTask");
                startActivity(intent);
                finish();
            }
        });
        confirmView.show();
    }

    public void cancel(View view){
        finish();
    }
}

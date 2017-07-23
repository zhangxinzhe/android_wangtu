package net.wangtu.android.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.HomeActivity;
import net.wangtu.android.activity.MyTaskActivity;
import net.wangtu.android.activity.common.AlbumActivity;
import net.wangtu.android.common.util.FileUtil;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.view.HorizontalListView;
import net.wangtu.android.common.view.dialog.ActionSheet;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.util.HeaderUtil;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.album.XImageUtil;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * 悬赏发布
 */

public class RewardCreateView extends RelativeLayout implements View.OnClickListener{;
    private boolean inited;

    private Button publishReward;
    private Button cancelReward;
    private RewardEditView rewardEditView;

    public RewardCreateView(Context context) {
        this(context, null);
    }

    public RewardCreateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RewardCreateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(inited){
            return;
        }
        inited = true;

        //状态栏透明
        HeaderUtil.initRewardPublishStatusBar((Activity) getContext(),this);

        rewardEditView = (RewardEditView) findViewById(R.id.reward_edit);
        publishReward = (Button) findViewById(R.id.publishReward);
        publishReward.setOnClickListener(this);
        cancelReward = (Button) findViewById(R.id.cancelReward);
        cancelReward.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if(v == publishReward){
            final ConfirmView confirmView = new ConfirmView(getContext());
            confirmView.setTitleMsg("创建成功！");
            confirmView.setContentMsg("创建成功，可以到我的悬赏里进行发布？");
            confirmView.setOkBtnName("去发布");
            confirmView.setOkBtnListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    confirmView.dismiss();
                    Intent intent = new Intent(getContext(),MyTaskActivity.class);
                    intent.putExtra("type","myReward");
                    getContext().startActivity(intent);
                    setVisibility(GONE);
                }
            });
            confirmView.setCancelBtnName("取消");
            confirmView.setCancelBtnListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    confirmView.dismiss();
                    Intent intent = new Intent(getContext(),HomeActivity.class);
                    getContext().startActivity(intent);
                    setVisibility(GONE);
                }
            });
            confirmView.show();
        }else{
            setVisibility(GONE);
        }
    }

    public RewardEditView getRewardEditView() {
        return rewardEditView;
    }
}

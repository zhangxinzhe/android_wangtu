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
import android.widget.Toast;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.HomeActivity;
import net.wangtu.android.activity.LoginActivity;
import net.wangtu.android.activity.MyTaskActivity;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.activity.base.BaseFragmentActivity;
import net.wangtu.android.activity.common.AlbumActivity;
import net.wangtu.android.common.util.FileUtil;
import net.wangtu.android.common.util.JsonUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.HorizontalListView;
import net.wangtu.android.common.view.dialog.ActionSheet;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.util.HeaderUtil;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.album.XImageUtil;
import net.wangtu.android.util.xhttp.XHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        try {
            rewardEditView.clearReward();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v){
        if(v == publishReward){
            publicReward();
        }else{
            setVisibility(GONE);
        }
    }

    public void publicReward(){
        try {
            Map<String,String> params = rewardEditView.getReward();
            if(ValidateUtil.isBlank(params.get("reward.title"))){
                ToastUtil.error(getContext(),"主题不能为空！");
                return;
            }
            if(ValidateUtil.isBlank(params.get("reward.catalogId"))){
                ToastUtil.error(getContext(),"分类不能为空！");
                return;
            }
            if(ValidateUtil.isBlank(params.get("reward.price"))){
                ToastUtil.error(getContext(),"悬赏金额不能为空！");
                return;
            }
            if(ValidateUtil.isBlank(params.get("reward.deadline"))){
                ToastUtil.error(getContext(),"期限不能为空！");
                return;
            }
            if(ValidateUtil.isBlank(params.get("reward.location"))){
                ToastUtil.error(getContext(),"工作地点不能为空！");
                return;
            }
            if(ValidateUtil.isBlank(params.get("reward.description"))){
                ToastUtil.error(getContext(),"悬赏描述不能为空！");
                return;
            }

            List<File> fileList = new ArrayList<File>();
            for (int i = 0; i < AlbumOpt.selectImages.size(); i++) {
                ImageItem imageItem = AlbumOpt.selectImages.get(i);
                fileList.add(new File(imageItem.getImagePath()));
            }
            Map<String, List<File>> files = new HashMap<String, List<File>>();
            files.put("rewardFiles", fileList);

            String url = WangTuUtil.getPage(Constants.API_ADD_REWARD);

            final BaseFragmentActivity activity = (BaseFragmentActivity)getContext();
            activity.startLoading();
            XHttpUtil.getInstance().upLoadFile(url, params, files, new XHttpUtil.XCallBack() {
                @Override
                public void onSuccess(Object result) {
                    activity.stopLoading();
                    try {
                        JSONObject dataJson = JsonUtil.parseJson((String) result);
                        if("success".equals(dataJson.optString("msg"))){
                            publicSuccess();
                        }else{
                            ToastUtil.error(activity, dataJson.optString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError() {
                    activity.stopLoading();
                    ToastUtil.error(activity, "网络错误");
                }
            });
        } catch (JSONException e) {
            ToastUtil.error(getContext(),"数据处理失败！");
        }
    }

    public void publicSuccess(){
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
    }

    public RewardEditView getRewardEditView() {
        return rewardEditView;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(rewardEditView != null){
            try {
                rewardEditView.clearReward();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

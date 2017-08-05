package net.wangtu.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.activity.base.BaseFragmentActivity;
import net.wangtu.android.common.util.JsonUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.dialog.BoxView;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.xhttp.XHttpUtil;
import net.wangtu.android.view.MyTaskView;
import net.wangtu.android.view.RewardEditView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 赏金发布或保存
 */

public class MyRewardDetailPublishActivity extends BaseActivity{
    private RewardEditView rewardEditView;
    private String rewardId;
    private JSONObject dataJson;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_reward_detail_publish);
        initHeader("发布",true);

        Bundle bundle = getIntent().getExtras();
        rewardId = bundle.getString("rewardId");
        rewardEditView = (RewardEditView) findViewById(R.id.reward_edit);
        getDataFromServer();
    }

    private void getDataFromServer(){
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_REWARD_DETAIL) + "?rewardId=" + rewardId;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,MyRewardDetailPublishActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyRewardDetailPublishActivity.this);
                            rewardEditView.initData(dataJson.optJSONObject("reward"));
                        }
                    });

                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyRewardDetailPublishActivity.this);
                            showError();
                        }
                    });
                }
            }
        });
    }


    public void publishRewardOnClick(View view){
        ToastUtil.confirm(this, "确定发布悬赏？",
                new ToastUtil.DialogOnClickListener() {
                    @Override
                    public void onClick(BoxView dialog) {
                        toSaveOrPublish(true);
                        dialog.dismiss();
                    }
                }, new ToastUtil.DialogOnClickListener() {
                    @Override
                    public void onClick(BoxView dialog) {
                        dialog.dismiss();
                    }
                });
    }

    public void saveRewardOnClick(View view){
        toSaveOrPublish(false);
    }

    /**
     * 保存或者发布
     * @param isPublish
     */
    public void toSaveOrPublish(final boolean isPublish){
        try {
            Map<String,String> params = rewardEditView.getReward();
            if(ValidateUtil.isBlank(params.get("reward.title"))){
                ToastUtil.error(this,"主题不能为空！");
                return;
            }
            if(ValidateUtil.isBlank(params.get("reward.catalogId"))){
                ToastUtil.error(this,"分类不能为空！");
                return;
            }
            if(ValidateUtil.isBlank(params.get("reward.price"))){
                ToastUtil.error(this,"悬赏金额不能为空！");
                return;
            }
            if(ValidateUtil.isBlank(params.get("reward.deadline"))){
                ToastUtil.error(this,"期限不能为空！");
                return;
            }
            if(ValidateUtil.isBlank(params.get("reward.location"))){
                ToastUtil.error(this,"工作地点不能为空！");
                return;
            }
            if(ValidateUtil.isBlank(params.get("reward.description"))){
                ToastUtil.error(this,"悬赏描述不能为空！");
                return;
            }
            if(isPublish){
                params.put("reward.status",Constants.REWARD_STATUS_PUBLISH);
            }
            String savedFileList = null;
            List<File> fileList = new ArrayList<File>();
            for (int i = 0; i < AlbumOpt.selectImages.size(); i++) {
                ImageItem imageItem = AlbumOpt.selectImages.get(i);
                if(!imageItem.getIsLocal()){
                    if(savedFileList == null){
                        savedFileList = imageItem.getImageId();
                    }else{
                        savedFileList += "," + imageItem.getImageId();
                    }
                }else{
                    fileList.add(new File(imageItem.getImagePath()));
                }
            }
            Map<String, List<File>> files = new HashMap<String, List<File>>();
            files.put("rewardFiles", fileList);
            params.put("rewardPictureIds",savedFileList);
            String url = WangTuUtil.getPage(Constants.API_UPDATE_REWARD);
            ToastUtil.startLoading(this);
            XHttpUtil.getInstance().upLoadFile(url, params, files, new XHttpUtil.XCallBack() {
                @Override
                public void onSuccess(Object result) {
                    ToastUtil.stopLoading(MyRewardDetailPublishActivity.this);
                    try {
                        JSONObject dataJson = JsonUtil.parseJson((String) result);
                        if("success".equals(dataJson.optString("msg"))){
                            String msg = isPublish ? "发布成功！" : "保存成功";
                            ToastUtil.alert(MyRewardDetailPublishActivity.this, msg, new ToastUtil.DialogOnClickListener() {
                                @Override
                                public void onClick(BoxView dialog) {
                                    finish();
                                }
                            });
                        }else{
                            ToastUtil.error(MyRewardDetailPublishActivity.this, dataJson.optString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError() {
                    ToastUtil.stopLoading(MyRewardDetailPublishActivity.this);
                    ToastUtil.error(MyRewardDetailPublishActivity.this, "网络错误");
                }
            });
        } catch (JSONException e) {
            ToastUtil.error(this,"数据处理失败！");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        rewardEditView.onActivityResult(requestCode,resultCode,data);
    }
}

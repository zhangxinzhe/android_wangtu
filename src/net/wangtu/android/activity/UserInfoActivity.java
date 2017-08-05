package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.activity.common.AvatarActivity;
import net.wangtu.android.common.util.ImageCacheUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.dialog.AlertView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.view.UserInfoCommentView;
import net.wangtu.android.view.UserInfoStarView;

import org.json.JSONObject;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class UserInfoActivity extends BaseActivity {
    private ImageView userPhoto;
    private TextView userName;
    private TextView userPhone;
    private TextView userSex;
    private TextView userAge;
    private TextView commentComprehensiveScore;
    private TextView commentServiceAttitudeValue;
    private TextView commentServiceQuilityValue;
    private UserInfoStarView commentServiceAttitudeStar;
    private UserInfoStarView commentServiceQuilityStar;
    private UserInfoCommentView userInfoCommentView;
    private String userId;
    private JSONObject dataJson;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        statusColor = R.color.brown;
        setContentView(R.layout.user_info);
        initHeader(null,true);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            userId = bundle.getString("userId");
        }

        initUI();
        initData();
    }

    private void initUI(){
        userPhoto = (ImageView)findViewById(R.id.user_photo);
        userName = (TextView)findViewById(R.id.user_name);
        userPhone = (TextView)findViewById(R.id.user_phone);
        userSex = (TextView)findViewById(R.id.user_sex);
        userAge = (TextView)findViewById(R.id.user_age);
        commentComprehensiveScore = (TextView)findViewById(R.id.comment_comprehensive_score);
        commentServiceAttitudeValue = (TextView)findViewById(R.id.comment_service_attitude_value);
        commentServiceQuilityValue = (TextView)findViewById(R.id.comment_service_quility_value);
        commentServiceAttitudeStar = (UserInfoStarView)findViewById(R.id.comment_service_attitude_star);
        commentServiceQuilityStar = (UserInfoStarView)findViewById(R.id.comment_service_quility_star);
        userInfoCommentView = (UserInfoCommentView)findViewById(R.id.user_info_comment);
    }

    private void initData(){
        getDataFromServer(userId);
        userInfoCommentView.getDataFromServer(userId,"all","true");
    }

    public void getDataFromServer(final String userId){
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_GET_USER_INFO) + "?userId=" + userId;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,UserInfoActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(UserInfoActivity.this);
                            JSONObject userInfo = dataJson.optJSONObject("userInfo");
                            ImageCacheUtil.lazyLoad(userPhoto,WangTuUtil.getPage(userInfo.optString("avatarFile")),R.drawable.icon_header,false);
                            userName.setText(userInfo.optString("userName"));
                            userPhone.setText(userInfo.optString("phone"));
                            String sex = userInfo.optString("sex");
                            if("MAN".equals(sex)){
                                sex = "男";
                            }else if("WOMAN".equals(sex)){
                                sex = "女";
                            }else{
                                sex = "未知";
                            }
                            userSex.setText(sex);
                            userAge.setText(userInfo.optString("age") + "岁");
                            commentComprehensiveScore.setText(userInfo.optString("comprehensiveScore"));
                            commentServiceAttitudeValue.setText(userInfo.optString("serviceAttitude"));
                            commentServiceQuilityValue.setText(userInfo.optString("serviceQuility"));
                            commentServiceAttitudeStar.initData(userInfo.optDouble("serviceAttitude"));
                            commentServiceQuilityStar.initData(userInfo.optDouble("serviceQuility"));
                        }
                    });

                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(UserInfoActivity.this);
                            showError();
                        }
                    });
                }
            }
        });
    }

    public void modifyAvatarOnClick(View view){
        Intent intent = new Intent(this,AvatarActivity.class);
        startActivity(intent);
    }
}

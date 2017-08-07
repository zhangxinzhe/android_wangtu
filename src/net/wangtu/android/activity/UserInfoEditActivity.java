package net.wangtu.android.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.activity.base.BaseFragmentActivity;
import net.wangtu.android.activity.common.AvatarActivity;
import net.wangtu.android.common.util.JsonUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.util.LoginUtil;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.xhttp.XHttpUtil;
import net.wangtu.android.view.SelectPopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class UserInfoEditActivity extends BaseActivity {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    private EditText userRealName;
    private TextView userSex;
    private TextView userBirthday;
    private TextView userIndustry;
    private EditText userPhone;
    private View contentView;

    private JSONObject dataJson;
    private String userId;
    private String avatarFile;
    private SelectPopupWindow popupWindow;
    private String sex;
    private String birthdayLong;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.user_info_edit);
        initHeader("个人信息修改",true);
        userId = LoginUtil.getUserId();
        initUI();
        initData();
    }

    private void initUI(){
        contentView =  findViewById(R.id.content_view);
        userRealName = (EditText)findViewById(R.id.user_real_name);
        userSex = (TextView)findViewById(R.id.user_sex);
        userBirthday = (TextView)findViewById(R.id.user_birthday);
        userIndustry = (TextView)findViewById(R.id.user_industry);
        userPhone = (EditText)findViewById(R.id.user_phone);
    }

    private void initData(){
        getDataFromServer(userId);
    }

    public void getDataFromServer(final String userId){
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_GET_USER_INFO) + "?userId=" + userId;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,UserInfoEditActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(UserInfoEditActivity.this);
                            JSONObject userInfo = dataJson.optJSONObject("userInfo");
                            avatarFile = dataJson.optString("avatarFile");
                            userRealName.setText(userInfo.optString("realName"));
                            userPhone.setText(userInfo.optString("phone"));
                            sex = userInfo.optString("sex");
                            String sexName = "";
                            if("MAN".equals(sex)){
                                sexName = "男";
                            }else if("WOMAN".equals(sex)){
                                sexName = "女";
                            }
                            userSex.setText(sexName);
                            birthdayLong = userInfo.optString("birthday");
                            userBirthday.setText(dateFormat.format(new Date(userInfo.optLong("birthday"))));
                            userIndustry.setText(userInfo.optString("industry"));
                            userPhone.setText(userInfo.optString("phone"));
                        }
                    });
                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(UserInfoEditActivity.this);
                            showError();
                        }
                    });
                }
            }
        });
    }

    public void modifyAvatarOnClick(View view){
        Intent intent = new Intent(this,AvatarActivity.class);
        intent.putExtra("avatarFile",WangTuUtil.getPage(avatarFile));
        startActivity(intent);
    }

    public void modifySexOnClick(View view){
        if(popupWindow == null){
            try {
                popupWindow = new SelectPopupWindow(contentView,view);
                JSONArray sexArray = new JSONArray();
                JSONObject man = new JSONObject();
                man.put("key","男");
                man.put("value","MAN");
                sexArray.put(man);
                JSONObject woman = new JSONObject();
                woman.put("key","女");
                woman.put("value","WOMAN");
                sexArray.put(woman);
                popupWindow.initData(sexArray);
                popupWindow.setOnItemCheckListener(new SelectPopupWindow.OnItemCheckListener() {
                    @Override
                    public void onItemChecked(JSONObject data) throws JSONException {
                        userSex.setText(data.optString("key"));
                        sex = data.optString("value");
                    }
                });
            }catch (JSONException e){
                ToastUtil.error(this,e.getMessage());
            }
        }
        if(ValidateUtil.isBlank(sex)){
            popupWindow.setValue(sex);
        }
        popupWindow.show();
    }

    public void modifyBirthdayOnClick(View view){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                userBirthday.setText(year + "年" + monthOfYear + "月" + dayOfMonth + "日");
                Calendar c = Calendar.getInstance();
                c.set(year,monthOfYear,dayOfMonth);
                birthdayLong = c.getTimeInMillis() + "";
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 更新用户信息
     * @param view
     */
    public void saveOnClick(View view){
        Map<String,String> params = new HashMap<String,String>();
        String value = userRealName.getText().toString();
        if(ValidateUtil.isBlank(value)){
            ToastUtil.error(this,"请填写姓名！");
            return;
        }
        params.put("updateUser.realName",value);

        if(ValidateUtil.isBlank(sex)){
            ToastUtil.error(this,"请选择性别！");
            return;
        }
        params.put("updateUser.sex",sex);

        value = userBirthday.getText().toString();
        if(ValidateUtil.isBlank(value)){
            ToastUtil.error(this,"请选择生日！");
            return;
        }
        params.put("updateUser.birthday",value.toString().replaceAll("(年|月)","-").replace("日",""));

        value = userIndustry.getText().toString();
        if(ValidateUtil.isBlank(value)){
            ToastUtil.error(this,"请填写行业！");
            return;
        }
        params.put("updateUser.industry",value);

        value = userPhone.getText().toString();
        if(ValidateUtil.isBlank(value)){
            ToastUtil.error(this,"请填写手机号！");
            return;
        }
        params.put("updateUser.phone",value);

        String url = WangTuUtil.getPage(Constants.API_UPDATE_USER_INFO);
        ToastUtil.startLoading(this);
        XHttpUtil.getInstance().upLoadFile(url, params, null, new XHttpUtil.XCallBack() {
            @Override
            public void onSuccess(Object result) {
                ToastUtil.stopLoading(UserInfoEditActivity.this);
                try {
                    JSONObject dataJson = JsonUtil.parseJson((String) result);
                    if("success".equals(dataJson.optString("msg"))){
                        finish();
                    }else{
                        ToastUtil.error(UserInfoEditActivity.this, dataJson.optString("msg"));
                    }
                } catch (JSONException e) {
                    ToastUtil.error(UserInfoEditActivity.this, e.getMessage());
                }
            }

            @Override
            public void onError() {
                ToastUtil.stopLoading(UserInfoEditActivity.this);
                ToastUtil.error(UserInfoEditActivity.this, "网络错误");
            }
        });
    }
}

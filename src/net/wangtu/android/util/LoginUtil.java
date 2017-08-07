package net.wangtu.android.util;

import android.content.Context;

import net.wangtu.android.common.util.DataUtil;
import net.wangtu.android.common.util.ValidateUtil;

import org.json.JSONObject;

/**
 * Created by zhangxz on 2017/7/19.
 */

public class LoginUtil {
    private static  String userId;
    private static  String realName;
    private static  String avatarFile;
    public static boolean isLogin(){
        return !ValidateUtil.isBlank(getUserId());
    }

    public static void login(JSONObject dataJson){
        userId = dataJson.optString("userId");
        realName = dataJson.optString("realName");
        avatarFile = WangTuUtil.getPage(dataJson.optString("avatarFile"));
        DataUtil.setData("userId",userId);
        DataUtil.setData("realName",realName);
        DataUtil.setData("avatarFile",avatarFile);
    }

    public static void logout(){
        userId = "";
        realName = "";
        avatarFile = "";
        DataUtil.setData("userId",userId);
        DataUtil.setData("realName",realName);
        DataUtil.setData("avatarFile",avatarFile);
    }

    public static String getUserId(){
        if(ValidateUtil.isBlank(userId)){
            userId = DataUtil.getData("userId");
        }
        return userId;
    }

    public static String getRealName(){
        if(ValidateUtil.isBlank(userId)){
            realName = DataUtil.getData("realName");
        }
        return realName;
    }

    public static String getAvatarFile(){
        if(ValidateUtil.isBlank(avatarFile)){
            avatarFile = DataUtil.getData("avatarFile");
        }
        return avatarFile;
    }
}

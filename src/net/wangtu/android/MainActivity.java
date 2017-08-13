package net.wangtu.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.wangtu.android.activity.HomeActivity;
import net.wangtu.android.activity.LoginActivity;
import net.wangtu.android.activity.TaskCommentActivity;
import net.wangtu.android.activity.UserInfoActivity;

/**
 * Created by zhangxz on 2017/7/2.
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if(1==1){
//            Intent intent = new Intent(this,TaskCommentActivity.class);
//            startActivity(intent);
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
//            Intent intent = new Intent(this,UserInfoActivity.class);
//            startActivity(intent);
        }else{
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}

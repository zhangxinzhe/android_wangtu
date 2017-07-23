package net.wangtu.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.wangtu.android.activity.HomeActivity;
import net.wangtu.android.activity.LoginActivity;

/**
 * Created by zhangxz on 2017/7/2.
 */

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

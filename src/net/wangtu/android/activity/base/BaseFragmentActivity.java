package net.wangtu.android.activity.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import net.wangtu.android.R;
import net.wangtu.android.common.statusbar.SystemStatusManager;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.util.HeaderUtil;

/**
 * Created by zhangxz on 2017/7/3.
 */

public class BaseFragmentActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toCreate(savedInstanceState);

        //状态栏透明
        HeaderUtil.initFraggmentStatusBar(this);
        //设置padding
        View headerView = findViewById(R.id.header_container);
    }

    protected void toCreate(Bundle savedInstanceState) {

    }
}

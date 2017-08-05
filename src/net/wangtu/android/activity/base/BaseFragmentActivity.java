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
import net.wangtu.android.util.ToastUtil;

/**
 * Created by zhangxz on 2017/7/3.
 */

public class BaseFragmentActivity extends FragmentActivity implements ToastUtil.LoadingInterface {
    private View loadingView;

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

    public synchronized void startLoading(){
        if(loadingView == null){
            loadingView = View.inflate(BaseFragmentActivity.this, R.layout.common_loading, null);
            ViewGroup container = (ViewGroup) ((ViewGroup)findViewById(android.R.id.content));
            container.addView(loadingView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            loadingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //不需要任何操作，只是为了消费事件
                }
            });
        }

        loadingView.setVisibility(View.VISIBLE);
        loadingView.bringToFront();
    }

    public synchronized void stopLoading(){
        loadingView.setVisibility(View.GONE);
    }
}

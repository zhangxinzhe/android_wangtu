package net.wangtu.android.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.MyRewardDetailBiddingActivity;
import net.wangtu.android.common.statusbar.SystemStatusManager;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.util.ActivityUtil;
import net.wangtu.android.util.ToastUtil;

import java.util.zip.Inflater;

/**
 * Created by zhangxz on 2017/7/3.
 */

public class BaseActivity extends Activity implements ToastUtil.LoadingInterface {
    private Handler handler;

    protected TextView headerTitle;
    protected ImageView headerBack;
    protected View headerView;
    protected int statusColor = R.color.background_color;
    private View loadingView;
    private RelativeLayout errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toCreate(savedInstanceState);

        headerView = findViewById(R.id.header_container);

        //状态栏透明
        initStatusBar();

        handler = new Handler();
    }

    protected void toCreate(Bundle savedInstanceState) {

    }

    protected void initHeader(String title,boolean showBack) {
        if(!ValidateUtil.isBlank(title)){
            headerTitle = (TextView) findViewById(R.id.header_title);
            headerTitle.setText(title);
        }

        headerBack = (ImageView) findViewById(R.id.header_back);
        if(showBack){
            headerBack.setVisibility(View.VISIBLE);
            headerBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }else{
            headerBack.setVisibility(View.GONE);
        }
    }

    public void initStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //bar改变颜色
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            win.setAttributes(winParams);

            SystemStatusManager tintManager = new SystemStatusManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(0);//状态栏无背景

            ViewGroup container = (ViewGroup)findViewById(android.R.id.content);
            View contentView = container.getChildAt(0);
            contentView.setPadding(0, Util.getStatusHeight(this), 0, 0);
            View backgroundStatusView = new View(this);
            backgroundStatusView.setBackgroundColor(getResources().getColor(statusColor));
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Util.getStatusHeight(this));
            backgroundStatusView.setLayoutParams(layoutParams);
            container.addView(backgroundStatusView);
            backgroundStatusView.bringToFront();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        ActivityUtil.startActivityForResult(this,  ActivityUtil.RIGHT);
    }

    @Override
    public void finish() {
        super.finish();
        ActivityUtil.finish(this, ActivityUtil.RIGHT);
    }

    public void post(Runnable runnable){
        handler.post(runnable);
    }

    public synchronized void startLoading(){
        if(loadingView == null){
            loadingView = View.inflate(BaseActivity.this, R.layout.common_loading, null);
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
    }

    public synchronized void stopLoading(){
        if(loadingView != null){
            loadingView.setVisibility(View.GONE);
        }
    }

    public void showError(){
        if(errorView == null){
            errorView = new RelativeLayout(this);
            errorView.setBackgroundResource(R.color.transparent);
            ViewGroup container = (ViewGroup) ((ViewGroup)findViewById(android.R.id.content));
            container.addView(errorView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            View errorView1 = View.inflate(BaseActivity.this, R.layout.common_error_view, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0,Util.getStatusHeightIfNeed(this) + Util.dip2px(this,64),0,0);
            errorView.addView(errorView1,params);
        }
        errorView.setVisibility(View.VISIBLE);
    }

    public void closeError(){
        if(loadingView != null){
            loadingView.setVisibility(View.GONE);
        }
    }
}

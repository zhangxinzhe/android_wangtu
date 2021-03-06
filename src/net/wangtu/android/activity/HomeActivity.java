package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.igexin.sdk.PushManager;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseFragmentActivity;
import net.wangtu.android.activity.common.AvatarActivity;
import net.wangtu.android.common.compoment.getui.GeTuiIntentService;
import net.wangtu.android.common.compoment.getui.GeTuiService;
import net.wangtu.android.common.util.ImageCacheUtil;
import net.wangtu.android.fragment.HomeFragment;
import net.wangtu.android.fragment.SearchFragment;
import net.wangtu.android.util.BalanceUtil;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.album.XImageUtil;
import net.wangtu.android.view.RewardCreateView;
import net.wangtu.android.view.RewardEditView;
import net.wangtu.android.util.LoginUtil;

/**
 * Created by zhangxz on 2017/7/3.
 */

public class HomeActivity extends BaseFragmentActivity{
    private static final String FRAGMENT_TAG_PREFIX = "Fragment_";// 前缀

    private FragmentTabHost tabHost;
    private DrawerLayout drawerLayout;
    private RelativeLayout leftbar;
    private RewardCreateView rewardPublishView;
    private View[] mainTabBtns;
    private int currentTab = -1;// 当前tab所在下标索引
    private View balanceContainer;
    private TextView balanceTxt;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);

        setContentView(R.layout.home);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        leftbar = (RelativeLayout)findViewById(R.id.home_leftbar);

        initFragment();

        initTabUI();

        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageView imageView =  (ImageView)leftbar.findViewById(R.id.leftbar_header_img);
        ImageCacheUtil.lazyLoad(imageView,LoginUtil.getAvatarFile(),R.drawable.icon_header,false);

        TextView realName =  (TextView)leftbar.findViewById(R.id.leftbar_real_name);
        realName.setText(LoginUtil.getRealName());

        //余额显示控制
        BalanceUtil.initBalance(this, new Runnable() {
            @Override
            public void run() {
                if(balanceContainer == null){
                    balanceContainer = leftbar.findViewById(R.id.balance_container);
                }
                if(balanceTxt == null){
                    balanceTxt = (TextView)balanceContainer.findViewById(R.id.txt_balance);
                }

                if(BalanceUtil.getBalance() < 0){
                    balanceContainer.setVisibility(View.GONE);
                }else{
                    balanceContainer.setVisibility(View.VISIBLE);
                    balanceTxt.setText("￥" + BalanceUtil.getBalance());
                }
            }
        });
    }

    /**
     * 初始化正文内容
     */
    private void initFragment(){
        tabHost = (FragmentTabHost)findViewById(R.id.tab_host);

        // 正文
        tabHost.setup(this, getSupportFragmentManager(), R.id.real_tab_content);

        //home
        TabHost.TabSpec tabHome = tabHost.newTabSpec(FRAGMENT_TAG_PREFIX + 0).setIndicator(String.valueOf(0));
        tabHost.addTab(tabHome, HomeFragment.class, null);

        //search
        TabHost.TabSpec tabSearch = tabHost.newTabSpec(FRAGMENT_TAG_PREFIX + 2).setIndicator(String.valueOf(2));
        tabHost.addTab(tabSearch, SearchFragment.class, null);
    }

    /**
     * 初始化tab
     */
    private void initTabUI() {
        mainTabBtns = new View[3];
        mainTabBtns[0] = findViewById(R.id.homeBtn);
        mainTabBtns[1] = findViewById(R.id.rewardBtn);
        mainTabBtns[2] = findViewById(R.id.searchBtn);
    }

    /**
     * 事件绑定
     */
    private void initEvent(){
        //选中样式
        mainTabBtns[0].setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabIndex(0);
            }
        });
        mainTabBtns[1].setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!LoginUtil.isLogin()){
                    ToastUtil.login(HomeActivity.this);
                    return;
                }
                if(rewardPublishView == null){
                    rewardPublishView = (RewardCreateView)((ViewStub)findViewById(R.id.rewardPublishStub)).inflate();
                }
                rewardPublishView.setVisibility(View.VISIBLE);
            }
        });
        mainTabBtns[2].setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabIndex(1);
            }
        });

        //默认选中的tab
        Bundle bundle = getIntent().getExtras();
        int tabIndex = -1;
        if(bundle != null){
            tabIndex = (int)bundle.get("tabIndex");
        }
        if(tabIndex < 0){
            tabIndex = 0;
        }
        tabIndex(tabIndex);
    }

    /***
     * 页面切换
     *
     * @param tabIndex
     *            选项卡下标
     */
    public void tabIndex(int tabIndex) {
        if (currentTab == tabIndex) {
            return;
        }

        currentTab = tabIndex;
        tabHost.setCurrentTab(currentTab);
    }

    public void openLeftBarOnClick(View view){
        if(!LoginUtil.isLogin()){
            ToastUtil.login(this);
            return;
        }

        if (drawerLayout.isDrawerOpen(leftbar)) {
            drawerLayout.closeDrawer(leftbar);
        }else{
            drawerLayout.openDrawer(leftbar);
        }
    }

    public void myTaskOnClick(View view){
        if(!LoginUtil.isLogin()){
            ToastUtil.login(HomeActivity.this);
        }else{
            Intent intent = new Intent(this,MyTaskActivity.class);
            intent.putExtra("tab","myTask");
            startActivity(intent);
        }

        if (drawerLayout.isDrawerOpen(leftbar)) {
            drawerLayout.closeDrawer(leftbar);
        }
    }

    public void myRewardOnClick(View view){
        if(!LoginUtil.isLogin()){
            ToastUtil.login(HomeActivity.this);
        }else{
            Intent intent = new Intent(this,MyTaskActivity.class);
            intent.putExtra("type","myReward");
            startActivity(intent);
        }

        if (drawerLayout.isDrawerOpen(leftbar)) {
            drawerLayout.closeDrawer(leftbar);
        }
    }

    public void userInfoOnClick(View view){
        if(!LoginUtil.isLogin()){
            ToastUtil.login(this);
        }else{
            Intent intent = new Intent(this,UserInfoActivity.class);
            intent.putExtra("userId",LoginUtil.getUserId());
            startActivity(intent);
        }

        if (drawerLayout.isDrawerOpen(leftbar)) {
            drawerLayout.closeDrawer(leftbar);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        rewardPublishView.getRewardEditView().onActivityResult(requestCode,resultCode,data);
    }

    public void settingOnClick(View view){
        if(!LoginUtil.isLogin()){
            ToastUtil.login(this);
            return;
        }
        Intent intent = new Intent(this,SettingActivity.class);
        startActivity(intent);
        if (drawerLayout.isDrawerOpen(leftbar)) {
            drawerLayout.closeDrawer(leftbar);
        }
    }

    public void myNoticeOnClick(View view){
        if(!LoginUtil.isLogin()){
            ToastUtil.login(HomeActivity.this);
            return;
        }

        Intent intent = new Intent(this,MyNoticeActivity.class);
        startActivity(intent);
        if (drawerLayout.isDrawerOpen(leftbar)) {
            drawerLayout.closeDrawer(leftbar);
        }
    }
}

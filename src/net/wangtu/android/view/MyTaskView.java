package net.wangtu.android.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.LiquidatedDamagesPayActivity;
import net.wangtu.android.activity.MyRewardDetailBiddingActivity;
import net.wangtu.android.activity.MyRewardDetailPublishActivity;
import net.wangtu.android.activity.MyRewardDetailResultActivity;
import net.wangtu.android.activity.MyTaskDetailBiddingActivity;
import net.wangtu.android.activity.MyTaskDetailPayActivity;
import net.wangtu.android.activity.MyTaskDetailProgressActivity;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.view.dialog.BoxView;
import net.wangtu.android.common.view.refresh.DefaultRefreshViewEvent;
import net.wangtu.android.common.view.refresh.RefreshView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 我的任务
 */

public class MyTaskView extends RefreshView{
    private JSONArray datas;
    private ListView listView;
    private MyTaskAdapter adapter;
    private int currentPage;
    private int totalPage;
    private boolean inited;

    public MyTaskView(Context context) {
        this(context, null);
    }

    public MyTaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTaskView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(inited){
            return;
        }
        inited = true;

        initUI();
        initEvent();
        initData();
    }

    private void initUI(){
        listView = (ListView)findViewById(R.id.listView);
    }

    private void initEvent(){
        // 初始化刷新事件
        setRefreshViewEvent(new DefaultRefreshViewEvent() {
            @Override
            public void startVerticalRefresh() {
                getDataFromServer(1, true);
            }

            @Override
            public boolean showVerticalRefresh() {
                return true;
            }

            @Override
            public void startVerticalLoad() {
                if (currentPage < totalPage) {
                    getDataFromServer(++currentPage, false);
                } else {
                    MyTaskView.this.stopVerticalLoad(totalPage == 1);
                }
            }

            @Override
            public boolean showVerticalLoad() {
                return true;
            }
        });

        //选择
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final JSONObject dataJson = datas.optJSONObject(position);
                if(dataJson == null){
                    return;
                }
                String rewardStatus = dataJson.optString("biddingStatus");
                final String rewardId = dataJson.optString("id");
                Intent intent = null;
                //未支付，点击跳到平台使用费
                if(Constants.Bidding_STATUS_UNPAY.equals(rewardStatus)){
                    ToastUtil.confirm(getContext(),"提示", "支付完平台使用费，才能开始竞价！","支付","取消", new ToastUtil.DialogOnClickListener() {
                        @Override
                        public void onClick(BoxView dialog) {
                            Intent intent = new Intent(getContext(),LiquidatedDamagesPayActivity.class);
                            intent.putExtra("rewardId",rewardId);
                            getContext().startActivity(intent);
                        }
                    },null);
                }
                //已支付，竞价中
                else if(Constants.Bidding_STATUS_PAY.equals(rewardStatus)){
                    intent = new Intent(getContext(),MyTaskDetailBiddingActivity.class);
                    intent.putExtra("rewardId",rewardId);
                    getContext().startActivity(intent);
                //竞价成功，开始任务
                }else if(Constants.Bidding_STATUS_SUCCESS.equals(rewardStatus)){
                    intent = new Intent(getContext(),MyTaskDetailProgressActivity.class);
                    intent.putExtra("rewardId",rewardId);
                    getContext().startActivity(intent);
                }
                //竞价失败
                else{
                    intent = new Intent(getContext(),MyTaskDetailProgressActivity.class);
                    intent.putExtra("rewardId",rewardId);
                    getContext().startActivity(intent);
                }
            }
        });
    }

    private void initData(){
        datas = new JSONArray();
        adapter = new MyTaskAdapter();
        listView.setAdapter(adapter);
        getDataFromServer(currentPage = 1,false);
    }

    public void getDataFromServer(final int currentPage,final boolean isReload){
        if(currentPage == 1 && !isReload){
            startLoading();
        }else if(currentPage > 1){
            startVerticalLoad();
        }
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_MY_TASK) + "?page.currentPage=" + currentPage;
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,(Activity)getContext());
                    //分页
                    JSONObject pageObj = dataJson.optJSONObject("page");
                    if (pageObj != null) {
                        MyTaskView.this.totalPage = pageObj.optInt("totalPage");
                        MyTaskView.this.currentPage = pageObj.optInt("currentPage");
                    }

                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            removeSpecialView();
                            stopLoading();
                            if(isReload){
                                datas = dataJson.optJSONArray("list");
                            }else{
                                JSONArray list = dataJson.optJSONArray("list");
                                if (list != null && list.length() > 0) {
                                    for (int i = 0; i < list.length(); i++) {
                                        datas.put(list.optJSONObject(i));
                                    }
                                }
                            }
                            if(datas == null || datas.length() <= 0){
                                showSpecialViewResource(R.layout.common_empty,null);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            stopLoading();
                            if(currentPage > 1){
                                failVerticalLoad();
                            }
                            if(datas != null && datas.length() > 0){
                                ToastUtil.error(getContext(),"网络访问失败！");
                            }else{
                                showSpecialViewResource(R.layout.common_error_view,null);
                            }
                        }
                    });
                }
            }
        });
    }

    private class MyTaskAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (datas == null || datas.length() <= 0) {
                return 0;
            }
            return datas.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (datas == null || datas.length() <= 0) {
                return null;
            }
            return datas.optJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyTaskItemView myTaskItemView = null;
            if (convertView == null) {
                convertView  = (MyTaskItemView)View.inflate(getContext(), R.layout.my_task_my_task_item, null);
                myTaskItemView = (MyTaskItemView)convertView;
                myTaskItemView.initUI();
            }else{
                myTaskItemView = (MyTaskItemView)convertView;
            }

            try {
                myTaskItemView.initData(datas.getJSONObject(position),position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
}

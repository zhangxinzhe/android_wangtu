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
import net.wangtu.android.activity.MyRewardDetailBiddingActivity;
import net.wangtu.android.activity.MyRewardDetailPayActivity;
import net.wangtu.android.activity.MyRewardDetailPublishActivity;
import net.wangtu.android.activity.MyRewardDetailResultActivity;
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
 * 我的悬赏
 */

public class MyRewardView extends RefreshView{
    private JSONArray datas;
    private ListView listView;
    private MyRewardAdapter adapter;
    private int currentPage;
    private int totalPage;
    private boolean inited;

    public MyRewardView(Context context) {
        this(context, null);
    }

    public MyRewardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRewardView(Context context, AttributeSet attrs, int defStyle) {
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
                    MyRewardView.this.stopVerticalLoad(totalPage == 1);
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
                String rewardStatus = dataJson.optString("status");
                Intent intent = null;
                //发布
                if(Constants.REWARD_STATUS_CREATE.equals(rewardStatus)){
                    intent = new Intent(getContext(),MyRewardDetailPublishActivity.class);
                    intent.putExtra("rewardId",dataJson.optString("id"));
                    getContext().startActivity(intent);
                }
                //竞价
                else if(Constants.REWARD_STATUS_PUBLISH.equals(rewardStatus)){
                    intent = new Intent(getContext(),MyRewardDetailBiddingActivity.class);
                    intent.putExtra("rewardId",dataJson.optString("id"));
                    getContext().startActivity(intent);
                }
                //已接单
                else if(Constants.REWARD_STATUS_DOING.equals(rewardStatus)){
                    intent = new Intent(getContext(),MyRewardDetailResultActivity.class);
                    intent.putExtra("rewardId",dataJson.optString("id"));
                    getContext().startActivity(intent);
                }
                //撤销
                else if(Constants.REWARD_STATUS_CANCEL.equals(rewardStatus)){
                    intent = new Intent(getContext(),MyRewardDetailResultActivity.class);
                    intent.putExtra("rewardId",dataJson.optString("id"));
                    getContext().startActivity(intent);
                }
                //完成
                else{
                    intent = new Intent(getContext(),MyRewardDetailResultActivity.class);
                    intent.putExtra("rewardId",dataJson.optString("id"));
                    getContext().startActivity(intent);
                }
            }
        });

        //长按删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final JSONObject dataJson = datas.optJSONObject(position);
                if(dataJson == null){
                    return false;
                }
                String rewardStatus = dataJson.optString("status");
                if(Constants.REWARD_STATUS_CREATE.equals(rewardStatus)){
                    ToastUtil.confirm(getContext(), "确定要删除？", new ToastUtil.DialogOnClickListener() {
                        @Override
                        public void onClick(BoxView v) {
                            v.dismiss();
                            delReward(dataJson.optString("id"));
                        }
                    }, new ToastUtil.DialogOnClickListener() {
                        @Override
                        public void onClick(BoxView v) {
                            v.dismiss();
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }

    private void initData(){
        datas = new JSONArray();
        adapter = new MyRewardAdapter();
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
                String url = WangTuUtil.getPage(Constants.API_MY_REWARD) + "?page.currentPage=" + currentPage;
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,(Activity)getContext());
                    //分页
                    JSONObject pageObj = dataJson.optJSONObject("page");
                    if (pageObj != null) {
                        MyRewardView.this.totalPage = pageObj.optInt("totalPage");
                        MyRewardView.this.currentPage = pageObj.optInt("currentPage");
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

    /**
     * 删除悬赏
     * @param rewardId
     */
    private void delReward(final String rewardId){
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_DELETE_REWARD) + "?rewardId=" + rewardId;
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,(Activity)getContext());
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(getContext());
                            if("success".equals(dataJson.optString("msg"))){
                                ToastUtil.alert(getContext(), "删除成功！", new ToastUtil.DialogOnClickListener() {
                                    @Override
                                    public void onClick(BoxView v) {
                                        v.dismiss();
                                        getDataFromServer(currentPage = 1,true);
                                    }
                                });
                            }else{
                                ToastUtil.error(getContext(),dataJson.optString("msg"));
                            }
                        }
                    });
                } catch (Exception e) {
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(getContext());
                            ToastUtil.error(getContext(),"网络访问失败！");
                        }
                    });
                }
            }
        });
    }

    private class MyRewardAdapter extends BaseAdapter {
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
            MyRewardItemView myRewardItemView = null;
            if (convertView == null) {
                convertView  = (MyRewardItemView)View.inflate(getContext(), R.layout.my_task_my_reward_item, null);
                myRewardItemView = (MyRewardItemView)convertView;
                myRewardItemView.initUI();
            }else{
                myRewardItemView = (MyRewardItemView)convertView;
            }

            try {
                myRewardItemView.initData(datas.getJSONObject(position),position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
}

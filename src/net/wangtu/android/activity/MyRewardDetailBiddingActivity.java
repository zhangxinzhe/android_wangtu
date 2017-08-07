package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.dialog.AlertView;
import net.wangtu.android.common.view.dialog.BoxView;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.view.MyRewardDetailBiddingItemView;
import net.wangtu.android.view.RewardReadView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 悬赏竞价中页面
 */

public class MyRewardDetailBiddingActivity extends BaseActivity{
    private ListView designerListView;
    private DesignerListViewAdapter adapter;
    private RewardReadView rewardReadView;
    private JSONArray biddingList;
    private JSONObject dataJson;
    private MyRewardDetailBiddingItemView checkedItem;
    private String rewardId;
    private String biddingId;


    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_reward_detail_bidding);
        initHeader("详情",true);

        Bundle bundle = getIntent().getExtras();
        rewardId = bundle.getString("rewardId");
        initUI();
        initEvent();
        initData();
    }

    private void initUI(){
        rewardReadView = (RewardReadView)findViewById(R.id.reward_read_view);
        designerListView = (ListView)findViewById(R.id.designerListView);
    }

    private void initEvent(){
        designerListView.setAdapter(adapter = new DesignerListViewAdapter());
        designerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(checkedItem != null){
                    checkedItem.checked(false);
                }
                checkedItem = (MyRewardDetailBiddingItemView)view;
                checkedItem.checked(true);
                biddingId = biddingList.optJSONObject(position).optString("id");
            }
        });
    }

    private void initData(){
        biddingList = new JSONArray();
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_REWARD_DETAIL) + "?rewardId=" + rewardId;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,MyRewardDetailBiddingActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyRewardDetailBiddingActivity.this);
                            JSONObject reward = dataJson.optJSONObject("reward");
                            rewardReadView.initData(reward);
                            if(reward != null && !reward.isNull("biddingList")){
                                biddingList = reward.optJSONArray("biddingList");
                                adapter.notifyDataSetChanged();
                                setListViewHeightBasedOnChildren(designerListView);
                            }
                        }
                    });

                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyRewardDetailBiddingActivity.this);
                            showError();
                        }
                    });
                }
            }
        });
    }

    public void chooseOnClick(View view){
        if(ValidateUtil.isBlank(biddingId)){
            ToastUtil.error(this,"请选择接单人！");
            return;
        }

        final ConfirmView confirmView = new ConfirmView(this);
        confirmView.setTitleMsg("选择提示！");
        confirmView.setContentMsg("您确定要选择他，完成此任务？");
        confirmView.setOkBtnName("确定");
        confirmView.setOkBtnListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmView.dismiss();
                makeSureChoose();
            }
        });
        confirmView.show();
    }

    private void makeSureChoose(){
        ToastUtil.startLoading(this);
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_CHOOSE_BIDDING) + "?biddingId=" + biddingId;
                try {
                    dataJson = WangTuHttpUtil.getJson(url,MyRewardDetailBiddingActivity.this);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyRewardDetailBiddingActivity.this);
                            if("success".equals(dataJson.optString("msg"))){
                                ToastUtil.alert(MyRewardDetailBiddingActivity.this, "操作成功！", new ToastUtil.DialogOnClickListener() {
                                    @Override
                                    public void onClick(BoxView dialog) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                            }else{
                                ToastUtil.error(getApplicationContext(),dataJson.optString("msg"));
                            }
                        }
                    });

                } catch (Exception e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.stopLoading(MyRewardDetailBiddingActivity.this);
                            ToastUtil.error(MyRewardDetailBiddingActivity.this,"请求失败");
                        }
                    });
                }
            }
        });
    }

    public void waitOnClick(View view){
        finish();
    }

    private class DesignerListViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (biddingList == null || biddingList.length() <= 0) {
                return 0;
            }
            return biddingList.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (biddingList == null || biddingList.length() <= 0) {
                return null;
            }
            return biddingList.optJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyRewardDetailBiddingItemView itemView = null;
            if (convertView == null) {
                convertView = View.inflate(MyRewardDetailBiddingActivity.this, R.layout.my_reward_detail_bidding_item, null);
                itemView = (MyRewardDetailBiddingItemView)convertView;
                itemView.initUI();
            } else {
                itemView = (MyRewardDetailBiddingItemView) convertView;
            }
            itemView.initData(biddingList.optJSONObject(position),position);
            return convertView;
        }
    }

    //第一种方法
    public static void setListViewHeightBasedOnChildren(ListView listView){
        if (listView==null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
        // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}

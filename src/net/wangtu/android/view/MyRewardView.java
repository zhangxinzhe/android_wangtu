package net.wangtu.android.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import net.wangtu.android.R;
import net.wangtu.android.activity.MyRewardDetailBiddingActivity;
import net.wangtu.android.activity.MyRewardDetailPayActivity;
import net.wangtu.android.activity.MyRewardDetailPublishActivity;
import net.wangtu.android.activity.MyRewardDetailResultActivity;
import net.wangtu.android.activity.MyTaskActivity;
import net.wangtu.android.activity.RewardPayActivity;
import net.wangtu.android.common.view.dialog.AlertView;
import net.wangtu.android.common.view.refresh.DefaultRefreshViewEvent;
import net.wangtu.android.common.view.refresh.RefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 我的悬赏
 */

public class MyRewardView extends RefreshView{
    private JSONArray datas;
    private ListView listView;
    private MyTaskAdapter adapter;
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

        init();
    }

    public void init(){
        listView = (ListView)findViewById(R.id.listView);
        datas = new JSONArray();
        adapter = new MyTaskAdapter();
        listView.setAdapter(adapter);
        //选择
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch(position){
                    //发布
                    case 0:
                        intent = new Intent(getContext(),MyRewardDetailPublishActivity.class);
                        getContext().startActivity(intent);
                        break;
                    //竞价
                    case 1:
                        intent = new Intent(getContext(),MyRewardDetailBiddingActivity.class);
                        getContext().startActivity(intent);
                        break;
                    //确认或支付
                    case 2:
                        intent = new Intent(getContext(),MyRewardDetailPayActivity.class);
                        getContext().startActivity(intent);
                        break;
                    //结果：
                    default:
                        intent = new Intent(getContext(),MyRewardDetailResultActivity.class);
                        getContext().startActivity(intent);
                        break;

                }
            }
        });

        //长按删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    final AlertView alertView = new AlertView(getContext());
                    alertView.setTitleMsg("提示");
                    alertView.setContentMsg("确认删除");
                    alertView.setOkBtnName("确定");
                    alertView.setOkBtnListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            alertView.dismiss();
                        }
                    });
                    alertView.show();
                    return true;
                }
                return false;
            }
        });

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
                    MyRewardView.this.startVerticalLoad();
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

        getDataFromServer(0,false);
    }

    public void getDataFromServer(int currentPage,boolean isReload){
        startLoading();
        //showSpecialViewResource(R.layout.common_error_view,null);
        datas = new JSONArray();
        for (int i =0;i< 7;i++){
            datas.put(new JSONObject());
        }
        adapter.notifyDataSetChanged();
        stopLoading();
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

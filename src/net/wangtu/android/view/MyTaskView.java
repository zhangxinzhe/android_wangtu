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
import net.wangtu.android.activity.MyTaskDetailBiddingActivity;
import net.wangtu.android.activity.MyTaskDetailPayActivity;
import net.wangtu.android.activity.MyTaskDetailProgressActivity;
import net.wangtu.android.common.view.refresh.DefaultRefreshViewEvent;
import net.wangtu.android.common.view.refresh.RefreshView;

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

        init();
    }

    public void init(){
        listView = (ListView)findViewById(R.id.listView);
        datas = new JSONArray();
        adapter = new MyTaskAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch(position){
                    case 0:
                        intent = new Intent(getContext(),MyTaskDetailBiddingActivity.class);
                        getContext().startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(getContext(),MyTaskDetailPayActivity.class);
                        getContext().startActivity(intent);
                        break;
                    default:
                        intent = new Intent(getContext(),MyTaskDetailProgressActivity.class);
                        getContext().startActivity(intent);
                        break;
                }
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
                    MyTaskView.this.startVerticalLoad();
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

        getDataFromServer(0,false);
    }

    public void getDataFromServer(int currentPage,boolean isReload){
        startLoading();
        datas = new JSONArray();
        for (int i =0;i< 30;i++){
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

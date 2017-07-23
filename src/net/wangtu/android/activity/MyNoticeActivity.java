package net.wangtu.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.view.refresh.DefaultRefreshViewEvent;
import net.wangtu.android.common.view.refresh.RefreshView;
import net.wangtu.android.fragment.HomeFragment;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class MyNoticeActivity extends BaseActivity {
    private ListView listView;
    private RefreshView refreshView;
    private MyNoticeActivity.NoticeAdapter searchAdapter;
    private JSONArray rewardArray;
    private int currentPage;
    private int totalPage;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_notice);

        initHeader("我的消息",true);

        listView = (ListView)findViewById(R.id.listView);
        refreshView = (RefreshView)findViewById(R.id.refreshView);

        rewardArray = new JSONArray();
        searchAdapter = new MyNoticeActivity.NoticeAdapter();
        listView.setAdapter(searchAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MyNoticeActivity.this,RewardDetailActivity.class);
//                startActivity(intent);
            }
        });

        // 初始化刷新事件
        refreshView.setRefreshViewEvent(new DefaultRefreshViewEvent() {
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
                    refreshView.startVerticalLoad();
                    getDataFromServer(++currentPage, false);
                } else {
                    refreshView.stopVerticalLoad(totalPage == 1);
                }
            }

            @Override
            public boolean showVerticalLoad() {
                return true;
            }
        });
        getDataFromServer(1, true);
    }

    private void getDataFromServer(int currentPage,boolean isReload){
        for (int i =0;i< 30;i++){
            rewardArray.put(i);
        }

        refreshView.stopLoading();
    }

    private class NoticeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (rewardArray == null || rewardArray.length() <= 0) {
                return 0;
            }
            return rewardArray.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (rewardArray == null || rewardArray.length() <= 0) {
                return null;
            }
            return rewardArray.optJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MyNoticeActivity.this, R.layout.my_notice_item, null);
            } else {
                //itemView = (CourseListViewItemEvent) convertView;
            }

            return convertView;
        }
    }
}

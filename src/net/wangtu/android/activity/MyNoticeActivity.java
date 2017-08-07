package net.wangtu.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.UrlUtil;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.refresh.DefaultRefreshViewEvent;
import net.wangtu.android.common.view.refresh.RefreshView;
import net.wangtu.android.fragment.HomeFragment;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.view.MyTaskView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangxz on 2017/7/6.
 */

public class MyNoticeActivity extends BaseActivity {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private ListView listView;
    private RefreshView refreshView;
    private MyNoticeActivity.NoticeAdapter searchAdapter;
    private JSONArray datas;
    private int currentPage;
    private int totalPage;

    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_notice);
        initHeader("我的消息",true);
        initUI();
        initEvent();
        initData();
    }

    private void initUI(){
        listView = (ListView)findViewById(R.id.listView);
        refreshView = (RefreshView)findViewById(R.id.refreshView);
    }

    private void initEvent(){
        datas = new JSONArray();
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
    }

    private void initData(){
        getDataFromServer(1, false);
    }

    private void getDataFromServer(final int currentPage,final boolean isReload){
        if(currentPage == 1 && !isReload){
            refreshView.startLoading();
        }
        if(currentPage > 1){
            refreshView.startVerticalLoad();
        }
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_PUSH_MSG_LIST) + "?page.currentPage=" + currentPage;
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,MyNoticeActivity.this);
                    //分页
                    JSONObject pageObj = dataJson.optJSONObject("page");
                    if (pageObj != null) {
                        MyNoticeActivity.this.totalPage = pageObj.optInt("totalPage");
                        MyNoticeActivity.this.currentPage = pageObj.optInt("currentPage");
                    }

                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshView.removeSpecialView();
                            refreshView.stopLoading();
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
                                refreshView.showSpecialViewResource(R.layout.common_empty,null);
                            }
                            searchAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            if(currentPage > 1){
                                refreshView.failVerticalLoad();
                            }
                            refreshView.stopLoading();
                            if(datas != null && datas.length() > 0){
                                ToastUtil.error(MyNoticeActivity.this,"网络访问失败！");
                            }else{
                                refreshView.showSpecialViewResource(R.layout.common_error_view,null);
                            }
                        }
                    });
                }
            }
        });
    }

    private class NoticeAdapter extends BaseAdapter {
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
            TextView itemContent = null;
            TextView itemTime = null;
            if (convertView == null) {
                convertView = View.inflate(MyNoticeActivity.this, R.layout.my_notice_item, null);
                itemContent = (TextView) convertView.findViewById(R.id.item_content);
                itemTime = (TextView) convertView.findViewById(R.id.item_time);
                convertView.setTag(new ViewHolder(itemContent,itemTime));
            } else {
                ViewHolder viewHolder = (ViewHolder)convertView.getTag();
                itemContent = viewHolder.getItemContent();
                itemTime = viewHolder.getItemTime();
            }
            JSONObject itemJson = datas.optJSONObject(position);
            itemContent.setText(itemJson.optString("content"));
            itemTime.setText(dateFormat.format(new Date(itemJson.optLong("createTime"))));
            return convertView;
        }
    }

    public class ViewHolder{
        private TextView itemContent;
        private TextView itemTime;

        public  ViewHolder(TextView itemContent,TextView itemTime){
            this.itemContent = itemContent;
            this.itemTime = itemTime;
        }

        public TextView getItemContent() {
            return itemContent;
        }

        public TextView getItemTime() {
            return itemTime;
        }
    }
}

package net.wangtu.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.RewardDetailActivity;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.UrlUtil;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.refresh.DefaultRefreshViewEvent;
import net.wangtu.android.common.view.refresh.RefreshView;
import net.wangtu.android.db.service.CourseSearchLogService;
import net.wangtu.android.util.LogUtil;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.view.HomeFragmentItemView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangxz on 2017/7/4.
 */

public class SearchFragment extends BaseFragment {
    private CourseSearchLogService courseSearchLogService = new CourseSearchLogService();

    private EditText searchContent;
    private Button searchBtn;
    private ListView listView;
    private RefreshView refreshView;
    private ListView historyListView;
    private View historyListViewContainer;
    private SearchFragment.SearchAdapter searchAdapter;
    private ArrayAdapter<String> historyAdapter;
    private JSONArray rewardArray;
    private List<String> searchHistory;
    private int currentPage = 1;
    private int totalPage;
    private boolean isReload = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View toCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View childView = inflater.inflate(R.layout.home_fragment_search, container, false);
        initUI(childView);
        initEvent(childView);
        initHistoryData();
        return childView;
    }

    private void initUI(View childView ){
        searchContent = (EditText) childView.findViewById(R.id.search_content);
        searchBtn = (Button) childView.findViewById(R.id.search_btn);
        historyListView = (ListView)childView.findViewById(R.id.history_list_view);
        historyListViewContainer = childView.findViewById(R.id.history_list_view_container);
        listView = (ListView)childView.findViewById(R.id.listView);
        refreshView = (RefreshView)childView.findViewById(R.id.refreshView);
    }

    private void initEvent(View childView ){
        rewardArray = new JSONArray();
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
        //列表
        searchAdapter = new SearchAdapter();
        listView.setAdapter(searchAdapter);

        //选择指定悬赏
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(getActivity(),RewardDetailActivity.class);
                    intent.putExtra("rewardId",rewardArray.getJSONObject(position).optString("id"));
                    startActivity(intent);
                } catch (JSONException e) {
                    ToastUtil.error(getContext(),"打开失败！");
                }
            }
        });
    }

    private void initHistoryData(){
        //搜索事件
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 搜素内容
                String courseName = searchContent.getText().toString();
                // 保存查询内容
                courseSearchLogService.updateOrAddCourseSearchLog(courseName, new Date());
                // 转码
                try {
                    courseName = URLEncoder.encode(courseName, "utf-8");
                } catch (Exception e) {
                    LogUtil.error(e);
                }
                historyListViewContainer.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                getDataFromServer(1,true);
            }
        });

        //初始化
        searchHistory = new ArrayList<String>();
        historyAdapter = new ArrayAdapter<String>(getContext(), R.layout.home_fragment_search_item, searchHistory);
        historyListView.setAdapter(historyAdapter);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchContent.setText(searchHistory.get(position));
                getDataFromServer(1,true);
            }
        });

        //加载数据
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                List<String> data = courseSearchLogService.getCourseSearchLog();
                if (ValidateUtil.isEmpty(data)) {
                    historyListView.post(new Runnable() {
                        @Override
                        public void run() {
                            historyListView.setVisibility(View.GONE);
                        }
                    });
                } else {
                    // 数据量过大，清空超过20条的
                    if (data.size() > 20) {
                        courseSearchLogService.clearCourseSearchLog(20);
                        for (int i = 20; i < data.size(); i++) {
                            data.remove(i);
                        }
                    }
                    searchHistory.addAll(data);
                    // 刷新数据
                    historyListView.post(new Runnable() {
                        @Override
                        public void run() {
                            historyAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void getDataFromServer(final int currentPage,final boolean isReload){
        historyListViewContainer.setVisibility(View.GONE);
        refreshView.setVisibility(View.VISIBLE);
        if(currentPage == 1 && !isReload){
            refreshView.startLoading();
        }
        if(currentPage > 1){
            refreshView.startVerticalLoad();
        }
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_LIST_REWARD) + "?page.currentPage=" + currentPage;
                String searchName = searchContent.getText().toString();
                try {
                    if(!ValidateUtil.isBlank(searchName)){
                        url = UrlUtil.addParams(url,"rewardTitle=" + URLEncoder.encode(searchName,"utf-8"));
                    }

                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,(Activity)getContext());
                    //分页
                    JSONObject pageObj = dataJson.optJSONObject("page");
                    if (pageObj != null) {
                        SearchFragment.this.totalPage = pageObj.optInt("totalPage");
                        SearchFragment.this.currentPage = pageObj.optInt("currentPage");
                    }

                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshView.removeSpecialView();
                            refreshView.stopLoading();
                            if(isReload){
                                rewardArray = dataJson.optJSONArray("list");
                            }else{
                                JSONArray list = dataJson.optJSONArray("list");
                                if (list != null && list.length() > 0) {
                                    for (int i = 0; i < list.length(); i++) {
                                        rewardArray.put(list.optJSONObject(i));
                                    }
                                }
                            }

                            if(rewardArray == null || rewardArray.length() <= 0){
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
                            if(rewardArray != null && rewardArray.length() > 0){
                                ToastUtil.error(getContext(),"网络访问失败！");
                            }else{
                                refreshView.showSpecialViewResource(R.layout.common_error_view,null);
                            }
                        }
                    });
                }
            }
        });
    }

    private class SearchAdapter extends BaseAdapter {
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
            HomeFragmentItemView homeFragmentItemView = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.home_fragment_item, null);
                homeFragmentItemView = (HomeFragmentItemView)convertView;
                homeFragmentItemView.initUI();
            } else {
                homeFragmentItemView = (HomeFragmentItemView) convertView;
            }
            homeFragmentItemView.initData((JSONObject) rewardArray.opt(position),position);
            return convertView;
        }
    }
}

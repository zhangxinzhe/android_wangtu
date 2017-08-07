package net.wangtu.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.activity.RewardDetailActivity;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.UrlUtil;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.refresh.DefaultRefreshViewEvent;
import net.wangtu.android.common.view.refresh.RefreshView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.view.HomeFragmentItemView;
import net.wangtu.android.view.SelectPopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangxz on 2017/7/4.
 */

public class HomeFragment extends BaseFragment {
    private ListView listView;
    private RefreshView refreshView;
    private SearchAdapter searchAdapter;
    private TextView filterBtn;
    private TextView sortBtn;
    private JSONArray rewardArray;
    private JSONArray catalogArray;
    private int currentPage = 1;
    private int totalPage;
    private boolean isReload = false;
    private String catalogId;
    private String sort;

    private SelectPopupWindow popupWindow;

    @Override
    public View toCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View childView = inflater.inflate(R.layout.home_fragment_home, container, false);
        initUI(childView);
        initEvent(childView);
        initData();
        return childView;
    }

    private void initUI(View childView){
        listView = (ListView)childView.findViewById(R.id.listView);
        refreshView = (RefreshView)childView.findViewById(R.id.refreshView);
        filterBtn = (TextView) childView.findViewById(R.id.btn_filter);
        sortBtn = (TextView) childView.findViewById(R.id.btn_sort);
    }

    private void initEvent(final View childView){
        //刷新控制
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

        //搜索条件控制
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow == null){
                    popupWindow = new SelectPopupWindow(childView,v);
                    popupWindow.initData(catalogArray);
                    popupWindow.setOnItemCheckListener(new SelectPopupWindow.OnItemCheckListener() {
                        @Override
                        public void onItemChecked(JSONObject data) throws JSONException {
                            filterBtn.setText(data.getString("key") + "\u0020");
                            catalogId = data.optString("value");
                            getDataFromServer(1,true);
                        }
                    });
                }
                if(ValidateUtil.isBlank(catalogId)){
                    popupWindow.setValue(catalogId);
                }
                popupWindow.show();
            }
        });
        // 排序方式控制
        final Drawable nav_up= listView.getResources().getDrawable(R.drawable.icon_arrow_up);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        final Drawable nav_down= listView.getResources().getDrawable(R.drawable.icon_arrow_down);
        nav_down.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        sortBtn.setOnClickListener(new View.OnClickListener() {
            private Drawable direct;
            @Override
            public void onClick(View v) {
                if(direct == null || direct == nav_down){
                    direct = nav_up;
                    sort = "up";
                }else{
                    direct = nav_down;
                    sort = "down";
                }
                sortBtn.setCompoundDrawables(null, null, direct, null);
                getDataFromServer(1,true);
            }
        });
    }

    private void initData(){
        rewardArray = new JSONArray();
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
                String url = WangTuUtil.getPage(Constants.API_LIST_REWARD) + "?page.currentPage=" + currentPage;
                if(!ValidateUtil.isBlank(catalogId)){
                    url = UrlUtil.addParams(url,"catalogId=" + catalogId);
                }
                if(!ValidateUtil.isBlank(sort)){
                    url = UrlUtil.addParams(url,"sort=" + sort);
                }
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,(Activity)getContext());
                    //分页
                    JSONObject pageObj = dataJson.optJSONObject("page");
                    if (pageObj != null) {
                        HomeFragment.this.totalPage = pageObj.optInt("totalPage");
                        HomeFragment.this.currentPage = pageObj.optInt("currentPage");
                    }

                    //类别
                    catalogArray = dataJson.optJSONArray("catalogs");
                    if(catalogArray != null && catalogArray.length() > 0){
                        JSONObject cataLog = null;
                        for (int i=0;i < catalogArray.length();i++) {
                            cataLog = catalogArray.getJSONObject(i);
                            cataLog.put("key",cataLog.optString("cname"));
                            cataLog.put("value",cataLog.optString("id"));
                        }
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

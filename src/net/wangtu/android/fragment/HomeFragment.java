package net.wangtu.android.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.RewardDetailActivity;
import net.wangtu.android.common.view.refresh.DefaultRefreshViewEvent;
import net.wangtu.android.common.view.refresh.RefreshView;
import net.wangtu.android.view.HomeFragmentFilterPopupWindow;

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
    private int currentPage;
    private int totalPage;

    private HomeFragmentFilterPopupWindow popupWindow;

    @Override
    public View toCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View childView = inflater.inflate(R.layout.home_fragment_home, container, false);
        listView = (ListView)childView.findViewById(R.id.listView);
        refreshView = (RefreshView)childView.findViewById(R.id.refreshView);

        rewardArray = new JSONArray();
        searchAdapter = new SearchAdapter();
        listView.setAdapter(searchAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),RewardDetailActivity.class);
                startActivity(intent);
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

        filterBtn = (TextView) childView.findViewById(R.id.btn_filter);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow == null){
                    popupWindow = new HomeFragmentFilterPopupWindow(childView,v);
                    JSONArray dataArray = new JSONArray();
                    for (int i = 0; i < 9; i++){
                        JSONObject data = new JSONObject();
                        try {
                            data.put("text","选择_" + i);
                            dataArray.put(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    popupWindow.initData(dataArray);
                    popupWindow.setOnItemCheckListener(new HomeFragmentFilterPopupWindow.OnItemCheckListener() {
                        @Override
                        public void onItemChecked(JSONObject data) throws JSONException {
                            filterBtn.setText(data.getString("text") + "\u0020");
                        }
                    });
                }
                popupWindow.show();
            }
        });

        sortBtn = (TextView) childView.findViewById(R.id.btn_sort);
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
                }else{
                    direct = nav_down;
                }
                sortBtn.setCompoundDrawables(null, null, direct, null);
            }
        });

        getDataFromServer(1, true);
        return childView;
    }

    private void getDataFromServer(int currentPage,boolean isReload){
        for (int i =0;i< 30;i++){
            rewardArray.put(i);
        }

        //refreshView.showSpecialViewResource(R.layout.common_error_view,null);
        refreshView.stopLoading();
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
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.home_fragment_search_item, null);
            } else {
                //itemView = (CourseListViewItemEvent) convertView;
            }

            return convertView;
        }
    }
}

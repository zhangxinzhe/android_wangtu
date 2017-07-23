package net.wangtu.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.base.BaseActivity;
import net.wangtu.android.common.view.dialog.AlertView;
import net.wangtu.android.common.view.dialog.ConfirmView;
import net.wangtu.android.view.MyRewardDetailBiddingItemView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 悬赏竞价中页面
 */

public class MyRewardDetailBiddingActivity extends BaseActivity{
    private ListView designerListView;
    private DesignerListViewAdapter adapter;
    private JSONArray datas;
    private MyRewardDetailBiddingItemView checkedItem;


    @Override
    protected void toCreate(Bundle savedInstanceState) {
        super.toCreate(savedInstanceState);
        setContentView(R.layout.my_reward_detail_bidding);
        initHeader("详情",true);

        designerListView = (ListView)findViewById(R.id.designerListView);
        designerListView.setAdapter(adapter = new DesignerListViewAdapter());
        designerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(checkedItem != null){
                    checkedItem.checked(false);
                }
                checkedItem = (MyRewardDetailBiddingItemView)view;
                checkedItem.checked(true);
            }
        });

        datas = new JSONArray();

        getDataFromServer();
    }

    public void chooseOnClick(View view){
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
        final AlertView alertView = new AlertView(this);
        alertView.setTitleMsg("操作成功！");
        alertView.setContentMsg("您已选择成功，请耐心等待对方确认？");
        alertView.setOkBtnName("确定");
        alertView.setOkBtnListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                alertView.dismiss();
                finish();
            }
        });
        alertView.show();
    }

    public void waitOnClick(View view){
        finish();
    }

    private void getDataFromServer(){
        for (int i =0;i< 10;i++){
            datas.put(i);
        }
        adapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(designerListView);
    }

    private class DesignerListViewAdapter extends BaseAdapter {
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
            if (convertView == null) {
                convertView = View.inflate(MyRewardDetailBiddingActivity.this, R.layout.my_reward_detail_bidding_item, null);
            } else {
                //itemView = (CourseListViewItemEvent) convertView;
            }

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

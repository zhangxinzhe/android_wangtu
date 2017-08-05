package net.wangtu.android.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 评论列表
 */

public class UserInfoCommentView extends LinearLayout implements View.OnClickListener{
    private boolean inited;
    private TextView tabAll;
    private View hasContentView;
    private ImageView hasContentCheck;
    private TextView tabAppease;
    private TextView tabNotAppease;
    private TextView tabPic;
    private ListView listView;
    private JSONArray dataArray;
    private CommentAdapter commentAdapter;
    private String userId;
    private String commentType;
    private String hasContent = "true";

    public UserInfoCommentView(Context context) {
        this(context, null);
    }

    public UserInfoCommentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserInfoCommentView(Context context, AttributeSet attrs, int defStyle) {
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
    }

    private void initUI(){
        hasContentView = findViewById(R.id.has_content);
        hasContentCheck = (ImageView)findViewById(R.id.has_content_check);
        tabAll = (TextView)findViewById(R.id.tab_all);
        tabAppease = (TextView)findViewById(R.id.tab_appease);
        tabNotAppease = (TextView)findViewById(R.id.tab_not_appease);
        tabPic = (TextView)findViewById(R.id.tab_pic);
        listView = (ListView)findViewById(R.id.listView);
        tabCurrent = tabAll;
    }

    private void initEvent(){
        dataArray = new JSONArray();
        commentAdapter = new CommentAdapter();
        listView.setAdapter(commentAdapter);
        tabAll.setOnClickListener(this);
        tabAppease.setOnClickListener(this);
        tabNotAppease.setOnClickListener(this);
        tabPic.setOnClickListener(this);
        hasContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if("true".equals(hasContent)){
                    hasContent = "false";
                    hasContentCheck.setImageResource(R.drawable.icon_user_info_check);
                }else{
                    hasContent = "true";
                    hasContentCheck.setImageResource(R.drawable.icon_user_info_checked);
                }
                getDataFromServer(userId,commentType,hasContent);
            }
        });
    }

    public void getDataFromServer(final String userId,final String commentType,final String hasContent){
        this.userId = userId;
        this.commentType = commentType;
        this.hasContent = hasContent;
        ThreadUtils.schedule(new Runnable() {
            @Override
            public void run() {
                String url = WangTuUtil.getPage(Constants.API_USER_COMMENTS) + "?userId=" + userId + "&commentType=" + commentType + "&hasContent=" + hasContent;
                try {
                    final JSONObject dataJson = WangTuHttpUtil.getJson(url,(Activity)getContext());
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            //按钮
                            tabAll.setText("全部(" + dataJson.optString("contentTypeAll") + ")");
                            tabAll.setTag("all");
                            tabAppease.setText("满意(" + dataJson.optString("contentTypeAppease") + ")");
                            tabAppease.setTag("appease");
                            tabNotAppease.setText("不满意(" + dataJson.optString("contentTypeNotAppease") + ")");
                            tabNotAppease.setTag("notAppease");
                            tabPic.setText("有图(" + dataJson.optString("contentTypeHasPic") + ")");
                            tabPic.setTag("hasPic");

                            //数据
                            dataArray = dataJson.optJSONArray("comments");
                            commentAdapter.notifyDataSetChanged();
                            setListViewHeightBasedOnChildren(listView);
                        }
                    });
                } catch (Exception e) {
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.error(getContext(),"网络访问失败！");
                        }
                    });
                }
            }
        });
    }

    public void refreshData(){
        getDataFromServer(userId,commentType,hasContent);
    }

    private class CommentAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (dataArray == null || dataArray.length() <= 0) {
                return 0;
            }
            return dataArray.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (dataArray == null || dataArray.length() <= 0) {
                return null;
            }
            return dataArray.optJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserInfoCommentItemView itemView = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.user_info_comment_item, null);
                itemView = (UserInfoCommentItemView)convertView;
                itemView.initUI();
            }else{
                itemView = (UserInfoCommentItemView)convertView;
            }
            itemView.initData(dataArray.optJSONObject(position),UserInfoCommentView.this,position);
            return convertView;
        }
    }

    //第一种方法
    public static void setListViewHeightBasedOnChildren(ListView listView){
        if (listView==null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        UserInfoCommentItemView listItem = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            listItem = (UserInfoCommentItemView)listAdapter.getView(i, null, listView);
            totalHeight += listItem.getItemHeight();
            Log.d("getItemHeight:",listItem.getItemHeight() + "");
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + Util.dip2px(listView.getContext(),50);
        listView.setLayoutParams(params);
    }

    private TextView tabCurrent;
    public void onClick(View v){
        if(tabCurrent != null){
            if(v == tabAll || v == tabNotAppease){
                tabCurrent.setBackgroundResource(R.color.purple_v_light);
            }else{
                tabCurrent.setBackgroundResource(R.color.blue_v_light);
            }

            tabCurrent.setTextColor(getResources().getColor(R.color.black));
        }
        tabCurrent= (TextView)v;
        tabCurrent.setBackgroundResource(R.color.blue);
        tabCurrent.setTextColor(getResources().getColor(R.color.white));
        getDataFromServer(userId,tabCurrent.getTag() + "",hasContent);
    }
}

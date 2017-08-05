package net.wangtu.android.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import net.wangtu.android.R;
import net.wangtu.android.common.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 弹出层选择框
 */

public class SelectPopupWindow extends PopupWindow {
    private  Activity activity;
    private ListView listView;
    private JSONArray filerArray;
    private FilterAdapter adapter;
    private int selectedIndex = -1;
    private OnItemCheckListener mOnItemClickListener;
    private View positionView;

    public SelectPopupWindow(View parentView, View positionView){
        super(LayoutInflater.from(parentView.getContext()).inflate(R.layout.common_select_popup_window, null),RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        activity = (Activity)parentView.getContext();
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable(parentView.getResources(), (Bitmap) null));
        update();
        this.positionView = positionView;

        //添加pop窗口关闭事件
        setOnDismissListener(new PoponDismissListener());
    }

    public void show(){
        backgroundAlpha(0.4f);
        showAsDropDown(positionView);
    }

    public void initData(JSONArray datas){
        filerArray = datas;
        listView = (ListView)getContentView().findViewById(R.id.listFilterView);
        listView.setAdapter(adapter = new FilterAdapter());
        listView.post(new Runnable() {
            @Override
            public void run() {
                if(filerArray.length() < 8){
                    setListViewHeightBasedOnChildren(listView);
                }else{
                    listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,Util.dip2px(listView.getContext(),300)));
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedIndex = position;
                adapter.notifyDataSetChanged();
                if(mOnItemClickListener != null){
                    try {
                        mOnItemClickListener.onItemChecked(filerArray.getJSONObject(selectedIndex));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dismiss();
            }
        });
    }

    private class FilterAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (filerArray == null || filerArray.length() <= 0) {
                return 0;
            }
            return filerArray.length();
        }

        @Override
        public JSONObject getItem(int position) {
            if (filerArray == null || filerArray.length() <= 0) {
                return null;
            }
            return filerArray.optJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FilterItemView textView = null;
            if (convertView == null) {
                textView = new FilterItemView(listView.getContext());
                convertView = textView;
            } else {
                textView = (FilterItemView) convertView;
            }

            if(position == selectedIndex){
                textView.setSeleted(true);
            }else{
                textView.setSeleted(false);
            }

            try {
                textView.setText(filerArray.getJSONObject(position).getString("key"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

    private class FilterItemView extends android.support.v7.widget.AppCompatTextView{
        public FilterItemView(Context context){
            super(context);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            setLayoutParams(params);
            setGravity(Gravity.CENTER_VERTICAL);
            int padding = Util.dip2px(listView.getContext(),5);
            setPadding(Util.dip2px(listView.getContext(),10),padding,Util.dip2px(listView.getContext(),20),padding);
            setTextSize(TypedValue.COMPLEX_UNIT_PX,listView.getResources().getDimension(R.dimen.font_size_large));
        }

        public void setSeleted(boolean seleted){
            if(seleted){
                setTextColor(listView.getResources().getColor(R.color.blue));
                Drawable nav_up= listView.getResources().getDrawable(R.drawable.icon_home_fragment_search_filer_check);
                nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                setCompoundDrawables(null, null, nav_up, null);
            }else{
                setTextColor(listView.getResources().getColor(R.color.black));
                setCompoundDrawables(null, null, null, null);
            }
        }
    }

    private class PoponDismissListener implements PopupWindow.OnDismissListener{
        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha){
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
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

    public void setOnItemCheckListener(OnItemCheckListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemCheckListener{
        public void onItemChecked(JSONObject data) throws JSONException;
    }

    public void setValue(String value){
        if(filerArray == null){
            throw new RuntimeException("filerArray is null");
        }
        if(value == null){
            return;
        }
        try {
            for (int i=0;i < filerArray.length();i++){
                if(value.equals(filerArray.getJSONObject(i).optString("value"))){
                    selectedIndex = i;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

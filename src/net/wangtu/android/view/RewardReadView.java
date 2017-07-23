package net.wangtu.android.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.wangtu.android.R;
import net.wangtu.android.activity.common.AlbumActivity;
import net.wangtu.android.common.util.FileUtil;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.view.HorizontalListView;
import net.wangtu.android.common.view.dialog.ActionSheet;
import net.wangtu.android.util.HeaderUtil;
import net.wangtu.android.util.album.AlbumOpt;
import net.wangtu.android.util.album.ImageItem;
import net.wangtu.android.util.album.XImageUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * 悬赏发布
 */

public class RewardReadView extends View{
    private JSONObject dataJson;
    private static final int imageNum = 9;// 图片个数
    private boolean inited;

    private HorizontalListView photoView;
    private RewardReadView.MyAdapter adapter;

    public RewardReadView(Context context) {
        this(context, null);
    }

    public RewardReadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RewardReadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initData(JSONObject dataJson){
        this.dataJson = dataJson;
        final View containerView = (View)getParent();
        photoView = (HorizontalListView) containerView.findViewById(R.id.photoView);
        final TextView titleText = (TextView)containerView.findViewById(R.id.titleText);
        final ImageView titleImage = (ImageView)containerView.findViewById(R.id.titleImage);
        final TextView contentText = (TextView)containerView.findViewById(R.id.contentText);
        final ImageView contentImage = (ImageView)containerView.findViewById(R.id.contentImage);

        photoView.setAdapter(adapter = new RewardReadView.MyAdapter(getContext(),null));
        //title
        containerView.findViewById(R.id.reward_read_title).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "设计一个银行年会主设计一个银行年会主";
                if(titleImage.getVisibility() == View.VISIBLE){
                    titleText.setText(title);
                    titleImage.setVisibility(View.INVISIBLE);
                }else{
                    if(title.length() > 10){
                        title = title.substring(0,10) + "...";
                    }
                    titleText.setText(title);
                    titleImage.setVisibility(View.VISIBLE);
                }
            }
        });
        //detail
        containerView.findViewById(R.id.reward_read_detail).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "设计一个银行年会主设计一个银行年会主设计一个银行年会主设计一个银行年会主设计一个银行年会主设计一个银行年会主设计一个银行年会主设计一个银行年会主";
                if(contentImage.getVisibility() == View.VISIBLE){
                    contentText.setText(title);
                    contentImage.setVisibility(View.INVISIBLE);
                }else{
                    if(title.length() > 10){
                        title = title.substring(0,10) + "...";
                    }
                    contentText.setText(title);
                    contentImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //模拟数据后面需要删除
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(inited){
            return;
        }
        inited = true;

        initData(null);
    }

    private class MyAdapter extends BaseAdapter {
        private Context context;
        private JSONArray jsonArray;

        public MyAdapter(Context context,JSONArray jsonArray) {
            this.context = context;
            this.jsonArray = new JSONArray();
            for (int i = 0; i < 9 ;i++){
                this.jsonArray.put(new JSONObject());
            }
        }

        @Override
        public int getCount() {
            if(jsonArray == null){
                return 0;
            }
            return jsonArray.length();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            RewardReadView.MyAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new RewardReadView.MyAdapter.ViewHolder();
                convertView = new FrameLayout(context);
                convertView.setLayoutParams(new AbsListView.LayoutParams(Util.dip2px(context, 100), Util.dip2px(context, 100)));

                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ((FrameLayout)convertView).addView(imageView);

                holder.image = imageView;
                convertView.setTag(holder);
            } else {
                holder = (RewardReadView.MyAdapter.ViewHolder) convertView.getTag();
            }

            XImageUtil.getInstance().loadImage(holder.image,"http://image1.nphoto.net/news/image/201006/d129390b9b21135f.jpg");
            return convertView;
        }

        private class ViewHolder {
            public ImageView image;
        }
    }
}

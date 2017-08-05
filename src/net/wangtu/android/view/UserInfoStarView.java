package net.wangtu.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.wangtu.android.R;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.view.HorizontalListView;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.album.XImageUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * 用户评价显示星
 */
public class UserInfoStarView extends RelativeLayout {
    private RelativeLayout.LayoutParams layoutParams;
    private LinearLayout starYellow;
    private int starLength = 0;

    public UserInfoStarView(Context context) {
        this(context, null);
    }

    public UserInfoStarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserInfoStarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initUI(){
        starLength = Util.dip2px(getContext(),68);
        starYellow = (LinearLayout)findViewById(R.id.star_yellow);
        layoutParams = (RelativeLayout.LayoutParams)starYellow.getLayoutParams();
    }

    public void initData(double percent){
        if(layoutParams == null){
            initUI();
        }
        layoutParams.width = (int)(starLength * (percent / 5.0));
        starYellow.requestLayout();
    }
}

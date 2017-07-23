package net.wangtu.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.wangtu.android.R;

/**
 * Created by zhangxz on 2017/7/9.
 */

public class MyRewardDetailBiddingItemView extends RelativeLayout{
    private boolean inited;
    private ImageView itemRadio;

    public MyRewardDetailBiddingItemView(Context context) {
        this(context, null);
    }

    public MyRewardDetailBiddingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRewardDetailBiddingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(inited){
            return;
        }
        inited = true;

        init();
    }

    public void init(){
        itemRadio = (ImageView)findViewById(R.id.item_radio);
    }

    public void checked(boolean checked){
        if(checked){
            itemRadio.setImageResource(R.drawable.icon_radio_checked);
        }else{
            itemRadio.setImageResource(R.drawable.icon_radio);
        }
    }
}

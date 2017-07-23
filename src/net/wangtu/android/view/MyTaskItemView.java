package net.wangtu.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.R;

import org.json.JSONObject;

/**
 * 我的任务
 */

public class MyTaskItemView extends RelativeLayout{
    private static  String STATUS_BIDDING = "竞价中";
    private static  String STATUS_TO_PAY = "待付平台使用费";
    private static  String STATUS_TASK_START = "进行中";

    private static  String REWARD_PRICE = "悬赏金额：${price}";

    private JSONObject dataJson;
    private TextView itemTitle;
    private TextView itemRewardRrice;
    private TextView itemStatus;

    public MyTaskItemView(Context context) {
        this(context, null);
    }

    public MyTaskItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTaskItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initUI(){
        itemTitle = (TextView)findViewById(R.id.item_title);
        itemRewardRrice = (TextView)findViewById(R.id.item_reward_price);
        itemStatus = (TextView)findViewById(R.id.item_status);
    }

    public void initData(JSONObject dataJson,int posision){
        itemTitle.setText("主题：设计一个银行年会主题背景设计一个银行...");
        itemRewardRrice.setText(REWARD_PRICE.replace("${price}","150"));
        switch(posision){
            case 0:
                itemStatus.setTextColor(getContext().getResources().getColor(R.color.red));
                itemStatus.setText(STATUS_BIDDING);
                break;
            case 1:
                itemStatus.setTextColor(getContext().getResources().getColor(R.color.red));
                itemStatus.setText(STATUS_TO_PAY);
                break;
            default:
                itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
                itemStatus.setText(STATUS_TASK_START);
                break;

        }

    }
}

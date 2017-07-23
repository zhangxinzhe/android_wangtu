package net.wangtu.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.R;

import org.json.JSONObject;

/**
 * 我的悬赏
 */

public class MyRewardItemView extends RelativeLayout{
    private static  String STATUS_TO_PUBLISH = "等待发布";
    private static  String STATUS_BID_AGAINST = "\u3000\u3000\u3000\u3000\u3000\u3000${price}个新的报价";
    private static  String STATUS_TASK_MAKING_SURE = "\u3000\u3000\u3000\u3000\u3000\u3000确认接单中";
    private static  String STATUS_TASK_MAKE_SURE = "确认接单";
    private static  String STATUS_TASK_START = "开始任务";
    private static  String STATUS_TASK_FINISH = "任务完成";

    private static  String REWARD_PRICE = "悬赏金额：${price}";

    private JSONObject dataJson;
    private TextView itemTitle;
    private TextView itemRewardRrice;
    private TextView itemStatus;

    public MyRewardItemView(Context context) {
        this(context, null);
    }

    public MyRewardItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRewardItemView(Context context, AttributeSet attrs, int defStyle) {
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
                itemStatus.setText(STATUS_TO_PUBLISH);
                break;
            case 1:
                itemStatus.setTextColor(getContext().getResources().getColor(R.color.red));
                itemStatus.setText(STATUS_BID_AGAINST.replace("${price}","11"));
                break;
            case 2:
                itemStatus.setTextColor(getContext().getResources().getColor(R.color.red));
                itemStatus.setText(STATUS_TASK_MAKING_SURE);
                break;
            case 3:
                itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
                itemStatus.setText(STATUS_TASK_MAKE_SURE);
                break;
            case 4:
                itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
                itemStatus.setText(STATUS_TASK_START);
                break;
            default:
                itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
                itemStatus.setText(STATUS_TASK_FINISH);
                break;

        }

    }
}

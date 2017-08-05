package net.wangtu.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.util.WangTuUtil;

import org.json.JSONObject;

/**
 * 我的悬赏
 */

public class MyRewardItemView extends RelativeLayout{
    private static  String STATUS_TO_PUBLISH = "等待发布";
    private static  String STATUS_BID_AGAINST = "\u3000\u3000\u3000\u3000\u3000\u3000${price}个新的报价";
    private static  String STATUS_TASK_MAKING_SURE = "\u3000\u3000\u3000\u3000\u3000\u3000确认接单中";
    private static  String STATUS_TASK_MAKE_SURE = "已接单";
    private static  String STATUS_TASK_START = "开始任务";
    private static  String STATUS_TASK_FINISH = "任务完成";
    private static  String STATUS_TASK_CANCEL = "撤销";

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
        itemTitle.setText("主题：" + dataJson.optString("title"));
        itemRewardRrice.setText(REWARD_PRICE.replace("${price}",dataJson.optString("price")));
        String rewardStatus = dataJson.optString("status");
        if(Constants.REWARD_STATUS_CREATE.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.red));
            itemStatus.setText(STATUS_TO_PUBLISH);
        }else if(Constants.REWARD_STATUS_PUBLISH.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.red));
            itemStatus.setText(STATUS_BID_AGAINST.replace("${price}",dataJson.optString("biddingNum")));
        }else if(Constants.REWARD_STATUS_DOING.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
            itemStatus.setText(STATUS_TASK_MAKE_SURE);
        }else if(Constants.REWARD_STATUS_FINISH.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
            itemStatus.setText(STATUS_TASK_FINISH);
        }else{
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
            itemStatus.setText(STATUS_TASK_CANCEL);
        }
    }
}

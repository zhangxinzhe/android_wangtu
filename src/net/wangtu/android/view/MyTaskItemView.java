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
 * 我的任务
 */

public class MyTaskItemView extends RelativeLayout{
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
        itemTitle.setText("主题：" + dataJson.optString("title"));
        itemRewardRrice.setText(REWARD_PRICE.replace("${price}",dataJson.optString("price")));
        String rewardStatus = dataJson.optString("biddingStatus");


        // 未支付竞价
        if(Constants.Bidding_STATUS_UNPAY.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.red));
            itemStatus.setText("待付平台使用费");
        // 已支付，竞价中
        }else if(Constants.Bidding_STATUS_PAY.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.red));
            itemStatus.setText("竞价中");
        // 竞价成功
        }else if(Constants.Bidding_STATUS_SUCCESS.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.red));
            itemStatus.setText("竞价成功，开始任务");
        // 任务完成
        }else if(Constants.Bidding_STATUS_FINISH.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
            itemStatus.setText("任务完成");
        // 竞价失败
        }else if(Constants.Bidding_STATUS_FAIL.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
            itemStatus.setText("竞价失败");
        // 任务撤销
        }else if(Constants.Bidding_USER_CANCEL.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
            itemStatus.setText("放弃竞价");
        // 发布撤销
        }else if(Constants.Bidding_USER_CANCEL.equals(rewardStatus)){
            itemStatus.setTextColor(getContext().getResources().getColor(R.color.green));
            itemStatus.setText("发布撤销");
        }
    }
}

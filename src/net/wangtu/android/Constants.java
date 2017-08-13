package net.wangtu.android;

/**
 * Created by zhangxz on 2017/7/18.
 */

public class Constants {
    public static final String APP_IDENTIFICATION = "wangtu";// 标识符
    public static  final String APP_ID = "wxb4ba3c02aa476ea1";
    public static  final String CHAR_SPACE = "&#160;";
    public static final String GE_TUI_CLIENT_ID = "ge_tui_client_id";//个推id

    public static final String API_LOGIN = "/appUser/userLogin.htm";//登录
    public static final String API_LOGOUT = "/appUser/logout.htm";//退出
    public static final String API_REGISTER = "/appUser/userRegister.htm";//注册页
    public static final String API_GET_USER_INFO = "/appUser/getUserInfo.htm";//个人信息
    public static final String API_CHANGE_AVATAR = "/appUser/changeAvatar.htm";//个人信息
    public static final String API_UPDATE_USER_INFO = "/appUser/updateUserInfo.htm";//更新个人信息
    public static final String API_USER_COMMENTS = "/appUser/getUserComments.htm";//评论信息
    public static final String API_REPLY_COMMENT = "/appUser/replyComment.htm";//回复评论

    public static final String API_ACCOUNT = "/appUser/account.htm";//用户余额
    public static final String API_ACCOUNT_APPLY = "/appUser/accountApply.htm";//用户提现

    public static final String API_MOBILE_CONFIG = "/appSms/mobileConfig.htm";//配置信息
    public static final String API_PUSH_MSG_LIST = "/appSms/pushMsgList.htm";//消息列表

    public static final String API_LIST_REWARD = "/appReward/listReward.htm";//悬赏列表
    public static final String API_REWARD_DETAIL = "/appReward/rewardDetail.htm";//悬赏详情
    public static final String API_LIST_CATALOG = "/appReward/listCatalogs.htm";//悬赏分类

    //悬赏者publishReward
    public static final String API_MY_REWARD = "/appReward/myReward.htm";//我的悬赏
    public static final String API_ADD_REWARD = "/appReward/addReward.htm";//添加悬赏
    public static final String API_UPDATE_REWARD = "/appReward/updateReward.htm";//添加悬赏
    public static final String API_PUBLISH_REWARD = "/appReward/publishReward.htm";//发布悬赏
    public static final String API_DELETE_REWARD = "/appReward/deleteReward.htm";//删除悬赏
    public static final String API_CHOOSE_BIDDING = "/appReward/chooseBidding.htm";//选择接单人
    public static final String API_REWARD_FINISH = "/appReward/rewardFinish.htm";//任务完成
    public static final String API_GET_COMMENT_USER_INFO = "/appUser/getCommentUserInfo.htm";//获取评论用户信息
    public static final String API_ADD_COMMENT = "/appUser/addComment.htm";//添加评论



    //接单者
    public static final String API_MY_TASK = "/appReward/myRewardBidding.htm";//我的任务
    public static final String API_BIDDING_DETAIL = "/appReward/biddingDetail.htm";//竞价详情
    public static final String API_ADD_REWARD_BIDDING = "/appReward/addRewardBidding.htm";//创建竞价
    public static final String API_CREATE_BIDDING_ORDER = "/appReward/createBiddingOrder.htm";//支付平台使用费
    public static final String API_FINISH_BIDDING_ORDER = "/appReward/finishBiddingOrder.htm";//支付平台使用费结果
    public static final String API_CANCEL_BIDDING = "/appReward/cancelBidding.htm";//取消竞价


    //悬赏状态
    public static final String REWARD_STATUS_CREATE = "CREATE";//创建
    public static final String REWARD_STATUS_PUBLISH = "PUBLISH";//发布
    public static final String REWARD_STATUS_DOING = "DOING";//已接单
    public static final String REWARD_STATUS_FINISH = "FINISH";//完成
    public static final String REWARD_STATUS_CANCEL = "CANCEL";//撤销

    //竞标状态
    public static final String Bidding_STATUS_UNPAY = "UNPAY";//未支付
    public static final String Bidding_STATUS_PAY = "PAY";    //已支付
    public static final String Bidding_STATUS_SUCCESS = "SUCCESS";//竞价成功
    public static final String Bidding_STATUS_FINISH = "FINISH";//任务成功
    public static final String Bidding_STATUS_FAIL = "FAIL";//竞价失败
    public static final String Bidding_USER_CANCEL = "USER_CANCEL";//竞价失败
    public static final String Bidding_PUBLISHER_CANCEL = "PUBLISHER_CANCEL";//竞价失败

}

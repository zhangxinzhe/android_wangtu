package net.wangtu.android.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.wangtu.android.Constants;
import net.wangtu.android.R;
import net.wangtu.android.common.util.ImageCacheUtil;
import net.wangtu.android.common.util.ThreadUtils;
import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.util.ValidateUtil;
import net.wangtu.android.common.view.HorizontalListView;
import net.wangtu.android.util.ToastUtil;
import net.wangtu.android.util.WangTuHttpUtil;
import net.wangtu.android.util.WangTuUtil;
import net.wangtu.android.util.album.XImageUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 评论回复
 */
public class UserInfoCommentReplyView extends PopupWindow {
    private  Activity activity;
    private String commentId;
    private Runnable runnable;

    public UserInfoCommentReplyView(View parentView){
        super(LayoutInflater.from(parentView.getContext()).inflate(R.layout.user_info_comment_reply, null), (Util.getWindowWidth(parentView.getContext()) - Util.dip2px(parentView.getContext(),20)), RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        activity = (Activity)parentView.getContext();
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable(parentView.getResources(), (Bitmap) null));
        update();

        //添加pop窗口关闭事件
        setOnDismissListener(new PoponDismissListener());
    }

    public void initData(final String commentId){
        this.commentId = commentId;
        final TextView textReply =  (TextView)getContentView().findViewById(R.id.txt_reply);
        final Button btnReply =  (Button)getContentView().findViewById(R.id.btn_reply);
        getContentView().findViewById(R.id.btn_reply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String reply = textReply.getText().toString();
                if(ValidateUtil.isBlank(reply)){
                    ToastUtil.error(activity,"请填写回复内容！");
                    return;
                }
                ToastUtil.startLoading(activity);
                ThreadUtils.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String url = WangTuUtil.getPage(Constants.API_REPLY_COMMENT);
                            url += "?commentId=" + commentId;
                            url += "&replyContent=" + URLEncoder.encode(reply,"utf-8");
                            final JSONObject dataJson = WangTuHttpUtil.getJson(url,activity);
                            if(dataJson != null){
                                btnReply.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.stopLoading(activity);
                                        if("success".equals(dataJson.optString("msg"))){
                                            if(runnable != null){
                                                runnable.run();
                                            }
                                            dismiss();
                                        }else{
                                            ToastUtil.error(activity,dataJson.optString("msg"));
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            btnReply.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.stopLoading(activity);
                                    ToastUtil.error(activity,"提交失败");
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private class PoponDismissListener implements PopupWindow.OnDismissListener{
        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }
    }

    public void show(){
        backgroundAlpha(0.4f);
        showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
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

    public void setCloseListner(Runnable runnable){
        this.runnable = runnable;
    }
}

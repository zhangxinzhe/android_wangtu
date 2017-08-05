package net.wangtu.android.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import net.wangtu.android.activity.HomeActivity;
import net.wangtu.android.activity.LiquidatedDamagesPayActivity;
import net.wangtu.android.activity.LoginActivity;
import net.wangtu.android.activity.MyTaskActivity;
import net.wangtu.android.common.view.dialog.AlertView;
import net.wangtu.android.common.view.dialog.BoxView;
import net.wangtu.android.common.view.dialog.ConfirmView;

/**
 * Created by zhangxz on 2017/7/29.
 */

public class ToastUtil {
    /**
     * alert
     * @param context
     * @param msg
     * @param okBtnListener
     */
    public static void alert(Context context, String msg,final DialogOnClickListener okBtnListener){
        final AlertView alertView = new AlertView(context);
        alertView.setTitleMsg("提示");
        alertView.setContentMsg(msg);
        alertView.setOkBtnName("确定");
        alertView.setOkBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okBtnListener.onClick(alertView);
            }
        });
        alertView.show();
    }

    /**
     * confirm
     * @param context
     * @param msg
     * @param okBtnListener
     * @param cancelBtnListener
     */
    public static void confirm(Context context, String msg,final DialogOnClickListener okBtnListener,final DialogOnClickListener cancelBtnListener){
        confirm(context,"提示！",msg,"确定","取消",okBtnListener,cancelBtnListener);
    }

    public static void confirm(Context context,String title, String msg,String okBtn,String cancelBtn,final DialogOnClickListener okBtnListener,final DialogOnClickListener cancelBtnListener){
        final ConfirmView confirmView = new ConfirmView(context);
        confirmView.setTitleMsg(title);
        confirmView.setContentMsg(msg);
        confirmView.setOkBtnName(okBtn);
        confirmView.setOkBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okBtnListener.onClick(confirmView);
            }
        });
        confirmView.setCancelBtnName(cancelBtn);
        confirmView.setCancelBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancelBtnListener == null){
                    confirmView.dismiss();
                }else{
                    cancelBtnListener.onClick(confirmView);
                }
            }
        });
        confirmView.show();
    }

    /**
     * 错误提示
     * @param context
     * @param msg
     */
    public static void error(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static  void startLoading(Context context){
        ((LoadingInterface)context).startLoading();
    }

    public static  void stopLoading(Context context){
        ((LoadingInterface)context).stopLoading();
    }

    /**
     * confirm
     * @param context
     */
    public static void login(final Context context){
        confirm(context, "提示！", "您还未登录，请先登录", "登录", "取消", new DialogOnClickListener() {
            @Override
            public void onClick(BoxView dialog) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        }, null);
    }

    public interface  LoadingInterface{
        public  void startLoading();
        public  void stopLoading();
    }

    public interface  DialogOnClickListener{
        public void onClick(BoxView dialog);
    }
}

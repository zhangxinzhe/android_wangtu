package net.wangtu.android.common.view.dialog;

import net.wangtu.android.R;
import net.wangtu.android.common.util.ValidateUtil;


import android.os.Bundle;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlertView extends BoxView{

	private Button okBtn;
	private View.OnClickListener okBtnListener;
	
	private TextView  msgView;
	private TextView  titleView;
	private String contentMsg;
	private String titleMsg;
	private String okBtnName;

	public AlertView(Context context) {
		super(context);
	}
	
	public static AlertView makeText(Context context,String title,String content,String okBtnName,View.OnClickListener okBtnListener){
		AlertView alert = new AlertView(context);
		alert.setTitleMsg(title);
		alert.setContentMsg(content);
		alert.setOkBtnListener(okBtnListener);
		alert.setOkBtnName(okBtnName);
		return alert;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.common_alert_view);
		
		okBtn = (Button)findViewById(R.id.dialog_button_ok);
		okBtn.setOnClickListener(okBtnListener);
		if(okBtnName != null){
			okBtn.setText(okBtnName);
		}

		msgView = (TextView)findViewById(R.id.msgView);
		msgView.setText(contentMsg);
		
		titleView =  (TextView)findViewById(R.id.titleView);
		if(ValidateUtil.isBlank(titleMsg)){
			titleView.setVisibility(View.GONE);
		}else{
			titleView.setText(titleMsg);
		}
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event){
		return false;
	}
	
	/**
	 * 设定标题
	 * @param title
	 */
	public void setTitleMsg(String titleMsg){
		if(titleMsg == null){
			titleView.setVisibility(View.GONE);
		}else{
			if(msgView != null){
				titleView.setText(titleMsg);
			}else{
				this.titleMsg = titleMsg;
			}
		}
	}
	
	/**
	 * 设定正文
	 * @param msg
	 */
	public void setContentMsg(String msg) {
		if(msgView != null){
			msgView.setText(msg);
		}else{
			this.contentMsg = msg;
		}
	}

	public void setOkBtnListener(View.OnClickListener okBtnListener) {
		this.okBtnListener = okBtnListener;
	}
	
	public void setOkBtnName(String name){
		if(okBtn == null){
			okBtnName = name;
		}else{
			okBtn.setText(name);
		}
	}
	
	public Button getOkBtn() {
		return okBtn;
	}
	
	@Override
	public void post(Runnable action) {
		if(msgView != null){
			msgView.post(action);
		}
	}
}

package net.wangtu.android.common.view.dialog;

import net.wangtu.android.R;
import net.wangtu.android.common.util.ValidateUtil;


import android.os.Bundle;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmView extends BoxView{

	private Button okBtn,cancelBtn;
	private View.OnClickListener okBtnListener;
	private View.OnClickListener cancelBtnListener;
	
	private TextView  msgView;
	private TextView  titleView;
	private String contentMsg;
	private String titleMsg;
	private String okBtnName;
	private String cancelBtnName;
	private boolean canCancel = true;

	public ConfirmView(Context context) {
		super(context);
	}
	
	public static ConfirmView makeText(Context context,String title,String content,String okBtnName,View.OnClickListener okBtnListener){
		ConfirmView alert = new ConfirmView(context);
		alert.setTitleMsg(title);
		alert.setContentMsg(content);
		alert.setOkBtnListener(okBtnListener);
		alert.setOkBtnName(okBtnName);
		return alert;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.common_confirm_view);
		
		okBtn = (Button)findViewById(R.id.dialog_button_ok);
		okBtn.setOnClickListener(okBtnListener);
		if(okBtnName != null){
			okBtn.setText(okBtnName);
		}

		cancelBtn = (Button)findViewById(R.id.dialog_button_cancel);
		canCancel(canCancel);
		if(cancelBtnListener == null){
			cancelBtnListener = new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					if(canCancel){
						ConfirmView.this.cancel();
					}
				}
			};
		}
		cancelBtn.setOnClickListener(cancelBtnListener);
		if(cancelBtnName != null){
			cancelBtn.setText(cancelBtnName);
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
		if(KeyEvent.KEYCODE_BACK == keyCode && !canCancel){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 设定标题
	 * @param titleMsg
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
	
	public void setCancelBtnListener(View.OnClickListener cancelBtnListener) {
		this.cancelBtnListener = cancelBtnListener;
	}

	public void setOkBtnName(String name){
		if(okBtn == null){
			okBtnName = name;
		}else{
			okBtn.setText(name);
		}
	}
	
	public void setCancelBtnName(String name){
		if(cancelBtn == null){
			cancelBtnName = name;
		}else{
			cancelBtn.setText(name);
		}
	}

	public Button getOkBtn() {
		return okBtn;
	}
	
	/**
	 * 是否允许取消
	 * @param value
	 */
	public void canCancel(boolean value){
		canCancel = value;
		if(cancelBtn != null && !canCancel){
			cancelBtn.setTextColor(0xffaaaaaa);
			cancelBtn.setClickable(false);
			cancelBtn.setBackgroundResource(R.drawable.common_style_confirm_bottom_button_disable);
		}
	}

	@Override
	public void post(Runnable action) {
		if(msgView != null){
			msgView.post(action);
		}
	}
}

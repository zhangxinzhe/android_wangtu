package net.wangtu.android.common.view.dialog;

import net.wangtu.android.R;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public abstract class BoxView extends Dialog {

	public BoxView(Context context) {
		super(context, R.style.common_dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setCanceledOnTouchOutside(false);
		Window win = getWindow();
		LayoutParams params = new LayoutParams();
		params.x = 0;// 设置x坐标
		params.y = -50;// 设置y坐标
		params.height = -2;
		params.width = -2;
		win.setAttributes(params);
	}

	/**
	 * 提交主线程任务
	 * @param action
	 */
	public abstract void post(Runnable action);
}
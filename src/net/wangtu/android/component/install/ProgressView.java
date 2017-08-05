package net.wangtu.android.component.install;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ProgressBar;

import net.wangtu.android.R;
import net.wangtu.android.common.view.dialog.BoxView;


/**
 * 现在进度显示
 * @author zhangxz
 *
 */
public class ProgressView extends BoxView {
	private ProgressBar progressBar;

	public ProgressView(Context context) {
		super(context);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.apk_progress_view);
		progressBar = (ProgressBar)findViewById(R.id.progress);
	}
	
	/**
	 * 更新进度
	 * @param progress
	 */
	public void updateProgress(int progress){
		progressBar.setProgress(progress);
		progressBar.setSecondaryProgress(progress);
	}

	@Override
	public void post(Runnable action) {
		
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(KeyEvent.KEYCODE_BACK == keyCode){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}

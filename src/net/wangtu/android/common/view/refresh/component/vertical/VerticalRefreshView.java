package net.wangtu.android.common.view.refresh.component.vertical;

import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.view.refresh.component.rotate.ArrowView;
import net.wangtu.android.common.view.refresh.component.rotate.RotateView;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 垂直刷新的状态栏
 * @author zhangxz
 *
 */
@SuppressLint("ViewConstructor")
public class VerticalRefreshView extends LinearLayout {
	//文本
	private static final  String DRAG_DOWN_TO_REFRESH_TEXT = "下拉可以刷新....";//下拉可以刷新
	private static final String GIVE_OUT_TO_REFRESH_TEXT = "松开即可刷新....";//放开刷新
	private static final  String REFRESHING_TEXT = "载入中....";//下拉可以刷新
	private static final int TEXT_COLOR = 0xff666666;
	
	//分界线
	private int rotateRefreshLine;//状态切换分界线
	private int rotateBeginLine;//旋转开始分界线
	private int rotateEndLine;//旋转结束分界线
	private int rotateHeightRange;//旋转范围
	
	//视图
	private RotateView loadingView;
	private ArrowView arrowView;
	private TextView textView;
	
    public VerticalRefreshView(Context context,boolean loading) {
    	super(context);
    	
    	//初始化数据
    	int size = Util.dip2px(getContext(), 30);
    	initData(size);
    	
    	//文字view
    	textView = new TextView(getContext());
    	LayoutParams layoutParams =new LayoutParams(size,LayoutParams.WRAP_CONTENT,6.0f);
		textView.setLayoutParams(layoutParams);
		textView.setBackgroundResource(android.R.color.transparent);
    	
		//等待view
		if(loading){
			int loadingViewSize = size-5;
			loadingView = new RotateView(getContext(),loadingViewSize,loadingViewSize);
			layoutParams =new LayoutParams(loadingViewSize,loadingViewSize,1.0f);
			layoutParams.setMargins(5, 5, size + 5, 5);
			loadingView.setLayoutParams(layoutParams);
			textView.setText(REFRESHING_TEXT);
			addView(loadingView);
		}
		//下拉view
		else{
			arrowView = new ArrowView(getContext(),size,size);
			layoutParams =new LayoutParams(size,size,1.0f);
			layoutParams.setMargins(0, 0, size, 0);
			arrowView.setLayoutParams(layoutParams);
			textView.setText(DRAG_DOWN_TO_REFRESH_TEXT);
			addView(arrowView);
		}
		textView.setTextColor(TEXT_COLOR);
	    addView(textView);
		 
	    this.setBackgroundResource(android.R.color.transparent);
		this.setOrientation(LinearLayout.HORIZONTAL);
    }
    
    /**
     * 初始化基本数据
     * @param size
     */
    private void initData(int size){
    	 //数据初始化
		 rotateBeginLine = - size;//旋转开始分界线
		 rotateEndLine  = Util.dip2px(getContext(), 50);//旋转结束分界线
		 rotateHeightRange = rotateEndLine - rotateBeginLine;//旋转范围
		 rotateRefreshLine = Util.dip2px(getContext(), 10);//状态切换分界线
    }
	
	/**
	 * 旋转
	 * @param top
	 */
	@SuppressLint("NewApi")
	public void rotate(int top){		
		//下拉图标旋转
		if(top >= rotateBeginLine){
			float r = 180 * (top - rotateBeginLine)/rotateHeightRange;
			if(top > rotateEndLine){
				r = 180;
			}
			if (Build.VERSION.SDK_INT >= 11) {
				arrowView.setRotation(-r);
				arrowView.invalidate();
			}
		}
	}

	/**
	 * 显示文字
	 * @param top
	 */
	public void text(int top){
		if(top > rotateRefreshLine){
			if(!GIVE_OUT_TO_REFRESH_TEXT.equals(textView.getText())){
				textView.setText(GIVE_OUT_TO_REFRESH_TEXT);
			}
		}else {
			if(!DRAG_DOWN_TO_REFRESH_TEXT.equals(textView.getText())){
				textView.setText(DRAG_DOWN_TO_REFRESH_TEXT);
			}
		}
	}
	
	/**
	 * 显示和隐藏
	 * @param show
	 */
	public void show(boolean show){
		if(show){
			setVisibility(VISIBLE);
			if(loadingView != null){
				loadingView.start();
			}
		}else{
			if(loadingView != null){
				loadingView.stop();
			}
			setVisibility(GONE);
		}
	}

	public int getRotateRefreshLine() {
		return rotateRefreshLine;
	}
}

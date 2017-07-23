package net.wangtu.android.common.view.refresh.component.vertical;

import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.view.refresh.RefreshView;
import net.wangtu.android.common.view.refresh.component.rotate.LoadView;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 垂直加载更多状态栏
 * 
 * @author kuanghf
 */
public class VerticalLoadView extends RelativeLayout implements android.view.View.OnClickListener {
	// 文本
	public static final String TEXT_LOADING = "努力加载...";// 上拉加载更多
	public static final String TEXT_LOADED = "--已经到底啦--";// 已无更多
	public static final String TEXT_LOADFAIL = "加载失败";// 加载失败
	private static final int TEXT_COLOR = 0xff999999;
	private static final int TEXT_ERROR_COLOR = 0xff5368c5;
	public static final int VERTICAL_LOAD_VIEW_HEIGHT = 30;
	public static final int VERTICAL_LOAD_VIEW_PADDING_BOTTOM = 90;
	public final int paddingBottom;
	public final int height;
	private boolean hasLoaded;

	// 视图
	private TextView textView;
	private LoadView loadingView;
	private RefreshView refreshView;
	

	public VerticalLoadView(Context context, RefreshView refreshView) {
		super(context);
		height = Util.dip2px(getContext(), VERTICAL_LOAD_VIEW_HEIGHT);
		paddingBottom = Util.dip2px(getContext(), VERTICAL_LOAD_VIEW_PADDING_BOTTOM);
		this.refreshView = refreshView;
		
		android.widget.AbsListView.LayoutParams lp = new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,height);
	    setLayoutParams(lp);		
		
		LinearLayout containerView = new LinearLayout(getContext());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		containerView.setLayoutParams(layoutParams);
		containerView.setPadding(0, 0, 0, 0);
		//containerView.setBackgroundResource(android.R.color.black);
		addView(containerView);
		containerView.setOrientation(LinearLayout.HORIZONTAL);
		
		// 添加图片
		int loadingViewSize = Util.dip2px(getContext(), 15);
		loadingView = new LoadView(getContext(), loadingViewSize, loadingViewSize);
		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(loadingViewSize, loadingViewSize);
		layoutParams2.gravity = Gravity.CENTER_VERTICAL;
		loadingView.setLayoutParams(layoutParams2);
		loadingView.setPadding(0, 0, 0, 0);
		containerView.addView(loadingView);
		//loadingView.start();

		// 添加文字
		textView = new TextView(getContext());
		layoutParams2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams2.setMargins(5, 0, 0, 0);
		textView.setLayoutParams(layoutParams2);
		textView.setTextColor(TEXT_COLOR);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		textView.setText(TEXT_LOADING);
		textView.setOnClickListener(this);
		containerView.addView(textView);
		
		//默认不显示
		setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onClick(View v) {
		startLoad();
		if(refreshView.getRefreshViewEvent() != null){
			if(textView.getText().equals(TEXT_LOADFAIL)){
				refreshView.getRefreshViewEvent().startVerticalLoad();
			}
		}
	}
	
	/**
	 * 加载中
	 */
	public void startLoad(){
		super.setVisibility(VISIBLE);
		loadingView.start();
		textView.setText(TEXT_LOADING);
		textView.setTextColor(TEXT_COLOR);
	}
	
	/**
	 * 全部加载完
	 */
	public void stopLoad(boolean hide){
		if(hide){
			super.setVisibility(INVISIBLE);
		}else{
			super.setVisibility(VISIBLE);
		}
		loadingView.stop();
		textView.setText(TEXT_LOADED);
		textView.setTextColor(TEXT_COLOR);
	}
	
	/**
	 * 加载失败
	 */
	public void failLoad(){
		super.setVisibility(VISIBLE);
		loadingView.stop();
		textView.setText(TEXT_LOADFAIL);
		textView.setTextColor(TEXT_ERROR_COLOR);
	}
	
	/**
	 * 显示加载
	 */
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		startRotate(visibility == VISIBLE);
	}

	/**
	 * 开启/关闭动画
	 * 
	 * @param start
	 */
	public void startRotate(boolean start) {
		if (loadingView == null) {
			return;
		}

		if (start) {
			loadingView.start();
		} else {
			loadingView.stop();
		}
	}
	
	public void touch(int status,VertivalContentView contentView) {
		if(status == MotionEvent.ACTION_DOWN){
			hasLoaded = false;
		}
		
		if(!hasLoaded && contentView != null && contentView.hasScrolledBottom()){
			hasLoaded = true;
			refreshView.getRefreshViewEvent().startVerticalLoad();
		}
		
//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
//			return;
//		}
//		
//		if(touchStatus == MotionEvent.ACTION_DOWN){
//			android.widget.AbsListView.LayoutParams lp = new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,height + paddingBottom);
//		    setLayoutParams(lp);
//		    postInvalidate();
//		}else if(touchStatus == MotionEvent.ACTION_UP){
//		    ValueAnimator va = ValueAnimator.ofInt(height + paddingBottom, height);
//	        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
//	        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//	            @Override
//	            public void onAnimationUpdate(ValueAnimator animation) {
//	                int h = (Integer) animation.getAnimatedValue();
//	                layoutParams.height = h;
//	                setLayoutParams(layoutParams);
//	                requestLayout();
//	            }
//	        });
//	        va.setDuration(100);
//	        va.start();
//		}
	}
}

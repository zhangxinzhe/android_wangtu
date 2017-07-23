package net.wangtu.android.common.view.refresh.component;

import java.util.Date;

import net.wangtu.android.common.view.refresh.RefreshView;
import net.wangtu.android.common.view.refresh.RefreshViewEvent;
import net.wangtu.android.common.view.refresh.component.vertical.VerticalLoadView;
import net.wangtu.android.common.view.refresh.component.vertical.VerticalRefreshView;
import net.wangtu.android.common.view.refresh.component.vertical.VertivalContentView;
import net.wangtu.android.common.view.refresh.component.vertical.adapter.ContentAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 垂直刷新
 * @author zhangxz
 *
 */
@SuppressLint("ViewConstructor")
public class VerticalView extends LinearLayout {
	public static final int CONTENT_VIEW_TAG = 1001111;
	public static final int VERTICAL_START_REFRESH = 1;
	public static final int VERTICAL_STOP_REFRESH = 2;
	public static final int VERTICAL_STOP_LOAD = 3;
	public static final int VERTICAL_STATUS_VIEW_MARGIN = 20;
	
	private RefreshView refreshView;
	private VertivalContentView contentView;
	private VerticalRefreshView pullBar;//下拉状态栏
	private VerticalRefreshView loadingBar;//加载状态栏
	private VerticalLoadView verticalLoadView;//加载更多状态栏
	private int refreshViewHeight;
	private boolean hasAddVerticalLoadView = false;
	private long currentRequestId = -1;
	
    public VerticalView(Context context,RefreshView refreshView) {
    	super(context);
    	this.refreshView = refreshView;
    	
		//下拉的bar
        pullBar = new VerticalRefreshView(getContext(),false);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, VERTICAL_STATUS_VIEW_MARGIN, 0, VERTICAL_STATUS_VIEW_MARGIN);
        pullBar.setLayoutParams(layoutParams);
        super.addView(pullBar);
        
        //加载中的bar
        loadingBar = new VerticalRefreshView(getContext(),true);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, VERTICAL_STATUS_VIEW_MARGIN, 0, VERTICAL_STATUS_VIEW_MARGIN);
        loadingBar.setLayoutParams(layoutParams);
        loadingBar.show(false);
        super.addView(loadingBar);
        
        //内容View
        contentView = new VertivalContentView(getContext(),this);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0);
        contentView.setLayoutParams(layoutParams);
        super.addView(contentView);
        
        this.setBackgroundResource(android.R.color.transparent);
		this.setOrientation(LinearLayout.VERTICAL);
		
		
    }
    
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
    	contentView.addView(child, params);
    	
    	RefreshViewEvent refreshViewEvent = refreshView.getRefreshViewEvent();
    	if(refreshViewEvent != null && refreshViewEvent.showVerticalLoad()){
			addVerticalLoadView();
    	}
    }
	
    /**
     * 添加滚动加载事件
     * @param listView
     */
	public void addVerticalLoadView(){
		ContentAdapter contentAdapter = contentView.getContentAdapter();
		if(contentAdapter == null || hasAddVerticalLoadView){
			return;
		}
		hasAddVerticalLoadView = true;
		//加载更多的bar
	    verticalLoadView = new VerticalLoadView(getContext(), refreshView);
	    verticalLoadView.setBackgroundResource(android.R.color.transparent);
	    contentAdapter.addVerticalLoadView(verticalLoadView);
	}
	
    /**
     * 纠正内容View的高度
     * @param height
     */
    public void setContentViewHeight(int height){
    	LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,height);
        layoutParams.setMargins(0, 0, 0, 0);
        contentView.setLayoutParams(layoutParams);
    }
	/**
	 * 触摸
	 * @param status
	 */
	@SuppressLint("NewApi")
	public int touch(int status){
		if(!refreshView.getRefreshViewEvent().showVerticalRefresh()){
			return -1;
		}
		
		switch (status) {
			case MotionEvent.ACTION_DOWN: {
				if(verticalLoadView != null){
					verticalLoadView.touch(MotionEvent.ACTION_DOWN,contentView);
				}
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				//不是初始坐标
				if(getTop() > -getRefreshViewHeight()){
					//不允许主动放弃加载
					if(!refreshView.getRefreshViewEvent().canForgiveVerticalRefresh() && loadingBar.getVisibility() == VISIBLE){
						break;
					}
					
					//改为拖动中
					loadingBar.show(false);
					pullBar.show(true);
					pullBar.text(getTop());
				}
				
				if(verticalLoadView != null){
					verticalLoadView.touch(MotionEvent.ACTION_MOVE,contentView);
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				if(verticalLoadView != null){
					verticalLoadView.touch(MotionEvent.ACTION_UP,contentView);
				}
				
				//不许放弃加载
				if(!refreshView.getRefreshViewEvent().canForgiveVerticalRefresh() && loadingBar.getVisibility() == VISIBLE){
					refreshView.getDragHelper().smoothSlideViewTo(this, 0, 0);
					ViewCompat.postInvalidateOnAnimation(refreshView);
					break;
				}
				
				//开始加载
				if(getTop() > pullBar.getRotateRefreshLine()){
					refreshView.getDragHelper().smoothSlideViewTo(this, 0, 0);
					ViewCompat.postInvalidateOnAnimation(refreshView);
					//显示加载
					loadingBar.show(true);
					pullBar.show(false);
					
					//一分钟还未加载出来，自动关闭动画
					final long requestId = currentRequestId = new Date().getTime();
					postDelayed(new Runnable() {
						@Override
						public void run() {
							if(currentRequestId == requestId){
								stopLoading();
							}
						}
					}, 120000);
					return VERTICAL_START_REFRESH;
				//放弃加载
				}else if(getTop() > -getRefreshViewHeight()){
					if(loadingBar.getVisibility() == VISIBLE && getTop() == 0){
						return -1;
					}
					refreshView.getDragHelper().smoothSlideViewTo(this, 0, -getRefreshViewHeight());
					ViewCompat.postInvalidateOnAnimation(refreshView);
					loadingBar.show(false);
					pullBar.show(true);
					return VERTICAL_STOP_REFRESH;
				}//else {
//					if(getContentView().hasScrolledBottom()){
//						if(contentView.getListView() != null){
//					    	contentView.getListView().smoothScrollBy(-verticalLoadViewPaddingBottom, 500);
//					    	ViewCompat.postInvalidateOnAnimation(contentView.getListView());
//					    }
//						return VERTICAL_STOP_LOAD;
//					}
//				}
			}
		}
		
		return -1;
	}
	
	/**
	 * 旋转
	 */
	public void rotate(){
		if(pullBar.getVisibility() == VISIBLE){
			pullBar.rotate(getTop());
		}
	}
	
	/**
	 * 获取下拉刷新栏高度
	 * @return
	 */
	public int getRefreshViewHeight(){
		if(refreshViewHeight <= VERTICAL_STATUS_VIEW_MARGIN * 2){
			refreshViewHeight =  VERTICAL_STATUS_VIEW_MARGIN * 2;
			if(pullBar.getVisibility() == VISIBLE){
				refreshViewHeight +=  pullBar.getHeight() ;
			}else{
				refreshViewHeight += loadingBar.getHeight();
			}
		}
		
		return refreshViewHeight;
	}
	
	/**
	 * 结束加载
	 */
	public void  stopLoading(){
		loadingBar.show(false);
		pullBar.show(true);
		refreshView.getDragHelper().smoothSlideViewTo(this, 0, -getRefreshViewHeight());
		ViewCompat.postInvalidateOnAnimation(refreshView);
	}
	
	/**
	 * 获取内容View
	 * @return
	 */
	public VertivalContentView getContentView() {
		return contentView;
	}

	/**
	 *  下拉是否显示
	 * @return
	 */
	public boolean isShow(){
		return getTop() > - getRefreshViewHeight();
	}
	
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
		//MotionEvent cev = MotionEvent.obtain(ev);  
		ev.offsetLocation(0, getRefreshViewHeight());
		return super.dispatchTouchEvent(ev);
	}
	
	/**
	 * 手指触摸区域是否在此view上
	 * @param ev
	 * @return
	 */
	public boolean inRangeOfView(MotionEvent ev){
		int[] location = new int[2];
		getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		if(ev.getRawX() < x || ev.getRawX() > (x + getWidth()) || ev.getRawY() < y || ev.getRawY() > (y + getHeight())){
			return false;
		}
		return true;
	}
	
	public void startVerticalLoad(){
		if(verticalLoadView != null){
			verticalLoadView.startLoad();
		}
	}
	
	public void failVerticalLoad(){
		if(verticalLoadView != null){
			verticalLoadView.failLoad();
		}
	}
	
	public void stopVerticalLoad(boolean hide){
		if(verticalLoadView != null){
			verticalLoadView.stopLoad(hide);
		}
	}
	
	public void showVerticalLoad(boolean show){
		if(verticalLoadView != null){
			verticalLoadView.setVisibility(show ? VISIBLE : INVISIBLE);
		}
	}
	
	public VerticalLoadView getVerticalLoadView(){
		return verticalLoadView;
	}
	
	public RefreshView getRefreshView(){
		return refreshView;
	}
	
	public boolean isVerticalRefreshing(){
		return loadingBar != null && loadingBar.getVisibility() == VISIBLE;
	}
	
	@Override
	public void setFocusableInTouchMode(boolean focusableInTouchMode){
		super.setFocusableInTouchMode(focusableInTouchMode);
		if(contentView != null){
			contentView.setFocusableInTouchMode(focusableInTouchMode);
		}
	}
}

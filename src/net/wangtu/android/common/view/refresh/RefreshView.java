package net.wangtu.android.common.view.refresh;

import net.wangtu.android.common.view.refresh.component.HorizonView;
import net.wangtu.android.common.view.refresh.component.VerticalView;
import net.wangtu.android.common.view.refresh.component.ViewDragCallback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 刷新View
 * @author zhangxz
 *
 */
@SuppressLint("ClickableViewAccessibility")
public class RefreshView extends RelativeLayout {
	private ViewDragHelper dragHelper;
	private HorizonView horizonView;
	private VerticalView verticalView;
	private RefreshViewEvent refreshViewEvent;
	private ViewDragCallback viewDragCallback;
	private OnScrollListener onScrollListener;
	
	private boolean hasInited = false;
	private boolean isLoading = false;
	private boolean canMove = false;
	
    public RefreshView(Context context) {
        this(context, null);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        verticalView = new VerticalView(getContext(),this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        verticalView.setLayoutParams(layoutParams);
        super.addView(verticalView);
        
        horizonView = new HorizonView(getContext());
        super.addView(horizonView);
        
        //创建拖拽控制器
        viewDragCallback =  new ViewDragCallback(horizonView,verticalView);
        dragHelper = ViewDragHelper.create(this, 1f,viewDragCallback);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	
    	if(verticalView != null){
    		int specMode = MeasureSpec.getMode(widthMeasureSpec);    
    		int specSize = MeasureSpec.getSize(widthMeasureSpec) ;
    		int width = MeasureSpec.makeMeasureSpec(specSize, specMode);
    		if(!hasInited){
    			verticalView.setContentViewHeight(MeasureSpec.getSize(heightMeasureSpec));
    		}
    		specMode = MeasureSpec.getMode(heightMeasureSpec);    
    		specSize = MeasureSpec.getSize(heightMeasureSpec) + verticalView.getRefreshViewHeight(); 
    		int height = MeasureSpec.makeMeasureSpec(specSize, specMode);
    		verticalView.measure(width, height);
    	}
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	int verticalViewTop = verticalView.getTop();
    	int verticalViewBottom = verticalView.getBottom();
    	
    	super.onLayout(changed, l, t, r, b);
    	if(horizonView != null){
    		//historyView重设横坐标
    		int hTop = (int)(getHeight() * 0.38);
    		int hBottom = hTop + horizonView.getHeight();
    		horizonView.layout(horizonView.getLeft(), hTop, horizonView.getRight(), hBottom);
    	}
    	
    	//纠正坐标
    	if(verticalView != null){
    		if(hasInited){
    			verticalView.layout(verticalView.getLeft(), verticalViewTop,verticalView.getRight() , verticalViewBottom);
    		}else{
    			verticalView.layout(0, -verticalView.getRefreshViewHeight(),verticalView.getWidth() , verticalView.getHeight());
    		}
    	}
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
    	boolean dispatch = super.dispatchTouchEvent(ev);
    	
    	//将事件传给子控件
    	if(!verticalView.isVerticalRefreshing() && verticalView.getTop() <=  -verticalView.getRefreshViewHeight() && verticalView.inRangeOfView(ev)){
			if(viewDragCallback == null || !viewDragCallback.change){
				verticalView.dispatchTouchEvent(ev);
			}
    	}
    	
    	return dispatch;
    }

    @Override
	public boolean onTouchEvent(MotionEvent ev) {
		hasInited = true;
        //附加事件处理
        final int action = ev.getAction();
		switch (action & MotionEventCompat.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {
				if(refreshViewEvent.showVerticalRefresh()){
					dragHelper.captureChildView(verticalView, 0);
					canMove = true;
				}else{
					dragHelper.abort();
					canMove = false;
				}
				verticalView.touch(MotionEvent.ACTION_DOWN);
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				verticalView.touch(MotionEvent.ACTION_MOVE);
				break;
			}
			case MotionEvent.ACTION_UP: {
				canMove = false;
				
				//下拉
				int status = verticalView.touch(MotionEvent.ACTION_UP);
				//开始加载
				if(VerticalView.VERTICAL_START_REFRESH == status){
					isLoading = true;
					refreshViewEvent.startVerticalRefresh();
				}
				//放弃加载
				else if(VerticalView.VERTICAL_STOP_REFRESH == status){
					isLoading = false;
					refreshViewEvent.stopVerticalRefresh();
				}
				break;
			}
		}
		
		if(canMove){
			try {
				dragHelper.processTouchEvent(ev);
			} catch (Exception e) {
				//LogUtil.error(e, RefreshView.class);
			}
		}
        return true;
	}

	@Override
	public void computeScroll() {
		if (dragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
		
		if(onScrollListener != null){
			onScrollListener.scroll();
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
        	dragHelper.cancel();
            return false;
        }
		return true;
	}
	
	/**
	 * 主动发起加载
	 */
	public void startLoading(){
		isLoading = true;
		post(new Runnable() {
			@Override
			public void run() {
				horizonView.start();
			}
		});
	}
	
	/**
	 * 停止加载
	 */
	public void stopLoading(){
		isLoading = false;
		post(new Runnable() {
			@Override
			public void run() {
				horizonView.stop();
				verticalView.stopLoading();
			}
		});
	}
	
	/**
	 * 显示加载中
	 */
	public void startVerticalLoad(){
		verticalView.startVerticalLoad();
	}
	
	/**
	 * 显示加载失败
	 */
	public void failVerticalLoad(){
		verticalView.failVerticalLoad();
	}
	
	/**
	 * 显示结束加载
	 */
	public void stopVerticalLoad(boolean hide){
		verticalView.stopVerticalLoad(hide);
	}
	
	/**
	 * 显示加载（默认状态）
	 * @param show
	 */
	public void showVerticalLoad(boolean show){
		verticalView.showVerticalLoad(show);
	}
	
	/*======================================SpecialView_S===============================================*/
	
	/**
	 * 显示特殊页面
	 * @param resid
	 * @param data
	 */
	public void showSpecialViewResource(int resid,Object data){
		showSpecialView(inflate(getContext(), resid, null),data);
	}
	
	
	/**
	 * 显示特殊页面
	 * @param specialView
	 */
	public void showSpecialView(View specialView,Object data){
		//移除
		removeSpecialView();
		
		//添加
		if(specialView instanceof SpecialViewDataDelegate){
			((SpecialViewDataDelegate)specialView).setSpecialViewData(data);
		}
		verticalView.getContentView().showSpecialView(specialView);
	}
	
	/**
	 * 移除特殊页面
	 */
	public void removeSpecialView(){
		verticalView.getContentView().removeSpecialView();
	}
	
	/*======================================SpecialView_E===============================================*/
    
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
    	verticalView.addView(child, params);
    }
    
    public void addViewNotContent(View child, android.view.ViewGroup.LayoutParams params){
    	super.addView(child, 0, params);
    }
	
	public ViewDragHelper getDragHelper() {
		return dragHelper;
	}

	public RefreshViewEvent getRefreshViewEvent() {
		return refreshViewEvent;
	}
	
	public void setRefreshViewEvent(RefreshViewEvent refreshViewEvent) {
		this.refreshViewEvent = refreshViewEvent;
		if(verticalView != null && refreshViewEvent.showVerticalLoad()){
			verticalView.addVerticalLoadView();
		}
	}

	public boolean isLoading() {
		return isLoading;
	}
	
	public View findRefreshViewById(int id) {
		if(verticalView != null && verticalView.getContentView() != null){
			return verticalView.getContentView().findViewById(id);
		}
		return null;
	}
	
	@Override
	public void setFocusableInTouchMode(boolean focusableInTouchMode){
		super.setFocusableInTouchMode(focusableInTouchMode);
		if(verticalView != null){
			verticalView.setFocusableInTouchMode(focusableInTouchMode);
		}
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}
	
	public OnScrollListener getOnScrollListener() {
		return onScrollListener;
	}
	
}

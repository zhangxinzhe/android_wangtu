package net.wangtu.android.common.view.refresh.component.vertical;

import net.wangtu.android.common.view.refresh.component.VerticalView;
import net.wangtu.android.common.view.refresh.component.vertical.adapter.ContentAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 内容View
 * @author zhangxz
 *
 */
@SuppressLint("ViewConstructor")
public class VertivalContentView extends LinearLayout {
	private ContentAdapter contentAdapter;
	private VerticalView verticalView;
	private View specialView;
	
	public VertivalContentView(Context context,VerticalView verticalView) {
		super(context);
		this.verticalView = verticalView;
	}
	
	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		contentAdapter = ContentAdapterFactory.createAdapter(child);
		super.addView(child,params);
    }
	
	/**
	 * 显示特殊页面
	 * @param specialView
	 */
	public void showSpecialView(View specialView){		
		contentAdapter.getView().setVisibility(GONE);
		this.specialView = specialView;
		if(specialView != null){
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,getHeight());
			specialView.setLayoutParams(lp);
			super.addView(specialView);
		}
	}
	
	/**
	 * 移除特殊页面
	 */
	public void removeSpecialView(){
		if(specialView != null){
			removeView(specialView);
			specialView = null;
		}

		contentAdapter.getView().setVisibility(VISIBLE);
	}
	
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}
	
	/**
	 * 获取子View滚动条坐标
	 * @return
	 */
	public int getChildScrollY() {
		if(contentAdapter == null){
			return 0;
		}
		return contentAdapter.getScrollY() ;
	}
	
	/**
	 * 是否已滚动到底部
	 * @return
	 */
	public boolean hasScrolledBottom(){
		if(!verticalView.getRefreshView().getRefreshViewEvent().showVerticalLoad()){
			return false;
		}
		
		if(contentAdapter == null){
			return false;
		}
		
		return contentAdapter.hasScrolledBottom(verticalView,this);
	}

	public ContentAdapter getContentAdapter() {
		return contentAdapter;
	}
}

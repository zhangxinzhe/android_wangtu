package net.wangtu.android.common.view.refresh.component;

import net.wangtu.android.common.util.Util;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;

/**
 * 拖动控制类
 * @author zhangxz
 *
 */
public class ViewDragCallback extends ViewDragHelper.Callback {
	private HorizonView horizonView;
	private VerticalView verticalView;
	private int verticalViewDragEndLine ; //下拉最大坐标
	public boolean change;
	
	public ViewDragCallback(HorizonView horizonView,VerticalView verticalView){
		this.horizonView = horizonView;
		this.verticalView = verticalView;
		
		verticalViewDragEndLine  = Util.dip2px(verticalView.getContext(), 60);//旋转结束分界线
	}

	@Override
	public boolean tryCaptureView(View view, int i) {
		return view ==  horizonView|| view == verticalView;
	}
	
    @Override
    public int clampViewPositionVertical(View child, int top, int dy) {
    	change = false;
    	if(child == verticalView){
    		if(verticalView.getContentView().getChildScrollY() <= 5){
    			//LogUtil.debug("1_" + top + "_" + -verticalView.getRefreshViewHeight());
    			int l = Math.min(Math.max(top,  -verticalView.getRefreshViewHeight()), verticalViewDragEndLine);
    			change = l !=  -verticalView.getRefreshViewHeight();
    			return l;
    		}else{
    			//LogUtil.debug("2_" + top + "_" + -verticalView.getRefreshViewHeight());
    			return -verticalView.getRefreshViewHeight();
    		}
    	}else{
    		return 0;
    	}
    }

    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx){
    	if(child ==  horizonView){
            return left;
    	}else{
    		return 0;
    	}
    }
    
    @Override
	public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
    	if(verticalView != null){
    		verticalView.rotate();
    	}
    	
    	if(verticalView != null && verticalView.getRefreshView() != null && verticalView.getRefreshView().getOnScrollListener() != null){
    		verticalView.getRefreshView().getOnScrollListener().scroll();
    	}
	}
    
	/**
	 * 当拖拽到状态改变时回调
	 * @params 新的状态
	 */
	@Override
	public void onViewDragStateChanged(int state) {
		super.onViewDragStateChanged(state);
		switch (state) {
		case ViewDragHelper.STATE_DRAGGING:  // 正在被拖动
			break;
		case ViewDragHelper.STATE_IDLE:  // view没有被拖拽或者 正在进行fling/snap
			break;
		case ViewDragHelper.STATE_SETTLING: // fling完毕后被放置到一个位置
			change = verticalView.getTop() == -verticalView.getRefreshViewHeight();
			break;
		}
	}
}

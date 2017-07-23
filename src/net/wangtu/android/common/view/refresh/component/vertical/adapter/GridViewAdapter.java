package net.wangtu.android.common.view.refresh.component.vertical.adapter;

import net.wangtu.android.common.view.refresh.component.VerticalView;
import net.wangtu.android.common.view.refresh.component.vertical.VerticalLoadView;
import net.wangtu.android.common.view.refresh.component.vertical.VertivalContentView;
import android.view.View;
import android.widget.GridView;

public class GridViewAdapter implements ContentAdapter{
	private GridView view;
	
	public GridViewAdapter(GridView view){
		this.view = view;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public int getScrollY() {
		View c = view.getChildAt(0);
		if (c == null) {
			return 0;
		}
		int itemHeight = c.getHeight();
		int top = c.getTop();
		
	    int firstVisiblePosition = view.getFirstVisiblePosition();
	    return -top + firstVisiblePosition * itemHeight ;
	}

	@Override
	public boolean hasScrolledBottom(VerticalView verticalView,VertivalContentView vertivalContentView) {
		return view.getLastVisiblePosition() == (view.getCount() - 1);
	}

	@Override
	public void addVerticalLoadView(VerticalLoadView verticalLoadView) {
		
	}
}

package net.wangtu.android.common.view.refresh.component.vertical.adapter;

import net.wangtu.android.common.view.refresh.component.VerticalView;
import net.wangtu.android.common.view.refresh.component.vertical.VerticalLoadView;
import net.wangtu.android.common.view.refresh.component.vertical.VertivalContentView;
import android.view.View;
import android.widget.ListView;

public class ListViewAdapter implements ContentAdapter{
	private ListView view;
	
	public ListViewAdapter(ListView view){
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
		if(view.getLastVisiblePosition() < (view.getCount() - 1)){
			return false;
		}
		
		if(verticalView.getVerticalLoadView().getBottom() - vertivalContentView.getHeight() <= verticalView.getVerticalLoadView().height){
			return true;
		}
		
		return false;
	}

	@Override
	public void addVerticalLoadView(VerticalLoadView verticalLoadView) {
		view.addFooterView(verticalLoadView);
		view.setFooterDividersEnabled(false);
	}
}

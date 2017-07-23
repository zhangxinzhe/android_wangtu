package net.wangtu.android.common.view.refresh.component.vertical.adapter;

import net.wangtu.android.common.view.refresh.component.VerticalView;
import net.wangtu.android.common.view.refresh.component.vertical.VerticalLoadView;
import net.wangtu.android.common.view.refresh.component.vertical.VertivalContentView;
import android.view.View;

public interface ContentAdapter {
	public View getView();
	public int getScrollY();
	public boolean hasScrolledBottom(VerticalView verticalView,VertivalContentView vertivalContentView);
	public void addVerticalLoadView(VerticalLoadView verticalLoadView);
}

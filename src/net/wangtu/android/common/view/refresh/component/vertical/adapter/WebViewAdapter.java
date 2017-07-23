package net.wangtu.android.common.view.refresh.component.vertical.adapter;

import net.wangtu.android.common.view.refresh.ScrollYInterface;
import net.wangtu.android.common.view.refresh.component.VerticalView;
import net.wangtu.android.common.view.refresh.component.vertical.VerticalLoadView;
import net.wangtu.android.common.view.refresh.component.vertical.VertivalContentView;
import android.view.View;
import android.webkit.WebView;

public class WebViewAdapter implements ContentAdapter{
	private WebView view;
	
	public WebViewAdapter(WebView view){
		this.view = view;
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public int getScrollY() {
		int scrollY = view.getScrollY();
		//解决第二级滚动条滚动无法识别问题
		if(scrollY == 0 && view instanceof ScrollYInterface){
			scrollY = ((ScrollYInterface)view).getScrollY1();
		}
		return scrollY;
	}

	@Override
	public boolean hasScrolledBottom(VerticalView verticalView,VertivalContentView vertivalContentView) {
		return false;
	}

	@Override
	public void addVerticalLoadView(VerticalLoadView verticalLoadView) {
		
	}
}

package net.wangtu.android.common.view.refresh.component.vertical;

import net.wangtu.android.common.view.refresh.component.vertical.adapter.ContentAdapter;
import net.wangtu.android.common.view.refresh.component.vertical.adapter.GridViewAdapter;
import net.wangtu.android.common.view.refresh.component.vertical.adapter.GridViewWithHeaderAndFooterAdapter;
import net.wangtu.android.common.view.refresh.component.vertical.adapter.ListViewAdapter;
import net.wangtu.android.common.view.refresh.component.vertical.adapter.WebViewAdapter;
import net.wangtu.android.common.view.GridViewWithHeaderAndFooter;
import android.view.View;
import android.webkit.WebView;
import android.widget.GridView;
import android.widget.ListView;

public class ContentAdapterFactory {
	public static ContentAdapter createAdapter(View view){
		if(view instanceof GridViewWithHeaderAndFooter){
			return new GridViewWithHeaderAndFooterAdapter((GridViewWithHeaderAndFooter)view);
		}else if(view instanceof ListView){
			return new ListViewAdapter((ListView)view);
		}else if(view instanceof WebView){
			return new WebViewAdapter((WebView)view);
		}else if(view instanceof GridView){
			return new GridViewAdapter((GridView)view);
		}
		return null;
	}
}

package net.wangtu.android.common.view.refresh.component;

import net.wangtu.android.common.util.Util;
import net.wangtu.android.common.view.refresh.component.rotate.RotateView;


import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 左右刷新
 * @author zhangxz
 *
 */
public class HorizonView extends RotateView {
    public HorizonView(Context context) {
    	super(context, Util.dip2px(context, 30),Util.dip2px(context, 30));
    	LayoutParams layoutParams =new LayoutParams(Util.dip2px(context, 30),Util.dip2px(context, 30));
    	layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL | RelativeLayout.ALIGN_PARENT_TOP);
//    	layoutParams.setMargins(0, 0, 0, 0);
		setLayoutParams(layoutParams);
    }
}

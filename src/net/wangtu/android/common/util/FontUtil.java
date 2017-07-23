package net.wangtu.android.common.util;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

public class FontUtil {
	
	/**
	 * 通过高度得到字体的尺寸
	 * @param height
	 * @return
	 */
	public static int getFontSize(int height){
		Paint p = new Paint();
        p.setColor(Color.WHITE);  
        p.setTextSize(height);  
        p.setAntiAlias(true);  
        FontMetrics fm = null;
        int textHeight,dela,fontSize=height;
        for (int i = 0; i < 20; i++) {
        	p.setTextSize(fontSize);
        	fm = p.getFontMetrics();
        	textHeight = (int) (Math.ceil(fm.descent - fm.ascent) + 2);
        	dela = textHeight - height;
        	if(dela == 0 || dela == -1){
        		return fontSize;
        	}else if(dela == 1){
        		return fontSize - 1;
        	}
        	
        	if(dela < 0){
        		fontSize += dela/2;
        	}
        	//字体过大
        	else{
        		fontSize -= dela/2;
        	}
		}
        
		return height;
	}
}

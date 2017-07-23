package net.wangtu.android.common.view.refresh.component.rotate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import net.wangtu.android.R;

/**
 * 滚动旋转动画
 * @author zhangxz
 *
 */
@SuppressLint({ "ViewConstructor"})
public class LoadView extends View implements Runnable{
    private Bitmap picture;  
    private Matrix matrix;  
    private Paint painter;
    private float centerX,centerY;
    private float picLeft,picTop;
    private boolean hasInit;
    private static Set<LoadView> views = Collections.synchronizedSet(new HashSet<LoadView>());
    private static Timer timer;
    
    public LoadView(Context context,float width,float height) {
    	super(context);
        setBackgroundResource(android.R.color.transparent);
        
        picture = BitmapFactory.decodeResource(getResources(),  R.drawable.refresh_view_load);
        matrix = new Matrix();
        float scale = height/picture.getHeight();
        matrix.postScale(scale,scale); 
        picture = Bitmap.createBitmap(picture,0,0,picture.getWidth(),picture.getHeight(),matrix,true);
        painter = new Paint();
        
        centerX = (float)picture.getWidth()/2f;
        centerY =  (float)picture.getHeight()/2f;
        setVisibility(GONE);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	if(!hasInit){
    		Rect rect = new Rect();
    		canvas.getClipBounds(rect);
    		picLeft = (rect.right - rect.left - picture.getWidth())/2;
			picTop = (rect.bottom  - rect.top - picture.getHeight())/2;
    		hasInit = true;
    	}
    	if(getVisibility() == VISIBLE){
    		canvas.save();
    		canvas.translate(picLeft,picTop);
    		matrix.preRotate(10.0f, centerX,  centerY);  
    		//canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);  
    		canvas.drawBitmap(picture, matrix, this.painter);
    		canvas.restore();
    	}
    }

	@Override
	public void run() {
		this.invalidate();
	}
	
	/**
	 * 启动动画
	 */
	public synchronized void start(){
		if(!views.contains(this)){
			views.add(this);
		}
		this.setVisibility(VISIBLE);
		if(timer == null){
			timer = new Timer();
	    	timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(views.size() == 0){
						timer.cancel();
						timer = null ;
						return ;
					}
					synchronized(views){
						for (LoadView view : views) {
							//可见就执行
							if(view.getVisibility() == VISIBLE){
								view.post(view);
							}
						}
					}
				}
			}, 0,50);
		}
		
	}
	
	/**
	 * 关闭动画
	 */
	public void stop(){
		if(views.contains(this)){
			views.remove(this);
		}
		this.setVisibility(GONE);
	}
}

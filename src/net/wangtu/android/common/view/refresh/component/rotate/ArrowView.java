package net.wangtu.android.common.view.refresh.component.rotate;

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
 * 箭头旋转动画
 * @author zhangxz
 *
 */
public class ArrowView extends View{
    private Bitmap picture;  
    private Matrix matrix;  
    private Paint painter;
    private float centerX,centerY;
    private float picLeft,picTop;
    private boolean hasInit;
    private float newRotation,oldRotation;//角度
    
    public ArrowView(Context context,float width,float height) {
    	super(context);
        setBackgroundResource(android.R.color.transparent);
        //防止自动缩放
        picture = BitmapFactory.decodeResource(getResources(),  R.drawable.arrow);
        //按比例缩放
        matrix = new Matrix();
        float scale = height/picture.getHeight();
        matrix.postScale(scale,scale); 
        picture = Bitmap.createBitmap(picture,0,0,picture.getWidth(),picture.getHeight(),matrix,true);
        
        painter = new Paint();
        matrix = new Matrix();
        
        centerX = picture.getWidth()/2;
        centerY = picture.getHeight()/2;
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

        canvas.save();
    	canvas.translate(picLeft,picTop);
        matrix.preRotate(this.newRotation - this.oldRotation, centerX,  centerY);
        this.oldRotation = this.newRotation;
        canvas.drawBitmap(picture, matrix, this.painter);  
        canvas.restore();
    }
	
	/**
	 * 启动动画
	 */
	public void setRotation(float rotation) {
		this.newRotation = rotation;
		this.invalidate();
	}
}

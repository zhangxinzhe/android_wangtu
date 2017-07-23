package net.wangtu.android.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import net.wangtu.android.R;

/**
 * 自定义圆形图片
 * @author kuanghf
 */
public class CircleImageView extends ImageView {
	// 默认边界宽度
    private static final int DEFAULT_BORDER_WIDTH = 0;
    // 默认边界颜色
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;
	
    // 缩放类型
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    
    // 初始化画笔
    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();
    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
 
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;// 位图渲染
    private int mBitmapWidth;// 位图宽度
    private int mBitmapHeight;// 位图高度
 
    private float mDrawableRadius;// 图片半径
    private float mBorderRadius;// 带边框的的图片半径
    private ColorFilter mColorFilter;
 
    private int mBorderWidth;
    private int mBorderColor;
    private boolean mBorderOverlay;
    private boolean mReady;
    private boolean mSetupPending;
    
    public CircleImageView(Context context) {
        this(context, null);
    }
    
    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // 获取自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyCircleImageView, defStyle, 0);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.MyCircleImageView_circleBorderWidth, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.MyCircleImageView_circleBorderColor, DEFAULT_BORDER_COLOR);
        mBorderOverlay = a.getBoolean(R.styleable.MyCircleImageView_circleBorderOverlay, DEFAULT_BORDER_OVERLAY);
        a.recycle();
        init();
    }
    
    /**
     * 初始化
     */
    private void init() {
    	super.setScaleType(SCALE_TYPE);
        mReady = true;
 
        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }
 
    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
    	if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }
 
    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        // 绘制内圆形
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
        // 绘制边框
        if (mBorderWidth != 0) {
        	canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
        }
    }
 
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }
 
    public int getBorderColor() {
    	return mBorderColor;
    }
 
    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }
 
        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }
 
    public void setBorderColorResource(@ColorRes int borderColorRes) {
        setBorderColor(getContext().getResources().getColor(borderColorRes));
    }
 
    public int getBorderWidth() {
        return mBorderWidth;
    }
 
    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }
 
        mBorderWidth = borderWidth;
        setup();
    }
 
    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }
 
    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay == mBorderOverlay) {
            return;
        }
 
        mBorderOverlay = borderOverlay;
        setup();
    }
 
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }
 
    @Override
    public void setImageDrawable(Drawable drawable) {
    	super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }
 
    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }
 
    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }
 
    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }
 
        mColorFilter = cf;
        mBitmapPaint.setColorFilter(mColorFilter);
        invalidate();
    }
    
    /**
     * Drawable转Bitmap
     * @param drawable
     * @return
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
 
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
 
        try {
            Bitmap bitmap;
 
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }
 
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
    
    private void setup() {
    	if (!mReady) {
            mSetupPending = true;
            return;
        }
        if (mBitmap == null) {
            return;
        }
        // 构建渲染器，用mBitmap位图来填充绘制区域
        mBitmapShader = new BitmapShader(mBitmap, TileMode.CLAMP, TileMode.CLAMP);
        // 设置图片画笔反锯齿
        mBitmapPaint.setAntiAlias(true);
        // 设置图片画笔渲染器
        mBitmapPaint.setShader(mBitmapShader);
        // 设置边界画笔样式
        mBorderPaint.setStyle(Paint.Style.STROKE);//设画笔为空心
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);    //画笔颜色
        mBorderPaint.setStrokeWidth(mBorderWidth);//画笔边界宽度
        // 取原图片的宽高
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();
        // 设置含边界显示区域，取的是CircleImageView的布局实际大小
        mBorderRect.set(0, 0, getWidth(), getHeight());
        // 计算 圆形带边界部分（外圆）的最小半径
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);
        // 初始图片显示区域为mBorderRect（CircleImageView的布局实际大小）
        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay) {
            mDrawableRect.inset(mBorderWidth, mBorderWidth);
        }
        // 计算内圆的最小半径
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);
        // 设置渲染器的变换矩阵
        updateShaderMatrix();
        // 手动触发ondraw()函数 完成最终的绘制
        invalidate();
    }
    /**
    * 设置BitmapShader的Matrix参数，设置最小缩放比例，平移参数。
    */
    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;
 
        mShaderMatrix.set(null);
        // 取最小的缩放比例
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            // y轴缩放 x轴平移 使得图片的y轴方向的边的尺寸缩放到图片显示区域（mDrawableRect）一样）
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            // x轴缩放 y轴平移 使得图片的x轴方向的边的尺寸缩放到图片显示区域（mDrawableRect）一样）
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }
        mShaderMatrix.setScale(scale, scale);
        // 平移
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }
}

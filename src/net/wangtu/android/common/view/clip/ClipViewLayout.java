package net.wangtu.android.common.view.clip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.wangtu.android.R;
import net.wangtu.android.common.util.ImageUtil;

/**
 * 头像上传原图裁剪容器
 * @author kuanghf
 */
public class ClipViewLayout extends RelativeLayout {
	// 裁剪原图
	private ImageView imageView;
	// 裁剪框
	private ClipImageBorderView clipView;
	// 裁剪框水平方向间距，xml布局文件中指定
	private float mHorizontalPadding;
	// 裁剪框垂直方向间距，计算得出
	private float mVerticalPadding;
	// 图片缩放、移动操作矩阵
	private Matrix matrix = new Matrix();
	// 图片原来已经缩放、移动过的操作矩阵
	private Matrix savedMatrix = new Matrix();
	// 动作标志：无
	private static final int NONE = 0;
	// 动作标志：拖动
	private static final int DRAG = 1;
	// 动作标志：缩放
	private static final int ZOOM = 2;
	// 初始化动作标志
	private int mode = NONE;
	// 记录起始坐标
	private PointF start = new PointF();
	// 记录缩放时两指中间点坐标
	private PointF mid = new PointF();
	private float oldDist = 1f;
	// 用于存放矩阵的9个值
	private final float[] matrixValues = new float[9];
	// 最小缩放比例
	private float minScale;
	// 最大缩放比例
	private float maxScale = 4;

	public ClipViewLayout(Context context) {
		this(context, null);
	}

	public ClipViewLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClipViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	
    /**
     * 初始化控件自定义的属性
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ClipViewLayout);

        //获取剪切框距离左右的边距, 默认为0dp
        mHorizontalPadding = array.getDimensionPixelSize(R.styleable.ClipViewLayout_horizontalPadding, 
        		(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
        //获取裁剪框边框宽度，默认1dp
        int clipBorderWidth = array.getDimensionPixelSize(R.styleable.ClipViewLayout_clipBorderWidth,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        //裁剪框类型(圆或者矩形)
        int clipType = array.getInt(R.styleable.ClipViewLayout_clipType, 1);
        //回收
        array.recycle();
        
        clipView = new ClipImageBorderView(context);
        //设置裁剪框类型
        clipView.setClipType(clipType == 1 ? ClipImageBorderView.ClipType.CIRCLE : ClipImageBorderView.ClipType.RECTANGLE);
        //设置剪切框边框
        clipView.setClipBorderWidth(clipBorderWidth);
        //设置剪切框水平间距
        clipView.setmHorizontalPadding(mHorizontalPadding);
        imageView = new ImageView(context);
        //相对布局布局参数
        ViewGroup.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(imageView, lp);
        this.addView(clipView, lp);
    }
    
	/**
	 * 初始化图片
	 */
	public void setImageSrc(final String path) {
		// 需要等到imageView绘制完毕再初始化原图
		imageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				initSrcPic(path);
				imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}
	
	private void initSrcPic(String path) {
		if (TextUtils.isEmpty(path)) {
			return;
		}

		// 等比压缩图片
		Bitmap bitmap = ImageUtil.decodeSampledBitmap(path, 720, 1280);
		// 竖屏照片旋转90度
		int degress = ImageUtil.getExifOrientation(path);
		bitmap = ImageUtil.rotateBitmap(bitmap, degress);
		
		if (bitmap == null) {
			return;
		}

		// 图片的缩放比
		float scale;
		if (bitmap.getWidth() >= bitmap.getHeight()) {// 宽图
			// 如果高缩放后小于裁剪区域 则将裁剪区域与高的缩放比作为最终的缩放比
			scale = (float) imageView.getWidth() / bitmap.getWidth();
			Rect rect = clipView.getClipRect();
			// 高的最小缩放比
			minScale = rect.height() / (float) bitmap.getHeight();
			if (scale < minScale) {
				scale = minScale;
			}
		} else {// 高图
			// 如果宽缩放后小于裁剪区域 则将裁剪区域与宽的缩放比作为最终的缩放比
			scale = (float) imageView.getHeight() / bitmap.getHeight();
			Rect rect = clipView.getClipRect();
			// 宽的最小缩放比
			minScale = rect.width() / (float) bitmap.getWidth();
			if (scale < minScale) {
				scale = minScale;
			}
		}
		// 缩放
		matrix.postScale(scale, scale);
		// 平移,将缩放后的图片平移到imageview的中心
		// imageView的中心x
		int midX = imageView.getWidth() / 2;
		// imageView的中心y
		int midY = imageView.getHeight() / 2;
		// bitmap的中心x
		int imageMidX = (int) (bitmap.getWidth() * scale / 2);
		// bitmap的中心y
		int imageMidY = (int) (bitmap.getHeight() * scale / 2);
		matrix.postTranslate(midX - imageMidX, midY - imageMidY);
		imageView.setScaleType(ImageView.ScaleType.MATRIX);
		imageView.setImageMatrix(matrix);
		imageView.setImageBitmap(bitmap);
    }
	
	/**
	 * 获取剪切图
	 */
	public Bitmap clip() {
		imageView.setDrawingCacheEnabled(true);
		imageView.buildDrawingCache();
		Rect rect = clipView.getClipRect();
		Bitmap cropBitmap = null;
		Bitmap zoomedCropBitmap = null;
		try {
			cropBitmap = Bitmap.createBitmap(imageView.getDrawingCache(), rect.left, rect.top, rect.width(), rect.height());
			zoomedCropBitmap = ImageUtil.zoomBitmap(cropBitmap, 300, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cropBitmap != null) {
			cropBitmap.recycle();
		}
		// 释放资源
		imageView.destroyDrawingCache();
		return zoomedCropBitmap;
	}

    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			// 设置开始点位置
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			// 开始放下时候两手指间的距离
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) { // 拖动
				matrix.set(savedMatrix);
				float dx = event.getX() - start.x;
				float dy = event.getY() - start.y;
				mVerticalPadding = clipView.getClipRect().top;
				matrix.postTranslate(dx, dy);
				// 检查边界
				checkBorder();
			} else if (mode == ZOOM) { // 缩放
				// 缩放后两手指间的距离
				float newDist = spacing(event);
				if (newDist > 10f) {
					// 手势缩放比例
					float scale = newDist / oldDist;
					if (scale < 1) { // 缩小
						if (getScale() > minScale) {
							matrix.set(savedMatrix);
							mVerticalPadding = clipView.getClipRect().top;
							matrix.postScale(scale, scale, mid.x, mid.y);
							// 缩放到最小范围下面去了，则返回到最小范围大小
							while (getScale() < minScale) {
								// 返回到最小范围的放大比例
								scale = 1 + 0.01F;
								matrix.postScale(scale, scale, mid.x, mid.y);
							}
						}
						// 边界检查
						checkBorder();
					} else { // 放大
						if (getScale() <= maxScale) {
							matrix.set(savedMatrix);
							mVerticalPadding = clipView.getClipRect().top;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
				}
			}
			imageView.setImageMatrix(matrix);
			break;
		}
		return true;
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     */
    private RectF getMatrixRectF(Matrix matrix) {
        RectF rect = new RectF();
        Drawable d = imageView.getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 边界检测
     */
    private void checkBorder() {
        RectF rect = getMatrixRectF(matrix);
        float deltaX = 0;
        float deltaY = 0;
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        // 如果宽或高大于屏幕，则控制范围 ; 这里的0.001是因为精度丢失会产生问题，但是误差一般很小，所以我们直接加了一个0.01
        if (rect.width() >= width - 2 * mHorizontalPadding) {
            if (rect.left > mHorizontalPadding) {
                deltaX = -rect.left + mHorizontalPadding;
            }
            if (rect.right < width - mHorizontalPadding) {
                deltaX = width - mHorizontalPadding - rect.right;
            }
        }
        if (rect.height() >= height - 2 * mVerticalPadding) {
            if (rect.top > mVerticalPadding) {
                deltaY = -rect.top + mVerticalPadding;
            }
            if (rect.bottom < height - mVerticalPadding) {
                deltaY = height - mVerticalPadding - rect.bottom;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 获得当前的缩放比例
     */
    private float getScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }


    /**
     * 多点触控时，计算最先放下的两指距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
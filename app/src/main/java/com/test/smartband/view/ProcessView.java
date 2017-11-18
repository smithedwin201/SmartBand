package com.test.smartband.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 *圆形转圈的 View
 */
public class ProcessView extends View {

    public static final int SHOWSPORTMODE = 0;
    public static final int SHOWSLEEPMODE = 1;

    private int mWidth;
    private int mHeight;

    private Paint mPaint;
    private Paint mTextPaint;

    private float progress = 0f;
    private final float maxProgress = 100f; // 不可以修改的最大值


    // 画笔的粗细（默认为40f, 在 onLayout 已修改）
    private float mStrokeWidth = 40f;
    private ValueAnimator mAnimator;
    private float mLastProgress = -1;
    private Paint mArcPaint;
    private Paint mLinePaint;
    private float circleWidth;
    private int mCenter;
    private float mRadius;
    private float insideRadius;
    private RectF mArcRectf;
    private SweepGradient mSweepGradient;
    private float top;
    private float scanprogress;
    public static int mode = 0;
    private int[] colors;

    public ProcessView(Context context) {
        this(context, null);
    }

    public ProcessView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProcessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    /**
     * 当开始布局时候调用
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 获取总的宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        // 初始化各种值
        initValue();

        // 设置圆环画笔
        setupPaint();

        // 设置文字画笔
        setupTextPaint();
    }

    /**
     * 初始化各种值
     */
    private void initValue() {
        // 画笔的粗细为总宽度的 1 / 15
        mStrokeWidth = mWidth/65;
        circleWidth = mWidth/18;//圆的宽度
    }

    /**
     * 设置圆环画笔
     */
    private void setupPaint() {
        // 创建圆环画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE); // 边框风格
        mPaint.setStrokeWidth(mStrokeWidth);

        mArcPaint = new Paint();
        mArcPaint.setStrokeWidth(circleWidth);//设置描边宽度
        mArcPaint.setAntiAlias(true);//去锯齿
        mArcPaint.setColor(Color.WHITE);//设置颜色
        mArcPaint.setStyle(Paint.Style.STROKE);//设置绘制模式

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(0xffdddddd);
        mLinePaint.setStrokeWidth(5);
    }

    /**
     * 设置文字画笔
     */
    private void setupTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLUE);
        mTextPaint.setTextSize(22);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 第一步：绘制一个圆环
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.GRAY);

        float cx = mWidth / 2.0f;
        float cy = mHeight / 2.0f;
        float radius = mWidth / 2.0f - mStrokeWidth / 2.0f;
        canvas.drawCircle(cx, cy, radius, mPaint);

        // 第三步：绘制动态进度圆环
        mPaint.setDither(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);//设置线段连接处样式为直线
        mPaint.setStrokeCap(Paint.Cap.BUTT); //  设置笔触为圆形
        mPaint.setStrokeWidth(mStrokeWidth);
        if (mode == 0) {
            mPaint.setColor(0xffEA8010);
        } else {
            mPaint.setColor(0xff88147f);
        }

        //设置椭圆
        RectF oval = new RectF(0 + mStrokeWidth / 2, 0 + mStrokeWidth / 2,
                mWidth - mStrokeWidth / 2, mHeight - mStrokeWidth / 2);
        canvas.drawArc(oval, 270, scanprogress / maxProgress * 360, false, mPaint);
        
        initSize();

        mArcPaint.setShader(null);
        //画底部纯白色圆
        canvas.drawArc(mArcRectf, 270, 360, false, mArcPaint);//设置开始角度和要画的角度

        // 设置画笔渐变色
        mArcPaint.setShader(mSweepGradient);
        // 画带渐变色的圆
        canvas.drawArc(mArcRectf, 270, progress / maxProgress * 360, false, mArcPaint);
        //画线 每隔1度画一条线 整个圆共画120条线
        for (int i = 0; i < 180; i++) {
            //圆心正顶部直线的Y坐标
            top = mCenter - mRadius - circleWidth / 2;
            canvas.drawLine(mCenter, mCenter - mRadius + circleWidth /2 + 5, mCenter, top, mLinePaint);
            //旋转画布3度，画笔不变仍然向上
            canvas.rotate(2, mCenter, mCenter);
        }

    }

    private void initSize() {
        mCenter = mHeight / 2;
        mRadius = mCenter - mStrokeWidth*3;
        insideRadius = mRadius - circleWidth / 2;

        mArcRectf = new RectF(mCenter - mRadius, mCenter - mRadius, mCenter + mRadius, mCenter + mRadius);
        if (mode == 0) {
            colors = new int[]{0xFFE5BD7D, 0xFFFAAA64,
                    0xFFFFFFFF, 0xFF6AE2FD,
                    0xFF8CD0E5, 0xFFA3CBCB,
                    0xFFBDC7B3, 0xFFD1C299, 0xFFE5BD7D,};
        } else {
            colors = new int[]{0xFF330066, 0xFF6600FF,
                    0xFFFFFFFF, 0xFFCC66FF,
                    0xFFFF33FF, 0xFFFF00FF,
                    0xFF9900CC, 0xFF660066, 0xFF330066,};
        }

        //渐变色
        mSweepGradient = new SweepGradient(mCenter, mCenter, colors, null);

    }

    /**
     * 设置当前显示的进度条
     *
     * @param progress
     */
    public void setProgress(float progress) {
        this.progress = progress;
        scanprogress = progress;
        // 使用 postInvalidate 比 postInvalidat() 好，线程安全
        postInvalidate();
    }

    /**
     * 改变模式：是显示步数还是睡眠时间
     * @param mode
     */
    public void changeMode(int mode) {
        this.mode = mode;
        postInvalidate();
    }
}

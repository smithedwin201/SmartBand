package com.test.smartband.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BatteryView extends View {

    private Paint mBatteryPaint;
    private Paint mPowerPaint;
    private float mBatteryStroke = 4f;

    private float mBatteryWidth = 60f;//电池宽度
    private float mBatteryHeight = 30f;//电池高度
    private float mCapWidth = 6f;
    private float mCapHeight = 12f;

    private RectF mBatteryRect;
    private RectF mCapRect;
    private RectF mPowerRect;

    private float mPowerPadding = 2;
    private float mPowerWidth = mBatteryWidth - mBatteryStroke - mPowerPadding * 2;//电池身体的总宽度
    private float mPowerHeight = mBatteryHeight - mBatteryStroke - mPowerPadding * 2;//电池身体的高度
    private float mPower = 100f;
    private int measureWidth;
    private int measureHeight;
    private boolean mIsCharge = false;


    public BatteryView(Context context) {
        super(context);
        initView();
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        /**
         * 设置电池画笔
         */
        mBatteryPaint = new Paint();
        mBatteryPaint.setColor(Color.WHITE);          //设置画笔颜色
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setStyle(Paint.Style.STROKE);    //设置画笔模式为填充
        mBatteryPaint.setStrokeWidth(mBatteryStroke);//设置画笔宽度

        /**
         * 设置电量画笔
         */
        mPowerPaint = new Paint();
        mPowerPaint.setColor(Color.GREEN);
        mPowerPaint.setAntiAlias(true);
        mPowerPaint.setStyle(Paint.Style.FILL);
        mPowerPaint.setStrokeWidth(mBatteryStroke);

        /**
         * 设置电池矩形
         * Rect是int(整形)的，而RectF是float(单精度浮点型)的
         */
        mBatteryRect = new RectF(mCapWidth, 0, mBatteryWidth, mBatteryHeight);//设置矩形左上角和右下角两个点的坐标


        /**
         * 设置电池盖矩形
         */
        mCapRect = new RectF(0, (mBatteryHeight - mCapHeight) / 2, mCapWidth,
                (mBatteryHeight - mCapHeight) / 2 + mCapHeight);


        /**
         * 设置电量矩形
         */
        mPowerRect = new RectF(mCapWidth + mBatteryStroke / 2 + mPowerPadding + mPowerWidth * ((100f - mPower) / 100f),
                mPowerPadding + mBatteryStroke / 2,
                mBatteryWidth - mPowerPadding * 2,
                mBatteryStroke / 2 + mPowerPadding + mPowerHeight
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsCharge) {
            mPowerPaint.setColor(Color.GREEN);
        } else {
            if (mPower > 30f) {
                mPowerPaint.setColor(Color.WHITE);
            } else {
                mPowerPaint.setColor(Color.RED);
            }
        }

        canvas.save();
        canvas.translate(0, 0);//将坐标系移动
        canvas.drawRoundRect(mBatteryRect, 2f, 2f, mBatteryPaint);//画电池轮廓需要考虑画笔的宽度
        canvas.drawRoundRect(mCapRect, 2f, 2f, mBatteryPaint);//画电池盖
        canvas.drawRect(mPowerRect,mPowerPaint);//画电量
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    /**
     * 设置电池电量
     */
    public void setPower(boolean isCharge, float power) {
        this.mPower = power;
        if (mPower < 0) {
            mPower = 0;
        }
        mIsCharge = isCharge;
        mPowerRect = new RectF(mCapWidth + mBatteryStroke / 2 + mPowerPadding
                + mPowerWidth * ((100f - mPower) / 100f), // 需要调整左边的位置
                mPowerPadding + mBatteryStroke / 2, // 需要考虑到 画笔的宽度
                mBatteryWidth - mPowerPadding * 2, mBatteryStroke / 2
                + mPowerPadding + mPowerHeight);
        invalidate();
    }
}

package com.yhongm.wave_progress_view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;


public class WaveProgressView extends View {
    private int mWidth = 900;
    private int mHeight = 900;
    private int mWaveLength = 800;//一个长度
    private int mWaveCount = 2;//数量
    private int mWaveOffsetX = 0;
    private int mWaveOffsetX2 = 0;
    private int mRadius;
    private int mCurrentHeight = 0;
    private int mWaveOffsetY;
    private int mWaveOffsetY2;
    private int mWaterColor = Color.rgb(84, 184, 227);//水颜色
    private int mWaterColor2 = Color.rgb(226,239,152);//水颜色
    private int mCircleColor = mWaterColor;
    private Paint mWavePaint;//水波画笔
    private Paint mWavePaint2;//水波画笔
    private Paint mCirclePaint;//圆形画笔
    float defaultProgress;//默认的进度

    public WaveProgressView(Context context) {
        super(context);
    }

    public WaveProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveProgressView);
        mWaterColor = typedArray.getColor(R.styleable.WaveProgressView_waterColor, mWaterColor);
        mCircleColor = typedArray.getColor(R.styleable.WaveProgressView_circleColor, mWaterColor);

        defaultProgress = typedArray.getFloat(R.styleable.WaveProgressView_progress, 0);
        initPaint();
        initAnimator();

    }

    private void initPaint() {


        mWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setColor(mWaterColor);
        mWavePaint.setStrokeWidth(10);

        mWavePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWavePaint2.setStyle(Paint.Style.FILL);
        mWavePaint2.setColor(mWaterColor2);
        mWavePaint2.setStrokeWidth(10);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(10);
    }

    private void initAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mWaveLength);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWaveOffsetX = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setDuration(800);
        valueAnimator.start();
        ValueAnimator valueAnimator2 = ValueAnimator.ofInt(0, mWaveLength);

        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWaveOffsetX2 = (int) animation.getAnimatedValue();
            }
        });
        valueAnimator2.setInterpolator(new AccelerateInterpolator());
        valueAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator2.setDuration(1200);
        valueAnimator2.start();
        ValueAnimator valueAnimatorWaveY = ValueAnimator.ofInt(80, 120);
        valueAnimatorWaveY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWaveOffsetY = (int) animation.getAnimatedValue();
            }
        });
        valueAnimatorWaveY.setInterpolator(new AccelerateInterpolator());
        valueAnimatorWaveY.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimatorWaveY.setDuration(800);
        valueAnimatorWaveY.start();
        ValueAnimator valueAnimatorWaveY2 = ValueAnimator.ofInt(80, 120);
        valueAnimatorWaveY2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mWaveOffsetY2 = (int) animation.getAnimatedValue();
            }
        });
        valueAnimatorWaveY2.setInterpolator(new DecelerateInterpolator());
        valueAnimatorWaveY2.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimatorWaveY2.setDuration(1200);
        valueAnimatorWaveY2.start();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(900, 900);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mCurrentHeight = mHeight;
        mRadius = Math.min(mWidth / 2, mHeight / 2) - 10;
        mWaveCount = (int) ((mWidth / mWaveLength) + 1.5);
        setProgress(defaultProgress / 100f);
    }

    public void setProgress(final float progress) {
        mCurrentHeight = (int) (mHeight * progress);
        mCurrentHeight = mHeight - mCurrentHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWave(canvas);
    }


    /**
     * 画波浪
     *
     * @param canvas
     */
    private void drawWave(Canvas canvas) {
        Path mCirclePath = getCirclePath();
        Path mPath = getWavePath(-1f, mWaveLength, mWaveOffsetX, mWaveOffsetY);
        Path mPath2 = getWavePath(-0.75f, mWaveLength, mWaveOffsetX2, mWaveOffsetY2);
        canvas.clipPath(mCirclePath);
        canvas.drawPath(mPath, mWavePaint);
        canvas.clipPath(mCirclePath);
//        canvas.drawPath(mPath2, mWavePaint2);
        canvas.drawPath(mCirclePath, mCirclePaint);
    }

    /**
     * 画圆
     *
     * @return
     */
    private Path getCirclePath() {
        Path mCirclePath = new Path();
        mCirclePath.addCircle(mWidth / 2, mHeight / 2, mRadius, Path.Direction.CW);
        return mCirclePath;
    }

    /**
     * 生成水波
     *
     * @param begin       水波形开始的位置
     * @param waveLength  水波的长度
     * @param waveOffsetX 水波水平的偏移
     * @param waveOffsetY 水波垂直方向的偏移
     * @return
     */
    private Path getWavePath(float begin, int waveLength, int waveOffsetX, int waveOffsetY) {
        Path mPath = new Path();
        mPath.reset();
        mPath.moveTo(waveLength * begin, mCurrentHeight);
        for (int i = 0; i < mWaveCount; i++) {
            mPath.quadTo(waveLength * (begin + 0.25f) + (i * waveLength) + waveOffsetX, mCurrentHeight + waveOffsetY, (waveLength * (begin + 0.5f) + (i * waveLength) + waveOffsetX), mCurrentHeight);
            mPath.quadTo(waveLength * (begin + 0.75f) + (i * waveLength) + waveOffsetX, mCurrentHeight - waveOffsetY, (waveLength * (begin + 1f) + (i * waveLength) + waveOffsetX), mCurrentHeight);
        }
        mPath.lineTo(mWidth, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();
        return mPath;
    }


}
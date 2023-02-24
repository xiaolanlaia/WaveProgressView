package com.yhongm.wave_progress_demo;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;


/**
 * @Description
 * @Author WuJianFeng
 * @Date 2023/2/24 7:21
 */
public class WaveView extends View {
    private Paint mPaintWave1;
    private Paint mPaintWave2;
    private int mWidth;
    private int mHeight;
    private int mCurrentHeight;
    private int mWaveHeight;
    //波长
    private int mWave1Length;
    private int mWave2Length;
    //水平偏移量
    private int dxWave1;
    //水平偏移量
    private int dxWave2;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaintWave1 = new Paint();
        mPaintWave1.setAntiAlias(true);
        mPaintWave1.setDither(true);
        mPaintWave1.setColor(getResources().getColor(R.color.wave_2));
        mPaintWave1.setAlpha(95);
        mPaintWave1.setStyle(Paint.Style.FILL);

        mPaintWave2 = new Paint();
        mPaintWave2.setAntiAlias(true);
        mPaintWave2.setDither(true);
        mPaintWave2.setColor(getResources().getColor(R.color.wave_1));
        mPaintWave2.setAlpha(95);
        mPaintWave2.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //控件的宽高
        mWidth = MeasureUtils.measureView(widthMeasureSpec, 300);
        mHeight = MeasureUtils.measureView(heightMeasureSpec, 300);
        mWave1Length = MeasureUtils.measureView(widthMeasureSpec, 300) * 2;
        mWave2Length = MeasureUtils.measureView(widthMeasureSpec, 300) * 3;
        //水波的高度
        mWaveHeight = MeasureUtils.measureView(heightMeasureSpec, 300) / 6;
        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path pathWave1 = drawWave(dxWave1);
        canvas.drawPath(pathWave1, mPaintWave1);

        Path pathWave2 = drawWave(dxWave2);
        canvas.drawPath(pathWave2, mPaintWave2);

    }


    private Path drawWave(int dx) {
        Path path = new Path();
        path.reset();
        path.moveTo((-mWave1Length) + dx, mCurrentHeight);

        Log.d("__drawWave","x = " + ((-mWave1Length) + dx) + " mWaveLength / 4 = "+(mWave1Length / 4));
        for (int i = -mWave1Length; i < getWidth() + mWave1Length; i += mWave1Length) {
            path.rCubicTo(mWave1Length / 4, -mWaveHeight, mWave1Length / 4, mWaveHeight, mWave1Length / 2, 0);

        }
        path.lineTo(mWidth, mHeight);
        path.lineTo(0, mHeight);
        //path.close() 绘制封闭的区域
        path.close();

        return path;
    }

    public void startAnimation() {

        //水波动画
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, mWave1Length);
        valueAnimator.setDuration(1500);
//        valueAnimator.setRepeatCount(Animation.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //水平方向的偏移量
                dxWave1 = (int)animation.getAnimatedValue();
                dxWave2 = (int)((int)animation.getAnimatedValue() * 0.7);


            }

        });
        valueAnimator.start();


        //水波升高动画
        ValueAnimator valueAnimatorHeight = ValueAnimator.ofInt(mHeight,mHeight / 2);
        valueAnimatorHeight.setDuration(1500);
        valueAnimatorHeight.setRepeatCount(Animation.INFINITE);
        valueAnimatorHeight.setInterpolator(new LinearInterpolator());
        valueAnimatorHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //水平方向的偏移量

                mCurrentHeight = (int)animation.getAnimatedValue();

                invalidate();
            }

        });

        valueAnimatorHeight.addListener(new Animator.AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation) {

                Log.d("__ami-start","1");

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d("__ami-end","1");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d("__ami-cancel","1");

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

                if (valueAnimator.isRunning()){
                    valueAnimator.cancel();
                }

                if (!valueAnimator.isRunning()){
                    valueAnimator.start();
                }
            }
        });
        valueAnimatorHeight.start();

    }
}

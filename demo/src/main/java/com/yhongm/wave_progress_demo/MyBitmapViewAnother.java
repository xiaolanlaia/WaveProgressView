package com.yhongm.wave_progress_demo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

/**
 * @Description
 * @Author WuJianFeng
 * @Date 2023/2/24 15:37
 */
public class MyBitmapViewAnother extends View {
    private int mWidth;//设置高
    private int mHeight;//设置高
    private Paint mPaint;

    //设置一个Bitmap
    private Bitmap bitmap;
    //创建该Bitmap的画布
    private Canvas bitmapCanvas;
    private Paint mPaintCover;
    private Paint mPaintRect;

    //定义一样个背景的Bitmap
    private Bitmap mBitmapBackground;
    private Matrix matrix;


    private int mCurrentHeight;
    private int mWaveHeight;
    private int mWave1Length;
    //水平偏移量
    private int dxWave1;


    public MyBitmapViewAnother(Context context) {
        super(context);
    }

    public MyBitmapViewAnother(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();//Bitmap的画笔

        //设置背景
        mBitmapBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        mPaintCover = new Paint();
        mPaintCover.setAntiAlias(true);
        mPaintCover.setColor(Color.GRAY);
        mPaintCover.setStrokeWidth(50);
        //设置图形混合方式，这里使用PorterDuff.Mode.XOR模式，与底层重叠部分设为透明
        PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.XOR);
        mPaintCover.setXfermode(mode);

        //这三句代码很重要，别忘记写了
        mPaintCover.setStyle(Paint.Style.STROKE);
        //设置笔刷的样式，默认为BUTT，如果设置为ROUND(圆形),SQUARE(方形)，需要将填充类型Style设置为STROKE或者FILL_AND_STROKE
        mPaintCover.setStrokeCap(Paint.Cap.ROUND);
        //设置画笔的结合方式
        mPaintCover.setStrokeJoin(Paint.Join.ROUND);

        //绘制蒙版的画笔
        mPaintRect = new Paint();
        mPaintRect.setAntiAlias(true);
        mPaintRect.setColor(Color.LTGRAY);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);//设置宽和高

        //创建一个Bitmap，用于绘图。
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);//该画布为bitmap。

        //绘制背景BitmapBackground大小的矩阵
        matrix = new Matrix();//如果在构造器中初始化，需要使用reset()方法
        matrix.postScale((float) mWidth /mBitmapBackground.getWidth(), (float) mHeight /mBitmapBackground.getHeight());
        startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将bitmapBackground设置该View画布的背景
        canvas.drawBitmap(mBitmapBackground,matrix,null);
        //然后画布添加背景的基础上添加bitmap。
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
        bitmapCanvas.drawRect(0, 0, mWidth, mHeight, mPaintRect);//bitmap上绘制一个蒙版


        Path pathWave1 = drawWave(dxWave1);

        bitmapCanvas.drawPath(pathWave1, mPaintCover);//bitmap上绘制手 划过的路径
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
//                dxWave2 = (int)((int)animation.getAnimatedValue() * 0.7);


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
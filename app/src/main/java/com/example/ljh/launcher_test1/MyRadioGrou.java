package com.example.ljh.launcher_test1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

/**
 * Created by 知晓 on 2016/11/9.
 */
public class MyRadioGrou extends RadioGroup {

    Paint mPaint;

    public MyRadioGrou(Context context) {
        super(context);
    }

    public MyRadioGrou(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;
        int childCount = getChildCount();

        if(widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }
        else {
            int max = 0;
            for(int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, heightMeasureSpec);
                max = (max > child.getMeasuredWidth()) ? max : child.getMeasuredHeight();
            }
            width = max;
        }

        if(heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        else {
            for(int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, heightMeasureSpec);
                height += child.getMeasuredHeight();
            }
        }


        setMeasuredDimension(width + 100, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint = new Paint();
        mPaint.setColor(0x88696969);
        mPaint.setAntiAlias(true);
        Path path = new Path();
        int width = getMeasuredWidth() - 150;
        path.moveTo(width, getMeasuredHeight());
        path.lineTo(0, getMeasuredHeight());
        path.lineTo(0, 0);
        path.lineTo(width, 0);
        path = verticalBezierWave(path, width, 0, width, getMeasuredHeight());
        canvas.drawPath(path, mPaint);
    }

    /**
     *  获得水纹的贝塞尔曲线
     * @param path 需要画的path
     * @param startX 开始坐标x
     * @param startY 开始坐标y
     * @param endX 结束坐标x
     * @param endY 结束坐标y
     * @return 返回画好贝塞尔曲线的path
     */
    private Path verticalBezierWave(Path path, int startX, int startY, int endX, int endY) {
        int centerX = startX + (endX - startX) / 2;
        int centerY = startY + (endY - startY) / 2;
        path.moveTo(startX, startY);
        path.quadTo(startX + (endY - startY) / 40 * 3, startY + (centerY - startY) / 5, centerX, centerY);
        path.quadTo(endX - (endY - startY) / 40 * 3, endY - (centerY - startY) / 5, endX, endY);
        return path;
    }


}

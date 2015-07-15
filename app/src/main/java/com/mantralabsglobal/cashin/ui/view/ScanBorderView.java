package com.mantralabsglobal.cashin.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.mantralabsglobal.cashin.R;

public class ScanBorderView extends View {
    private int mBorderHeight;
    private int mBorderWidth;
    private int mBorderColor;
    private Rect mBounds, mDrawBounds;
    private Paint mPaint;
    private double aspectRatio=1;

    double scanX;
    double scanY;
    double scanHeight;
    double scanWidth;

    public double getScanX()
    {
        return scanX;
    }
    public double getScanY()
    {
        return scanY;
    }

    public double getScanWidth()
    {
        return scanWidth;
    }

    public double getScanHeight()
    {
        return scanHeight;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio= aspectRatio;

    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public int getmBorderColor() {
        return mBorderColor;
    }

    public void setmBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

   public ScanBorderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBounds = new Rect();
        mDrawBounds = new Rect();

        mBorderWidth = 4;
        mBorderHeight = 40;
        mBorderColor = Color.RED;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBorderColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        double rectangleWidth = width;
        double rectangleHeight = height;

        if(aspectRatio<=1 && width<=height)
        {
            rectangleWidth = width*5/7;
            rectangleHeight = rectangleWidth * aspectRatio;
        }
        else
        {
            rectangleHeight = height*5/7;
            rectangleWidth =  rectangleHeight/aspectRatio;
        }

        int bufferHeight = (int)(height-rectangleHeight)/2;
        int bufferWidth = (int)(width-rectangleWidth)/2;

        mBounds.set(bufferWidth, bufferHeight,
                getWidth() - bufferWidth, getHeight() - bufferHeight);

        int boundHeight = (int)rectangleHeight/4;
        int boundWidth = (int) rectangleWidth/4;

        scanX = ((double)bufferWidth)/width;
        scanY = ((double)bufferHeight)/height;
        scanWidth =  rectangleWidth/width;
        scanHeight = rectangleHeight/height;

        mBorderHeight = Math.min(boundHeight, boundWidth);

        // top-left
        mDrawBounds.set(mBounds);
        mDrawBounds.right = mDrawBounds.left + mBorderWidth;
        mDrawBounds.bottom = mDrawBounds.top + mBorderHeight;
        canvas.drawRect(mDrawBounds, mPaint);

        mDrawBounds.right = mDrawBounds.left + mBorderHeight;
        mDrawBounds.bottom = mDrawBounds.top + mBorderWidth;
        canvas.drawRect(mDrawBounds, mPaint);


        // top-right
        mDrawBounds.set(mBounds);
        mDrawBounds.left = mDrawBounds.right - mBorderWidth;
        mDrawBounds.bottom = mDrawBounds.top + mBorderHeight;
        canvas.drawRect(mDrawBounds, mPaint);

        mDrawBounds.left = mDrawBounds.right - mBorderHeight;
        mDrawBounds.bottom = mDrawBounds.top + mBorderWidth;
        canvas.drawRect(mDrawBounds, mPaint);


        // bottom-left
        mDrawBounds.set(mBounds);
        mDrawBounds.top = mDrawBounds.bottom - mBorderHeight;
        mDrawBounds.right = mDrawBounds.left + mBorderWidth;
        canvas.drawRect(mDrawBounds, mPaint);

        mDrawBounds.set(mBounds);
        mDrawBounds.top = mDrawBounds.bottom - mBorderWidth;
        mDrawBounds.right = mDrawBounds.left + mBorderHeight;
        canvas.drawRect(mDrawBounds, mPaint);


        // bottom-right
        mDrawBounds.set(mBounds);
        mDrawBounds.top = mDrawBounds.bottom - mBorderHeight;
        mDrawBounds.left = mDrawBounds.right - mBorderWidth;
        canvas.drawRect(mDrawBounds, mPaint);

        mDrawBounds.set(mBounds);
        mDrawBounds.top = mDrawBounds.bottom - mBorderWidth;
        mDrawBounds.left = mDrawBounds.right - mBorderHeight;
        canvas.drawRect(mDrawBounds, mPaint);

    }
}
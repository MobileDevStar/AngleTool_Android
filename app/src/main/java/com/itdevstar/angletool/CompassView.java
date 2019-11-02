package com.itdevstar.angletool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class CompassView extends View {
    private static final String TAG = "CompassView";

    private Drawable mCompassDrawable;

    private boolean mStart = false;
    private float   mPitch = 0;

    private Paint mPaint = new Paint();
    private boolean mAnimate;

    private int     mLineColor = Color.rgb(12, 110, 165);
    private float   mCxRate = 0.296f;
    private float   mCyRate = 0.201f;
    private float   mLineLenRate = 0.603f;

    public CompassView(Context context) {
        super(context);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void resetPitch(float pitch) {
        mStart = true;
        mPitch = pitch;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = mPaint;

        canvas.drawColor(Color.BLACK);

        paint.setAntiAlias(true);
        paint.setColor(mLineColor);
        paint.setStyle(Paint.Style.FILL);

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        int top = (h - w) / 2;

        mCompassDrawable = getResources().getDrawable(R.drawable.sc_position);
        mCompassDrawable.setBounds(0, top, w, w + top);
        mCompassDrawable.draw(canvas);

        Path path = new Path();
        path.moveTo(-6, 0);
        path.lineTo(-6, w * mLineLenRate);
        path.lineTo(6, w * mLineLenRate);
        path.lineTo(6, 0);
        path.close();

        int cx = (int)(w * mCxRate);
        int cy = top + (int)(w * mCyRate);

        canvas.translate(cx, cy);
        if (mStart) {
            canvas.rotate(-mPitch);
        }
        canvas.drawPath(path, mPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        mAnimate = true;
        if (false) Log.d(TAG, "onAttachedToWindow. mAnimate=" + mAnimate);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        mAnimate = false;
        if (false) Log.d(TAG, "onDetachedFromWindow. mAnimate=" + mAnimate);
        super.onDetachedFromWindow();
    }
}

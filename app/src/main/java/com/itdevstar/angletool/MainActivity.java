package com.itdevstar.angletool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AngleTool";

    private SensorManager   mSensorManager;
    private Sensor          mSensorAccelerometer;
    private Sensor          mSensorMagneticField;

    // Gravity rotational data
    private float gravity[];
    // Magnetic rotational data
    private float magnetic[]; //for magnetic rotational data
    private float accels[] = new float[3];
    private float mags[] = new float[3];
    private float[] values = new float[3];

    // azimuth, pitch and roll
    private float azimuth;
    private float pitch;
    private float roll;

    private Drawable    mCompassDrawable;
    private CompassView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mCompassDrawable = getResources().getDrawable(R.drawable.sc_position);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mView = new CompassView(this);
        setContentView(mView);
    }

    @Override
    protected void onResume() {
        if (false) Log.d(TAG, "onResume");
        super.onResume();

        mSensorManager.registerListener(mySensorEventListener, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mySensorEventListener, mSensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onStop()
    {
        if (false) Log.d(TAG, "onStop");
        mSensorManager.unregisterListener(mySensorEventListener);
        super.onStop();
    }

    private SensorEventListener mySensorEventListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mags = event.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    accels = event.values.clone();
                    break;
            }

            if (mags != null && accels != null) {
                gravity = new float[9];
                magnetic = new float[9];
                SensorManager.getRotationMatrix(gravity, magnetic, accels, mags);
                float[] outGravity = new float[9];
                SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X,SensorManager.AXIS_Z, outGravity);
                SensorManager.getOrientation(outGravity, values);

                azimuth = values[0] * 57.2957795f;
                pitch =values[1] * 57.2957795f;
                roll = values[2] * 57.2957795f;
                mags = null;
                accels = null;

                Log.e("azimuth", Float.toString(azimuth));
                Log.e("pitch", Float.toString(pitch));
                Log.e("roll", Float.toString(roll));

                if (mView != null) {
                    mView.invalidate();
                }
            }
        }
    };

    private class CompassView extends View {
        private Paint   mPaint = new Paint();
        private boolean mAnimate;

        private int     mLineColor = Color.rgb(12, 110, 165);
        private float   mCxRate = (float)0.296;
        private float   mCyRate = (float)0.201;
        private float   mLineLenRate = (float)(0.603);

        public CompassView(Context context) {
            super(context);
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
            int cx = (int)(w * mCxRate);
            int cy = top + (int)(w * mCyRate);

            Path path = new Path();
            path.moveTo(-6, 0);
            path.lineTo(-6, w * mLineLenRate);
            path.lineTo(6, w * mLineLenRate);
            path.lineTo(6, 0);
            path.close();

            mCompassDrawable.setBounds(0, top, w, w + top);
            mCompassDrawable.draw(canvas);

            canvas.translate(cx, cy);
            if (values != null) {
                canvas.rotate(-pitch);
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
}

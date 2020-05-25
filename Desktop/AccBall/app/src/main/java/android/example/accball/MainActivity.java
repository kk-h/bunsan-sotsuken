package android.example.accball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SurfaceHolder.Callback {
    SensorManager mSensorManager;
    Sensor mAccSensor;
    SurfaceHolder mHolder;
    int mSurfaceWidth;
    int mSurfaceHeight;

    static final float RADIUS = 50.0f;
    static final float COEF = 1000.0f;

    float mBallX;
    float mBallY;
    float mVX;
    float mVY;

    long mFrom;
    long mTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            Log.d("MainActivity", "x=" + String.valueOf(event.values[0]) +
                                            "y=" + String.valueOf(event.values[1]) +
                                            "z=" + String.valueOf(event.values[2]));
            float x = -event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            mTo = System.currentTimeMillis();
            float t = (float)(mTo - mFrom);
            t = t/1000.0f;

            float dx = mVX*t+x*t*t/2.0f;
            float dy = mVY*t+y*t*t/2.0f;
            mBallX = mBallX+dx*COEF;
            mBallY = mBallY+dy*COEF;
            mVX = mVX+x*t;
            mVY = mVY+x*t;

            if (mBallX-RADIUS<0 && mVX<0){
                mVX = -mVX/1.5f;
                mBallX = RADIUS;
            }else if(mBallX+RADIUS>mSurfaceWidth && mVX>0){
                mVX = -mVX/1.5f;
                mBallX = mSurfaceWidth-RADIUS;
            }

            if (mBallY-RADIUS<0 && mVY<0){
                mVY = -mVY/1.5f;
                mBallY = RADIUS;
            }else if(mBallY+RADIUS>mSurfaceHeight && mVY>0){
                mVY = -mVY/1.5f;
                mBallY = mSurfaceHeight-RADIUS;
            }

            mFrom = System.currentTimeMillis();
            drawCanvas();
        }
    }

    private void drawCanvas() {
        Canvas c = mHolder.lockCanvas();
        c.drawColor(Color.YELLOW);
        Paint paint = new Paint();
        paint.setColor(Color.MAGENTA);
        c.drawCircle(mBallX, mBallY, RADIUS, paint);
        mHolder.unlockCanvasAndPost(c);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mFrom = System.currentTimeMillis();
        mSensorManager.registerListener(this, mAccSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mBallX = width/2;
        mBallY = height/2;
        mVX = 0;
        mVY = 0;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSensorManager.unregisterListener(this);
    }
}

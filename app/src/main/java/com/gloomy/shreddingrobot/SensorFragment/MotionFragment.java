package com.gloomy.shreddingrobot.SensorFragment;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.gloomy.shreddingrobot.Utility.Constants;
import com.gloomy.shreddingrobot.Widget.Logger;

import java.util.Timer;
import java.util.TimerTask;

public class MotionFragment extends Fragment {
    private static final String TAG = "MotionFrag";
    private static final double AIR_TIME_NOISE_THRESHOLD = 0.2;
    private static final double FREE_FALL_THRESHOLD = 2.0;
    private static final int SENSOR_UPDATE_TIME_IN_MILLISECONDS = 50;
    private static final int DURATION_UPDATE_INTERVAL_IN_SECONDS = 1;
    private static final int DURATION_UPDATE_IN_MILLISECONDS = DURATION_UPDATE_INTERVAL_IN_SECONDS * Constants.UC_MILLISECONDS_IN_SECOND;

    private Context _context;
    private MotionCallbacks mUICallback;
    private MotionCallbacks mDataCallback;

    private SensorManager mSensorManager;
    private Sensor mGraSensor;
    private Sensor mAccSensor;

    private Timer sensorTimer, trackTimer;
    private SensorTimerTask sensorTimerTask;
    private TrackTimerTask trackTimerTask;

    private boolean freeFalling;
    private boolean noGraSensor;

    private double[] graReading;
    private double[] accReading;
    private double graMag, accMag;
    private double projection;

    private double airTime;
    private int duration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = getActivity();
        init();
    }

    public void setUpDataCallback(MotionCallbacks callback){
        mDataCallback = callback;
    }

    public void setUpUICallback(MotionCallbacks callback) {
        mUICallback = callback;
    }

    private void init(){
        freeFalling = false;
        noGraSensor = false;

        mSensorManager = (SensorManager) _context.getSystemService(Context.SENSOR_SERVICE);
        mGraSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        noGraSensor = mGraSensor == null;
        Logger.d(TAG, "noGraSensor: "+noGraSensor);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        graReading = new double[3];
        accReading = new double[3];
    }

    SensorEventListener mSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            // In this example, alpha is calculated as t / (t + dT),
            // where t is the low-pass filter's time-constant and
            // dT is the event delivery rate.

            final double alpha = 0.7;
//            noGraSensor = true;

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accReading[0] = event.values[0];
                accReading[1] = event.values[1];
                accReading[2] = event.values[2];
                accMag = Math.sqrt(Math.pow(accReading[0], 2) + Math.pow(accReading[1], 2) + Math.pow(accReading[2], 2));

                if(noGraSensor){
                    // Isolate the force of gravity with the low-pass filter.
                    graReading[0] = alpha * graReading[0] + (1 - alpha) * accReading[0];
                    graReading[1] = alpha * graReading[1] + (1 - alpha) * accReading[1];
                    graReading[2] = alpha * graReading[2] + (1 - alpha) * accReading[2];
                    graMag = Math.sqrt(Math.pow(graReading[0], 2) + Math.pow(graReading[1], 2) + Math.pow(graReading[2], 2));
                }
            }

            if (!noGraSensor&&event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                graReading[0] = event.values[0];
                graReading[1] = event.values[1];
                graReading[2] = event.values[2];
                graMag = Math.sqrt(Math.pow(graReading[0], 2) + Math.pow(graReading[1], 2) + Math.pow(graReading[2], 2));
            }

            projection = 0.0;
            for (int axis = 0; axis < 3; axis++) {
                projection += accReading[axis] * graReading[axis];
            }
            freeFalling = (projection/graMag < FREE_FALL_THRESHOLD);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    public void startTracking() {
        Log.e(TAG, "start tracking");
        //Motion Sensor
        if (!noGraSensor) {
            mSensorManager.registerListener(mSensorListener, mGraSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        mSensorManager.registerListener(mSensorListener, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);

        airTime = 0.0;
        if (mDataCallback != null)
            mDataCallback.updateAirTime(airTime);
        if (mUICallback != null)
            mUICallback.updateAirTime(airTime);

        duration = 0;
        if (mDataCallback != null)
            mDataCallback.updateDuration(duration);
        if (mUICallback != null)
            mUICallback.updateDuration(duration);

        destroyTimer();
        trackTimer = new Timer();
        trackTimerTask = new TrackTimerTask();
        trackTimer.scheduleAtFixedRate(trackTimerTask, DURATION_UPDATE_IN_MILLISECONDS, DURATION_UPDATE_IN_MILLISECONDS);

        sensorTimer = new Timer();
        sensorTimerTask = new SensorTimerTask();
        sensorTimer.scheduleAtFixedRate(sensorTimerTask, 500, SENSOR_UPDATE_TIME_IN_MILLISECONDS);
    }

    public void stopTracking() {
        mSensorManager.unregisterListener(mSensorListener);
        destroyTimer();
    }

    private void destroyTimer() {
        if (sensorTimer != null) {
            sensorTimerTask.cancel();
            sensorTimer.cancel();
            sensorTimer = null;
        }
        if (trackTimer != null) {
            trackTimerTask.cancel();
            trackTimer.cancel();
            trackTimer = null;
        }
    }

    class SensorTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (freeFalling) {
                        airTime += ((double) SENSOR_UPDATE_TIME_IN_MILLISECONDS) / Constants.UC_MILLISECONDS_IN_SECOND;
//                        Logger.d(TAG, "free falling "+airTime);
//                        Logger.d(TAG, "proj: "+projection);
//                        Logger.d(TAG, graMag+"");
                    } else if (airTime > AIR_TIME_NOISE_THRESHOLD) {
//                        Logger.d(TAG, "stop free fall");
                        if (mDataCallback != null)
                            mDataCallback.updateAirTime(airTime);
                        if (mUICallback != null)
                            mUICallback.updateAirTime(airTime);
                        airTime = 0.0;
                    }else{
                        airTime = 0.0;
                    }
                }
            });
        }
    }

    class TrackTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    duration += DURATION_UPDATE_INTERVAL_IN_SECONDS;
                    if (mDataCallback != null)
                        mDataCallback.updateDuration(duration);

                    if (mUICallback != null)
                        mUICallback.updateDuration(duration);
                }
            });
        }
    }

    public static interface MotionCallbacks {
        void updateAirTime(double airTime);
        void updateDuration(int duration);
    }
}

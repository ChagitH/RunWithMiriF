package com.forst.miri.runwithme.miscellaneous.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by chagithazani on 6/6/18.
 */

public class TemperatureSensor  {

    private SensorManager mSensorManager;
    private Sensor mTemperatureSensor;
    private SensorEventListener mSensorEventListener;
    private TemperatureListener mListener;


    public TemperatureSensor(Context context, TemperatureListener listener) {
        mListener = listener;
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        mTemperatureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float ambient_temperature = sensorEvent.values[0];
                Log.d(getClass().getName(), "+++++++++++++ + ++++++++++++++ onSensorChanged " + ambient_temperature);
                if(mListener != null) mListener.tempChanged(ambient_temperature);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        boolean hasTempSensor = mSensorManager.registerListener(mSensorEventListener, mTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(getClass().getName(), "+++++++++++++ + ++++++++++++++ DOES HAVE TEMP SENSOR?????  " + hasTempSensor);
    }

    public void destroyTemperatureSensor() {
        mListener = null;
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    public interface TemperatureListener{
        public void tempChanged(float temp);
    }
}


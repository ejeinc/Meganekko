/* Copyright 2015 eje inc.
 * Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eje_c.meganekko;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

class InternalSensorManager {

    private final long mAppPtr;
    private final SensorManager mSensorManager;
    private final Sensor mSensor;
    private final SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            setSensorValues(mAppPtr, event.values[0], event.values[1], event.values[2], event.values[3]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public InternalSensorManager(Context context, long appPtr) {
        this.mAppPtr = appPtr;

        if (mAppPtr == 0l)
            throw new IllegalArgumentException("You must call GVRActivity.setAppPtr fist.");

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
    }

    public void start() {
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void stop() {
        mSensorManager.unregisterListener(mSensorEventListener, mSensor);
    }

    private static native void setSensorValues(long appPtr, float x, float y, float z, float w);
}

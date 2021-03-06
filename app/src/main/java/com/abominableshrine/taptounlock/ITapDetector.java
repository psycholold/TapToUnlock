/*
 * Copyright 2015 Hannes Bibel, Valentin Sawadski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abominableshrine.taptounlock;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Interface tap detectors must implement to be plug-able to
 * {@link com.abominableshrine.taptounlock.TapPatternDetectorService}
 */
public interface ITapDetector extends SensorEventListener {
    /**
     * Notify the detector about sensor changes.
     * <p/>
     * This API gets the data from {@link #onSensorChanged(android.hardware.SensorEvent)} and has
     * been added for testing because it is not possible to feed specific data to a
     * {@link android.hardware.SensorEventListener} because it is not possible to create a
     * {@link android.hardware.SensorEvent} in user code.
     *
     * @param timestamp The timestamp of the event
     * @param senorType The type of sensor with new readings
     * @param accuracy  The accuracy of the reading
     * @param values    The readings from the sensor
     * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
     */
    public void onSensorChanged(long timestamp, int senorType, int accuracy, float values[]);

    /**
     * Notify the detector about accuracy changes
     *
     * @param sensorType The type of sensor with new readings
     * @param accuracy   The new accuracy of this sensor, one of
     *                   {@code SensorManager.SENSOR_STATUS_*}
     * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
     */
    public void onAccuracyChanged(int sensorType, int accuracy);

    /**
     * Register an observer to be notified on each detected tap
     *
     * @param o The observer
     */
    public void registerTapObserver(TapDetector.TapObserver o);

    /**
     * Remove a previously registered observer
     *
     * @param o The observer to remove
     */
    public void removeTapObserver(TapDetector.TapObserver o);

    /**
     * Subscribe to the Sensors
     *
     * @param sensorManager The sensor manager to get access to the sensors
     */
    public void subscribeToSensors(SensorManager sensorManager);

    /**
     * Unsubscribe from the Sensors
     *
     * @param sensorManager The sensor manager to get access to the sensors
     */
    public void unsubscribeFromSensors(SensorManager sensorManager);

    /**
     * A simple interface to be implemented by observers if they require Tap notifications
     */
    public interface TapObserver {
        /**
         * Callback when a Tap has been detected
         *
         * @param timestamp When the tap occured
         * @param now       The current time
         * @param side      The side the device has been tapped
         */
        public void onTap(long timestamp, long now, DeviceSide side);
    }
}

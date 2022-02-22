package com.myprog.sportislife.model

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.myprog.sportislife.listener.AccelerometerListener
import com.myprog.sportislife.model.interfaceModel.AccelerometerInterface
import kotlin.math.sqrt


class Accelerometer(context: Context) : SensorEventListener, AccelerometerInterface {
    private var listAccelerometerListener: MutableList<AccelerometerListener> = mutableListOf()
    private val sensorManager: SensorManager =
        (context.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
    private val accelerometer: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var magnitudePrevious = 10.0

    override fun start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun stop() {
        sensorManager.unregisterListener(this, accelerometer);
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val magnitude = sqrt(
                event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]
            ).toDouble()
            val magnitudeDelta = magnitude - magnitudePrevious
            magnitudePrevious = magnitude
            if (magnitudeDelta > 7.5) {
                onStep()
            }
        }
    }

    private fun onStep() {
        for(accelerometerListener in listAccelerometerListener) {
            accelerometerListener.onStep()
        }
    }

    override fun setListener(accelerometerListener: AccelerometerListener) {
        listAccelerometerListener.add(accelerometerListener)
    }

}
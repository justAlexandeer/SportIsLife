package com.myprog.sportislife.model.interfaceModel

import com.myprog.sportislife.listener.AccelerometerListener

interface AccelerometerInterface {
    fun start()
    fun stop()
    fun setListener(accelerometerListener: AccelerometerListener)
}
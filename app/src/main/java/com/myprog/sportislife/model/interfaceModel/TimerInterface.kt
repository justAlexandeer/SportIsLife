package com.myprog.sportislife.model.interfaceModel

import com.myprog.sportislife.listener.TimerListener

interface TimerInterface {
    fun start()
    fun stop()
    fun setListener(timerListener: TimerListener)
}
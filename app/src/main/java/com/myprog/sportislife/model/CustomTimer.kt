package com.myprog.sportislife.model

import com.myprog.sportislife.model.interfaceModel.TimerInterface
import com.myprog.sportislife.listener.TimerListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch

class CustomTimer: TimerInterface {
    private lateinit var timerJob: Job
    var listTimerListener: MutableList<TimerListener> = mutableListOf()

    override fun start() {
        val tickerChannel = ticker(1_000, 0)
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            for (event in tickerChannel) {
                onTick()
            }
        }
    }

    override fun stop() {
        timerJob.cancel()
    }

    private fun onTick() {
        for(timer in listTimerListener){
            timer.onTick()
        }
    }

    override fun setListener(timerListener: TimerListener) {
        listTimerListener.add(timerListener)
    }
}


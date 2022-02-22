package com.myprog.sportislife.listener

import com.myprog.sportislife.data.Training

interface TrainingListener {
    fun onClick(training: Training)
}
package com.myprog.sportislife.data

import android.util.Log
import androidx.lifecycle.LiveData

class TrainingRepository (private val trainingDatabaseDao: TrainingDatabaseDao) {

    fun getAllTraining(): LiveData<List<Training>> {
        return trainingDatabaseDao.getAllTraining()
    }

    suspend fun addTraining(training: Training): Long {
        return trainingDatabaseDao.addTraining(training)
    }

    suspend fun addCoordinate(coordinate: Coordinate): Long {
        return trainingDatabaseDao.addCoordinate(coordinate)
    }

    suspend fun getListCoordinate(idTraining: Long): List<Coordinate> {
        return trainingDatabaseDao.getListCoordinate(idTraining)
    }

    suspend fun updateTraining(training: Training) {
        trainingDatabaseDao.updateTraining(training)
    }

    suspend fun getTraining(trainingId: Long): Training{
        return trainingDatabaseDao.getTraining(trainingId)
    }

    suspend fun getListIntervalTraining(date: List<Long>): List<Training?> {
        val firstDate = date[0]
        val secondDate= date[1]
        return trainingDatabaseDao.getListIntervalTraining(firstDate, secondDate)
    }

}
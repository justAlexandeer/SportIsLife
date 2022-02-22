package com.myprog.sportislife.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprog.sportislife.data.TrainingRepository
import com.myprog.sportislife.data.TrainingDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StartTrainingViewModel(
    private val database: TrainingDatabaseDao,
    private val application: Application) : ViewModel() {

    private val startTrainingRepository = TrainingRepository(database)
    private var trainingNow = 0L

    init {
        Log.i("Sport", "startTrainingViewModel created")
    }

    fun onButtonClick() {
        /*val training = Training("test")
        GlobalScope.launch(Dispatchers.IO) {
            val id = startTrainingRepository.addTraining(training)
            trainingNow = id
            Log.i("Sport", "addTraining = $trainingNow")
        }*/
    }

    /*fun onButtonClickCoordinate() {
        val coordinate = Coordinate("test", trainingNow)
        GlobalScope.launch(Dispatchers.IO) {
            val id = startTrainingRepository.addCoordinate(coordinate)
            Log.i("Sport", id.toString())
        }
    }*/

    /*fun onButtonShow() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = startTrainingRepository.getlistCoordinate(trainingNow)
            for(coordinate in list) {
                Log.i("Sport", coordinate.coordinateId.toString())
            }
        }
    }*/

}
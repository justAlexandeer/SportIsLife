package com.myprog.sportislife.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprog.sportislife.data.TrainingRepository
import com.myprog.sportislife.data.Training
import com.myprog.sportislife.data.TrainingDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import androidx.lifecycle.MutableLiveData
import com.myprog.sportislife.data.Coordinate

class TrainingDetailViewModel(
    private val database: TrainingDatabaseDao,
    private val application: Application) : ViewModel() {

    private var trainingNow:MutableLiveData<Training> = MutableLiveData()
    private var listCoordinate: MutableLiveData<List<Coordinate>> = MutableLiveData()

    init {
        Log.i("Sport", "training detail created")
    }

    fun getTrainingAndCoordinate(trainingId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val training = async { database.getTraining(trainingId) }
                val allCoordinate = async { database.getListCoordinate(trainingId) }
                trainingNow.postValue(training.await())
                listCoordinate.postValue(allCoordinate.await())
            } catch (cause: Throwable){
                Log.i("Sport", cause.message.toString())
            }
        }
    }

    fun getTrainingNow(): LiveData<Training> {
        return trainingNow
    }

    fun getAllCoordinate(): LiveData<List<Coordinate>> {
        return listCoordinate
    }
}
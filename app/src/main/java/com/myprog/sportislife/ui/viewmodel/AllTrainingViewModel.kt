package com.myprog.sportislife.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.myprog.sportislife.data.Training
import com.myprog.sportislife.data.TrainingRepository
import com.myprog.sportislife.data.TrainingDatabaseDao

class AllTrainingViewModel(
    private val database: TrainingDatabaseDao,
    private val application: Application) : ViewModel() {

    private val repository = TrainingRepository(database)
    private var listAllTraining = repository.getAllTraining()

    init {
        Log.i("Sport", "allTrainingViewModel created")
    }

    fun getAllNote(): LiveData<List<Training>> {
        return listAllTraining
    }

}
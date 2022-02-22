package com.myprog.sportislife.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myprog.sportislife.data.TrainingDatabaseDao
import java.lang.IllegalArgumentException

class AllTrainingViewModelFactory (
    private val dataSource: TrainingDatabaseDao,
    private val application: Application): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AllTrainingViewModel::class.java)) {
            return AllTrainingViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
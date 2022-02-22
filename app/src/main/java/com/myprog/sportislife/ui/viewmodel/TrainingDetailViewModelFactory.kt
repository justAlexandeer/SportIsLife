package com.myprog.sportislife.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.myprog.sportislife.data.TrainingDatabaseDao
import com.myprog.sportislife.ui.view.TrainingDetail
import java.lang.IllegalArgumentException

class TrainingDetailViewModelFactory (
    private val dataSource: TrainingDatabaseDao,
    private val application: Application): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TrainingDetailViewModel::class.java)) {
            return TrainingDetailViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
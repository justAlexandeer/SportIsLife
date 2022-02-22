package com.myprog.sportislife.other

import android.app.Application
import com.myprog.sportislife.data.AppDatabase
import com.myprog.sportislife.data.TrainingDatabaseDao

object RepositoryFactory {

    fun getRepository(application: Application): TrainingDatabaseDao {
        return AppDatabase.getInstance(application).trainingDatabaseDao
    }

}
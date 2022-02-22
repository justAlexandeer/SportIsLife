package com.myprog.sportislife.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myprog.sportislife.data.Training
import com.myprog.sportislife.data.TrainingDatabaseDao
import com.myprog.sportislife.data.TrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TrainingStatisticsViewModel (
    private val database: TrainingDatabaseDao,
    private val application: Application) : ViewModel() {

    private val trainingRepository = TrainingRepository(database)
    private var dateNow: MutableLiveData<List<Long>> = MutableLiveData()
    private var listIntervalTraining: MutableLiveData<List<Training?>> = MutableLiveData()
    private lateinit var calendar: Calendar

    init {
        getDate()
    }

    fun getListIntervalTraining(date: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                listIntervalTraining.postValue(trainingRepository.getListIntervalTraining(date))
            } catch (cause: Throwable){
                Log.i("Sport", cause.message.toString())
            }
        }
    }

    private fun getDate() {
        val mutableList = mutableListOf<Long>()
        calendar = Calendar.getInstance()

        calendar.firstDayOfWeek = Calendar.MONDAY
        setFirstDayOfWeek()
        mutableList.add(calendar.timeInMillis)

        setLustDayOfWeek()
        mutableList.add(calendar.timeInMillis)

        dateNow.postValue(mutableList)

    }

    fun nextWeek() {
        val mutableList = mutableListOf<Long>()

        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        setFirstDayOfWeek()
        mutableList.add(calendar.timeInMillis)
        setLustDayOfWeek()
        mutableList.add(calendar.timeInMillis)
        dateNow.postValue(mutableList)
    }

    fun pastWeek() {
        val mutableList = mutableListOf<Long>()

        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        setFirstDayOfWeek()
        mutableList.add(calendar.timeInMillis)
        setLustDayOfWeek()
        mutableList.add(calendar.timeInMillis)
        dateNow.postValue(mutableList)
    }

    private fun setFirstDayOfWeek() {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
    }

    private fun setLustDayOfWeek() {
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
    }

    fun getDataNow(): LiveData<List<Long>> {
        return dateNow
    }

    fun getListIntervalTraining(): LiveData<List<Training?>> {
        return listIntervalTraining
    }

}
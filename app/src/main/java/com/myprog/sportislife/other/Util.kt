package com.myprog.sportislife.other

import android.annotation.SuppressLint
import android.util.Log
import com.myprog.sportislife.data.Training
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun convertLongToTimer(systemTime: Long): String {
    val time = SimpleDateFormat("HH:mm:ss")
    time.timeZone = java.util.TimeZone.getTimeZone("GMT")
    return time.format(systemTime).toString()
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("EEEE MMM-dd-yyyy' Время: 'HH:mm:ss")
        .format(systemTime).toString()
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString1(systemTime: Long): String {
    val time = SimpleDateFormat("H' ч 'mm' м 'ss' с'")
    time.timeZone = java.util.TimeZone.getTimeZone("GMT")
    return time.format(systemTime).toString()
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString2(systemTime: Long): String {
    val time = SimpleDateFormat("MMM-dd-yyyy' Время: 'HH:mm")
    time.timeZone = java.util.TimeZone.getTimeZone("GMT")
    return time.format(systemTime).toString()
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString3(systemTime: Long): String {
    val time = SimpleDateFormat("MMM-dd")
    time.timeZone = java.util.TimeZone.getTimeZone("GMT")
    return time.format(systemTime).toString()
}

fun getDistanceInDayOfWeek(allTraining: List<Training>): List<Double> {
    val trainingInDayOfWeek = mutableListOf<Double>(0.0,0.0,0.0,0.0,0.0,0.0,0.0)
    for(training in allTraining) {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = training.dateStart
        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> {
                trainingInDayOfWeek[0] += training.totalDistance
            }
            Calendar.TUESDAY -> {
                trainingInDayOfWeek[1] += training.totalDistance
            }
            Calendar.WEDNESDAY -> {
                trainingInDayOfWeek[2] += training.totalDistance
            }
            Calendar.THURSDAY -> {
                trainingInDayOfWeek[3] += training.totalDistance
            }
            Calendar.FRIDAY -> {
                trainingInDayOfWeek[4] += training.totalDistance
            }
            Calendar.SATURDAY -> {
                trainingInDayOfWeek[5] += training.totalDistance
            }
            Calendar.SUNDAY -> {
                trainingInDayOfWeek[6] += training.totalDistance
            }
        }
    }
    return trainingInDayOfWeek
}

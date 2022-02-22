package com.myprog.sportislife.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.myprog.sportislife.model.CustomTimer
import com.myprog.sportislife.R
import com.myprog.sportislife.model.interfaceModel.TimerInterface
import com.myprog.sportislife.data.Coordinate
import com.myprog.sportislife.data.Training
import com.myprog.sportislife.data.TrainingDatabaseDao
import com.myprog.sportislife.data.TrainingRepository
import com.myprog.sportislife.listener.AccelerometerListener
import com.myprog.sportislife.listener.TimerListener
import com.myprog.sportislife.model.Accelerometer
import com.myprog.sportislife.model.interfaceModel.AccelerometerInterface
import com.myprog.sportislife.other.Constants
import com.myprog.sportislife.other.Constants.WEIGHT
import com.myprog.sportislife.other.RepositoryFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Подумать про скорость в координате, звучит как рофл
// Подумать что с калориями
// Подумать как передать database(Инъекция зависимостей)
// LiveData не через компаньон
// Шаги
// Вес изменять

class TrackingService(): Service() {

    companion object {
        val time = MutableLiveData<Long>()
        val speed = MutableLiveData<Double>()
        val distance = MutableLiveData<Float>()
        val countStep = MutableLiveData<Int>(0)
        val countCalories = MutableLiveData(0.0)
        var isStartLive = MutableLiveData<Boolean>()
        var isStart = false
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var database: TrainingDatabaseDao
    private lateinit var trainingRepository: TrainingRepository
    private lateinit var timer: TimerInterface
    private lateinit var accelerometer: AccelerometerInterface

    private var timeAfterStart = 0
    private lateinit var trainingNow: Training
    private var totalDistance = 0.0f
    private var calories = 0.0
    private var allCoordinate: MutableList<Coordinate> = mutableListOf()

    override fun onCreate() {
        super.onCreate()

        database = RepositoryFactory.getRepository(application)
        trainingRepository = TrainingRepository(database)

        createNotificationChannel()
        val notification = createNotification()
        startForeground(Constants.NOTIFICATION_ID, notification)

        isStart = true
        isStartLive.value = true
        addTraining()
        startTracking()
        startTimer()
        startAccelerometer()
    }

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        lateinit var previousLocation: Location
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val intervalSharedPreferences = sharedPreferences.getString("preference_location_tracking_key", "3000") as String
        val interval = intervalSharedPreferences.toInt()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (timeAfterStart < Constants.TRACKING_DELAY) {
                    previousLocation = locationResult.locations[0]
                    timeAfterStart++
                    return
                }
                for (location in locationResult.locations) {
                    location?.let{
                        val speedKph = location.speed * 3.6;
                        val coordinate = Coordinate(speedKph, location.latitude, location.longitude, trainingNow.trainingId)
                        allCoordinate.add(coordinate)
                        totalDistance += location.distanceTo(previousLocation)/1000
                        calories = totalDistance * WEIGHT
                        addCoordinate(coordinate)

                        speed.value = speedKph
                        distance.value = totalDistance
                        countCalories.value = calories

                        previousLocation = location
                    }
                }
            }
        }

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = interval.toLong()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun startTimer() {
        var timeTmp = -1000L
        timer = CustomTimer()
        timer.setListener(object : TimerListener {
            override fun onTick() {
                timeTmp += 1000L
                time.value = timeTmp
            }
        })
        timer.start()
    }

    private fun startAccelerometer() {
        var steps = 0
        accelerometer = Accelerometer(this)
        accelerometer.start()
        accelerometer.setListener(object: AccelerometerListener {
            override fun onStep() {
                steps++
                countStep.value = steps
            }
        })
    }

    private fun addTraining() {
        trainingNow = Training(System.currentTimeMillis(), 0, 0,0.0,
            0.0,0.0,0.0,0)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val id = trainingRepository.addTraining(trainingNow)
                trainingNow.trainingId = id
            } catch (cause: Throwable){
                Log.i("Sport", cause.message.toString())
            }
        }
    }

    private fun updateTraining() {
        trainingNow.dateEnd = System.currentTimeMillis()
        trainingNow.countStep = countStep.value!!
        trainingNow.totalDistance = totalDistance.toDouble()
        trainingNow.calories = calories
        trainingNow.totalTime = trainingNow.dateEnd - trainingNow.dateStart

        if(allCoordinate.size > 5) {
            var maxSpeed = 0.0
            var averageSpeed = 0.0
            for (i in 0 until allCoordinate.size - 5) {
                val maxSpeedTmp =
                    (allCoordinate[i].speed + allCoordinate[i + 1].speed
                            + allCoordinate[i + 2].speed + allCoordinate[i + 3].speed
                            + allCoordinate[i + 4].speed)
                if (maxSpeedTmp - maxSpeed > 0.001) {
                    maxSpeed = maxSpeedTmp
                }
            }
            for(coordinate in allCoordinate) {
                averageSpeed += coordinate.speed
            }
            trainingNow.maxSpeed = maxSpeed / 5
            trainingNow.averageSpeed = (averageSpeed / allCoordinate.size)

        } else {
            trainingNow.averageSpeed = 0.0
            trainingNow.maxSpeed = 0.0
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                trainingRepository.updateTraining(trainingNow)
            } catch (cause: Throwable){
                Log.i("Sport", cause.message.toString())
            }
        }
    }

    private fun addCoordinate(coordinate: Coordinate) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val i = trainingRepository.addCoordinate(coordinate)
                Log.i("Sport", "coordinateAdd = $i")
            } catch (cause: Throwable){
                Log.i("Sport", cause.message.toString())
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_TRACKING_ID,
                Constants.NOTIFICATION_CHANNEL_TRACKING_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_TRACKING_ID)
            .setContentTitle(resources.getString(R.string.tracking_service_notification_name))
            .setSmallIcon(R.drawable.ic_stat_name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun resetUI() {
        isStart = false
        isStartLive.value = false
        countCalories.value = 0.0
        distance.value = 0f
        time.value = 0
        speed.value = 0.0
    }

    override fun onBind(p0: Intent?) = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        timer.stop()
        accelerometer.stop()
        resetUI()
        updateTraining()
    }
}
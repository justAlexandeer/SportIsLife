package com.myprog.sportislife.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myprog.sportislife.R
import com.myprog.sportislife.data.AppDatabase
import com.myprog.sportislife.services.TrackingService
import com.myprog.sportislife.ui.viewmodel.StartTrainingViewModel
import com.myprog.sportislife.ui.viewmodel.StartTrainingViewModelFactory
import com.myprog.sportislife.other.*

// Сделать парсер строк результатов

class StartTrainingFragment : Fragment(R.layout.fragment_start_training) {

    private lateinit var startTrainingViewModel: StartTrainingViewModel
    private lateinit var buttonServices: Button
    private lateinit var textTimer: TextView
    private lateinit var textSpeed: TextView
    private lateinit var textDistance: TextView
    private lateinit var textCalories: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataSource = AppDatabase.getInstance(requireActivity().application).trainingDatabaseDao
        val viewModelFactory = StartTrainingViewModelFactory(dataSource, requireActivity().application)
        startTrainingViewModel = ViewModelProvider(this, viewModelFactory).get(StartTrainingViewModel::class.java)

        buttonServices = view.findViewById(R.id.start_training_button_start_services)
        textTimer = view.findViewById(R.id.start_training_timer)
        textSpeed = view.findViewById(R.id.start_training_speed)
        textDistance= view.findViewById(R.id.start_training_distance)
        textCalories = view.findViewById(R.id.start_training_calories)

        updateTextUI()

        buttonServices.setOnClickListener {
            val serviceIntent = Intent(context, TrackingService::class.java)
            if (!TrackingService.isStart) {
                requireContext().startService(serviceIntent)
            }
            else {
                requireContext().stopService(serviceIntent)
            }
        }

        TrackingService.time.observe(viewLifecycleOwner, Observer {
            textTimer.text = convertLongToTimer(it)
        })
        TrackingService.distance.observe(viewLifecycleOwner, Observer {
            if(it != 0f) {
                val distance = String.format(resources.getString(R.string.start_training_text_distance), it.toString().substring(0,5))
                textDistance.text = distance
            } else {
                val distance = String.format(resources.getString(R.string.start_training_text_distance), it.toString().plus("00"))
                textDistance.text = distance
            }
        })
        TrackingService.speed.observe(viewLifecycleOwner, Observer {
            if(it != 0.0) {
                val speed = String.format(resources.getString(R.string.start_training_text_speed), it.toString().substring(0,3))
                textSpeed.text = speed
            } else {
                val speed = String.format(resources.getString(R.string.start_training_text_speed), it.toString())
                textSpeed.text = speed
            }
        })
        TrackingService.countCalories.observe(viewLifecycleOwner, Observer {
            if(it != 0.0) {
                val calories = String.format(resources.getString(R.string.start_training_text_calories), it.toString().substring(0,5))
                textCalories.text = calories
            } else {
                val calories = String.format(resources.getString(R.string.start_training_text_calories), it.toString().plus("00"))
                textCalories.text = calories
            }
        })
        TrackingService.isStartLive.observe(viewLifecycleOwner, Observer {
            if(it){
                buttonServices.text = (resources.getString(R.string.start_training_button_end_training))
            } else {
                buttonServices.text = (resources.getString(R.string.start_training_button_start_training))
            }
        })


    }

    private fun updateTextUI() {
        textTimer.text = convertLongToTimer(0L)
        textSpeed.text = "0.0 км/ч"
        textDistance.text = "0.000 км"
        textCalories.text = "0.000 ккал"
    }

}

package com.myprog.sportislife.ui.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.myprog.sportislife.R
import com.myprog.sportislife.data.AppDatabase
import com.myprog.sportislife.data.Coordinate
import com.myprog.sportislife.data.Training
import com.myprog.sportislife.ui.fragment.WorkaroundMapFragment
import com.myprog.sportislife.ui.viewmodel.TrainingDetailViewModel
import com.myprog.sportislife.ui.viewmodel.TrainingDetailViewModelFactory
import com.myprog.sportislife.other.*

// Привязка данных
// Нормально отображние данных

class TrainingDetail : Fragment(R.layout.fragment_trainig_detail) {

    private lateinit var trainingDetailViewModel: TrainingDetailViewModel
    private val args: TrainingDetailArgs by navArgs()
    private lateinit var mapFragment: WorkaroundMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var scrollView: ScrollView

    private lateinit var viewTrainingTotalTime: TextView
    private lateinit var viewTrainingStartTime: TextView
    private lateinit var viewTrainingEndTime: TextView
    private lateinit var viewTrainingTotalDistance: TextView
    private lateinit var viewTrainingCountSteps: TextView
    private lateinit var viewTrainingAverageSpeed: TextView
    private lateinit var viewTrainingMaxSpeed: TextView
    private lateinit var viewTrainingCountCalories: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataSource = AppDatabase.getInstance(requireActivity().application).trainingDatabaseDao
        val viewModelFactory = TrainingDetailViewModelFactory(dataSource, requireActivity().application)
        trainingDetailViewModel = ViewModelProvider(this, viewModelFactory).get(TrainingDetailViewModel::class.java)

        mapFragment = childFragmentManager.findFragmentById((R.id.training_detail_map)) as WorkaroundMapFragment
        scrollView = view.findViewById(R.id.training_detail_scrollview)

        viewTrainingTotalTime = view.findViewById(R.id.training_detail_total_time)
        viewTrainingStartTime = view.findViewById(R.id.training_detail_start_time)
        viewTrainingEndTime = view.findViewById(R.id.training_detail_end_time)
        viewTrainingTotalDistance = view.findViewById(R.id.training_detail_total_distance)
        viewTrainingCountSteps = view.findViewById(R.id.training_detail_steps)
        viewTrainingAverageSpeed = view.findViewById(R.id.training_detail_average_speed)
        viewTrainingMaxSpeed = view.findViewById(R.id.training_detail_max_speed)
        viewTrainingCountCalories = view.findViewById(R.id.training_detail_calories)

        trainingDetailViewModel.getTrainingAndCoordinate(args.trainingId)

        trainingDetailViewModel.getTrainingNow().observe(viewLifecycleOwner, Observer {
            updateText(it)
        })


        initMap()
    }

    private fun updateText(training: Training) {
        viewTrainingTotalTime.text = String.format(resources.getString(R.string.training_detail_total_time),
            convertLongToDateString1(training.totalTime))
        viewTrainingStartTime.text = String.format(resources.getString(R.string.training_detail_start_time),
            convertLongToDateString2(training.dateStart))
        viewTrainingEndTime.text = String.format(resources.getString(R.string.training_detail_end_time),
            convertLongToDateString2(training.dateEnd))

        if (training.totalDistance.toString().length > 3) {
            viewTrainingTotalDistance.text = String.format(resources.getString(R.string.training_detail_total_distance),
                training.totalDistance.toString().substring(0,4))
        } else {
            viewTrainingTotalDistance.text = String.format(resources.getString(R.string.training_detail_total_distance),
                training.totalDistance.toString())
        }

        viewTrainingCountSteps.text = String.format(resources.getString(R.string.training_detail_steps),
            training.countStep.toString())

        if (training.averageSpeed.toString().length > 3) {
            viewTrainingAverageSpeed.text = String.format(resources.getString(R.string.training_detail_average_speed),
                training.averageSpeed.toString().substring(0,4))
        } else {
            viewTrainingAverageSpeed.text = String.format(resources.getString(R.string.training_detail_average_speed),
                training.averageSpeed.toString())
        }

        if (training.maxSpeed.toString().length > 3) {
            viewTrainingMaxSpeed.text = String.format(resources.getString(R.string.training_detail_max_speed),
                training.maxSpeed.toString().substring(0,4))
        } else {
            viewTrainingMaxSpeed.text = String.format(resources.getString(R.string.training_detail_max_speed),
                training.maxSpeed.toString())
        }

        if (training.calories.toString().length > 3) {
            viewTrainingCountCalories.text = String.format(resources.getString(R.string.training_detail_calories),
                training.calories.toString().substring(0,5))
        } else {
            viewTrainingCountCalories.text = String.format(resources.getString(R.string.training_detail_calories),
                training.calories.toString())
        }

    }

    private fun initMap() {
            mapFragment.getMapAsync { googleMap ->
                mMap = googleMap
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                mMap.uiSettings.isZoomControlsEnabled = true
                mapFragment.setListener(object : WorkaroundMapFragment.OnTouchListener {
                    override fun onTouch() {
                        scrollView.requestDisallowInterceptTouchEvent(true)
                    }
                })
                trainingDetailViewModel.getAllCoordinate().observe(viewLifecycleOwner, Observer{
                    updateMap(it)
                })
            }
    }

    private fun updateMap(allCoordinate: List<Coordinate>) {
        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.RED)
        if (allCoordinate.isNotEmpty()) {
            for(coordinate in allCoordinate){
                polylineOptions.add(LatLng(coordinate.latitude, coordinate.longitude))
            }
            mMap.addPolyline(polylineOptions)
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        allCoordinate[0].latitude,
                        allCoordinate[0].longitude), 16f
                )
            )
        }
    }
}
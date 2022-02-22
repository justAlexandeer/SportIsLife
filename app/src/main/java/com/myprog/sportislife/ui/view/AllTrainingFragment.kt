package com.myprog.sportislife.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myprog.sportislife.R
import com.myprog.sportislife.adapter.TrainingListAdapter
import com.myprog.sportislife.data.AppDatabase
import com.myprog.sportislife.data.Training
import com.myprog.sportislife.listener.TrainingListener
import com.myprog.sportislife.ui.viewmodel.AllTrainingViewModel
import com.myprog.sportislife.ui.viewmodel.AllTrainingViewModelFactory
import com.myprog.sportislife.ui.viewmodel.TrainingStatisticsViewModelFactory

class AllTrainingFragment : Fragment(R.layout.fragment_all_training) {

    private lateinit var allTrainingViewModel: AllTrainingViewModel
    private lateinit var allTrainingRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataSource = AppDatabase.getInstance(requireActivity().application).trainingDatabaseDao
        val viewModelFactory = AllTrainingViewModelFactory(dataSource, requireActivity().application)
        allTrainingViewModel = ViewModelProvider(this, viewModelFactory).get(AllTrainingViewModel::class.java)

        val adapter = TrainingListAdapter()
        adapter.setTrainingListener(object: TrainingListener{
            override fun onClick(training: Training) {
                val action = AllTrainingFragmentDirections.actionAllTrainingFragmentToTrainingDetailFragment(training.trainingId)
                findNavController().navigate(action)
            }
        })

        allTrainingRecyclerView = view.findViewById(R.id.all_training_recycler_view)
        allTrainingRecyclerView.layoutManager = LinearLayoutManager(context)
        allTrainingRecyclerView.adapter = adapter

        allTrainingViewModel.getAllNote().observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

    }

}
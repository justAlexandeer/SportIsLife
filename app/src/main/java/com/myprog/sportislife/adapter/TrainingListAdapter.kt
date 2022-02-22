package com.myprog.sportislife.adapter

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myprog.sportislife.R
import com.myprog.sportislife.data.Training
import com.myprog.sportislife.listener.TrainingListener
import com.myprog.sportislife.other.*

class TrainingListAdapter():
    ListAdapter<Training, TrainingListAdapter.ViewHolder>(TrainingDiffUtil()){

    private var trainingListener: TrainingListener? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate: TextView = itemView.findViewById(R.id.training_cell_date_training)
        val textTime: TextView = itemView.findViewById(R.id.training_cell_time_training)
        val textDistance: TextView = itemView.findViewById(R.id.training_cell_distance_training)
        val textSteps: TextView = itemView.findViewById(R.id.training_cell_steps_training)

        fun onBind(holder: ViewHolder, training: Training, trainingListener: TrainingListener?){
            val context = holder.itemView.context

            textDate.text = convertLongToDateString(training.dateStart)
            textTime.text = String.format(context.getString(R.string.training_list_adapter_date),
                convertLongToTimer(training.totalTime))
            if (training.totalDistance.toString().length > 3) {
                textDistance.text = String.format(context.getString(R.string.training_list_adapter_distance),
                    training.totalDistance.toString().substring(0,4))
            } else {
                textDistance.text = String.format(context.getString(R.string.training_list_adapter_distance),
                    training.totalDistance.toString())
            }
            textSteps.text = String.format(context.getString(R.string.training_list_adapter_steps),
                training.countStep)

            itemView.setOnClickListener {
                trainingListener?.onClick(training)
            }
        }

        companion object{
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.training_cell, parent, false)
                return ViewHolder(view)
            }
        }
    }

    fun setTrainingListener(trainingListener: TrainingListener) {
        this.trainingListener = trainingListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.onBind(holder, note, this.trainingListener)
    }
}

class TrainingDiffUtil: DiffUtil.ItemCallback<Training>() {
    override fun areItemsTheSame(oldTraining: Training, newTraining: Training): Boolean {
        return oldTraining.trainingId == newTraining.trainingId
    }

    override fun areContentsTheSame(oldTraining: Training, newTraining: Training): Boolean {
        return oldTraining == newTraining
    }
}
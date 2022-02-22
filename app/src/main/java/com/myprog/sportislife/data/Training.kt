package com.myprog.sportislife.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "training_table")

data class Training (

    var dateStart: Long,
    var dateEnd: Long,
    var countStep: Int,
    var averageSpeed: Double,
    var maxSpeed: Double,
    var totalDistance: Double,
    var calories: Double,
    var totalTime: Long,

    @PrimaryKey(autoGenerate = true)
    var trainingId: Long = 0L

)
package com.myprog.sportislife.data;

import androidx.room.Entity;
import androidx.room.ForeignKey
import androidx.room.PrimaryKey;

@Entity(tableName = "coordinate_training",
        foreignKeys = [ForeignKey(entity = Training::class, parentColumns = ["trainingId"], childColumns = ["trainingId"], onDelete = ForeignKey.CASCADE)])
data class Coordinate (
        val speed: Double,
        val latitude: Double,
        val longitude: Double,
        val trainingId: Long,

        @PrimaryKey(autoGenerate = true)
        val coordinateId: Long = 0L
)


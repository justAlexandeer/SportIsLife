package com.myprog.sportislife.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TrainingDatabaseDao {

    @Insert
    suspend fun addTraining(training: Training): Long

    @Insert
    suspend fun addCoordinate(coordinate: Coordinate): Long

    @Query("SELECT * FROM coordinate_training WHERE trainingId = :trainingId ")
    suspend fun getListCoordinate(trainingId: Long): List<Coordinate>

    @Query("SELECT * FROM training_table WHERE (dateEnd IS NOT 0) ORDER BY trainingId DESC")
    fun getAllTraining(): LiveData<List<Training>>

    @Update
    suspend fun updateTraining(training: Training)

    @Query("SELECT * FROM training_table WHERE trainingId = :trainingId")
    fun getTraining(trainingId: Long): Training

    @Query("SELECT * FROM training_table WHERE (dateStart >= :dateFirst AND dateStart <= :dateSecond and dateEnd IS NOT 0)")
    fun getListIntervalTraining(dateFirst: Long, dateSecond: Long): List<Training?>

}
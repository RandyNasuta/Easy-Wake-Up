package com.example.easywakeup.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.example.easywakeup.data.model.entity.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarms ORDER BY time ASC")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Query("SELECT * FROM alarms WHERE id = :id")
    fun getAlarmById(id: Long): Flow<Alarm?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createAlarm(alarm: Alarm)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAlarmStatus(alarm: Alarm)
}
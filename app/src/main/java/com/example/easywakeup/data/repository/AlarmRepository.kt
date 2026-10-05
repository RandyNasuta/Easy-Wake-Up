package com.example.easywakeup.data.repository

import com.example.easywakeup.data.local.AlarmDao
import com.example.easywakeup.data.model.entity.Alarm
import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val alarmDao: AlarmDao) {
    suspend fun create(alarm: Alarm) {
        alarmDao.createAlarm(alarm)
    }

    fun getAlarmById(id: Long): Flow<Alarm?> {
        return alarmDao.getAlarmById(id)
    }

    val allAlarms: Flow<List<Alarm>> = alarmDao.getAllAlarms()
}
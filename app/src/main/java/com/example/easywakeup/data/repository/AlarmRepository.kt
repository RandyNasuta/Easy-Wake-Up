package com.example.easywakeup.data.repository

import com.example.easywakeup.data.local.AlarmDao
import com.example.easywakeup.data.model.entity.Alarm

class AlarmRepository(private val alarmDao: AlarmDao) {
    suspend fun create(alarm: Alarm) {
        alarmDao.createAlarm(alarm)
    }
}
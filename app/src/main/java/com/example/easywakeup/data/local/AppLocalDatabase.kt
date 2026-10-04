package com.example.easywakeup.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.example.easywakeup.data.model.entity.Alarm

@Database(entities = [Alarm::class], version = 1, exportSchema = false)
abstract class AppLocalDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}
package com.example.easywakeup.di

import android.content.Context
import androidx.room3.Room
import com.example.easywakeup.data.local.AlarmDao
import com.example.easywakeup.data.local.AppLocalDatabase
import com.example.easywakeup.data.repository.AlarmRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppLocalDatabase {
        return Room.databaseBuilder(
            context,
            AppLocalDatabase::class.java,
            "alarm_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAlarmDao(database: AppLocalDatabase): AlarmDao {
        return database.alarmDao()
    }

    @Provides
    @Singleton
    fun provideAlarmRepository(alarmDao: AlarmDao): AlarmRepository {
        return AlarmRepository(alarmDao)
    }
}
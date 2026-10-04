package com.example.easywakeup.data.model.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val time: String,
    val methodList: String
)

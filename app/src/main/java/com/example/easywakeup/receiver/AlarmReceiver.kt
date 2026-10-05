package com.example.easywakeup.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.easywakeup.ui.ring.AlarmRingActivity
import kotlin.jvm.java

class AlarmReceiver : BroadcastReceiver() {
    private val TAG = "AlarmReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmId = intent?.getLongExtra("EXTRA_ALARM_ID", -1L)
        val sound = intent?.getStringExtra("EXTRA_ALARM_SOUND") ?: "Sound 1"
        val methods = intent?.getStringExtra("EXTRA_ALARM_METHODS") ?: ""

        Log.d(TAG, "Alarm berdering! ID: $alarmId")

        val ringIntent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarmId)
            putExtra("EXTRA_ALARM_SOUND", sound)
            putExtra("EXTRA_ALARM_METHODS", methods)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context?.startActivity(ringIntent)
    }
}
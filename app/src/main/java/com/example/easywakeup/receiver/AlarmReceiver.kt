package com.example.easywakeup.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.easywakeup.R
import com.example.easywakeup.ui.ring.AlarmRingActivity
import kotlin.jvm.java

class AlarmReceiver : BroadcastReceiver() {
    private val TAG = "AlarmReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("EXTRA_ALARM_ID", -1L)
        val sound = intent.getStringExtra("EXTRA_ALARM_SOUND") ?: "Sound 1"
        val methods = intent.getStringExtra("EXTRA_ALARM_METHODS") ?: ""

        Log.d(TAG, "Alarm berdering! ID: $alarmId")

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "EasyWakeUp::AlarmWakeLock"
        )
        wakeLock.acquire(5000)

        val ringIntent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarmId)
            putExtra("EXTRA_ALARM_SOUND", sound)
            putExtra("EXTRA_ALARM_METHODS", methods)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            ringIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "EASYWAKEUP_ALARM_CHANNEL_V2"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Waktunya Bangun!")
            .setContentText("Selesaikan tantangan untuk mematikan alarm.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)

        notificationManager.notify(alarmId.toInt(), notificationBuilder.build())
    }
}
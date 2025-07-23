package com.ghanshyam.chronosapp.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object NotificationHelper {
  const val CHANNEL_ID = "chronos_reminders"
  private const val CHANNEL_NAME = "Reminders"
  private const val CHANNEL_DESC = "Reminder alerts"

  fun createChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
      ).apply { description = CHANNEL_DESC }
      val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      mgr.createNotificationChannel(channel)
    }
  }

  fun scheduleAlarm(
    context: Context,
    timeMillis: Long,
    title: String,
    description: String
  ) {
    val intent = Intent(context, NotificationReceiver::class.java).apply {
      putExtra("title", title)
      putExtra("description", description)
    }
    val pending = PendingIntent.getBroadcast(
      context,
      timeMillis.hashCode(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pending)
  }
}

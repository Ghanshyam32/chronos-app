package com.ghanshyam.chronosapp.reminder

import android.Manifest
import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver: BroadcastReceiver() {
  @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
  override fun onReceive(context: Context, intent: Intent) {
    val title = intent.getStringExtra("title") ?: "Reminder"
    val desc  = intent.getStringExtra("description") ?: ""

    NotificationHelper.createChannel(context)

    val notif = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_dialog_info)
      .setContentTitle(title)
      .setContentText(desc)
      .setAutoCancel(true)
      .build()

    NotificationManagerCompat.from(context)
      .notify(title.hashCode(), notif)
  }
}

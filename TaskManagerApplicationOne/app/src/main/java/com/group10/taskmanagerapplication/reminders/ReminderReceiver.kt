package com.group10.taskmanagerapplication.reminders

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.drawerlayout.R

const val notificationID = 2
const val channelID = "channel1"

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent)
    {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.notification_bg_normal_pressed)
            .setContentTitle(intent.getStringExtra("titleExtra"))
            .setContentText(intent.getStringExtra("messageExtra"))
            .build()


        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }

}
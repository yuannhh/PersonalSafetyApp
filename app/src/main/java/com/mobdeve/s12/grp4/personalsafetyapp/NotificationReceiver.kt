package com.mobdeve.s12.grp4.personalsafetyapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "notification_channel"
        private const val NOTIFICATION_ID = 1 // Unique ID for notifications
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationText = intent.getStringExtra("notificationText") ?: "Default Notification"
        createNotificationChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
            .setContentTitle("Check-In Reminder")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Automatically remove notification when tapped
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Channel"
            val descriptionText = "Channel description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

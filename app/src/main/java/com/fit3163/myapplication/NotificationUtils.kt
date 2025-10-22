package com.fit3163.myapplication

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationUtils {
    private const val CHANNEL_ID = "budget_warning_channel"

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showBudgetNotification(context: Context, title: String, message: String) {
        // Create the notification channel (only once)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Budget Warnings",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when budgets are close to or exceed limits"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Display it
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
    }
}

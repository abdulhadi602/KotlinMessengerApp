package com.example.kotlinmessenger.Utils

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.Utils.Notification.Companion.NOTIFICATION_CAHNNEL_ID

class InitialNotificationService : Service() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val notification: android.app.Notification = android.app.Notification.Builder(this,NOTIFICATION_CAHNNEL_ID)
                .setContentTitle("Intial Service")
                .setContentText("Test")
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.drawable.sendicon)
                .build()
            startForeground(1, notification)
            stopSelf()

        }

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

}
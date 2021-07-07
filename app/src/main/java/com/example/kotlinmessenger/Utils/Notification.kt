package com.example.kotlinmessenger.Utils
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kotlinmessenger.Activities.Welcome.WelcomeScreen
import com.example.kotlinmessenger.Data.IDs
import com.example.kotlinmessenger.Data.UserData
import com.example.kotlinmessenger.Data.Users
import com.example.kotlinmessenger.R
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.google.gson.JsonElement

class Notification(var con: Context)  {


    companion object{
        var notificationID : Int = 0
        lateinit var con : Context
        lateinit var notificationManager: NotificationManager
        val NOTIFICATION_CAHNNEL_ID = "CHANNEL_1"
    }

    init {
        this.con = con
            createNotificationChannel()
            Firebaseref.init()
        con.startForegroundService(Intent(con.applicationContext,NotificationService::class.java))


    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CAHNNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            notificationManager = con.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }



}
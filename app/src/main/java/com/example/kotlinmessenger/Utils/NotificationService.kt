package com.example.kotlinmessenger.Utils

import android.app.Notification.PRIORITY_MIN
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.kotlinmessenger.Activities.Welcome.WelcomeScreen
import com.example.kotlinmessenger.Data.IDs
import com.example.kotlinmessenger.Data.UserData
import com.example.kotlinmessenger.Data.Users
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.Utils.Notification.Companion.NOTIFICATION_CAHNNEL_ID
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonElement
import java.lang.UnsupportedOperationException

class NotificationService : Service() {
lateinit var UserId : String
    private fun NotificationListener(){

        UserId =IDs.sharedPreferences.getString("UserId","")!!
        Firebaseref.database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                DoNotification(dataSnapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseError", "Failed to read value.", error.toException())
            }

        })
    }
    private fun DoNotification(dataSnapshot: DataSnapshot) {
        val users = dataSnapshot.child("users").children
        for (d in users) {
            var user = Users(d.key.toString(), null)

            val map: HashMap<String, String> = d.value as HashMap<String, String>
            val gson = Gson()
            val jsonElement: JsonElement = gson.toJsonTree(map)
            user.userdata = gson.fromJson(jsonElement, UserData::class.java)
            val temp = user.userdata
            var PendingNotificationsPerUser = 0
            if (temp != null && !temp.UserId.equals(IDs.UserId)) {
                val Log = dataSnapshot.child("/user-messages/${IDs.UserId}/${user.ID}").children
                var chatMessagesNotifcationUpdate : HashMap<String, Any> = HashMap()
                for (chatMessage in Log) {
                    val map = chatMessage.value as HashMap<String, Any>
                    if (map.get("messageRead") == false) {
                        temp.UnreadMessages += 1
                        if (map.get("notificationReceived") == false) {
                            PendingNotificationsPerUser += 1
                            map.set("notificationReceived", true)
                            chatMessagesNotifcationUpdate.put(chatMessage.key.toString(), map)
                        }
                    }
                }
                if(PendingNotificationsPerUser>0){
                    sendNotification(
                        "Messages",
                        "${PendingNotificationsPerUser} new message(s) from ${temp.Name}"
                    )
                    Firebaseref.database.child("/user-messages/${IDs.UserId}/${user.ID}")
                        .updateChildren(
                            chatMessagesNotifcationUpdate
                        ).addOnSuccessListener {

                        }
                }

            }
        }

    }
    private fun sendNotification(title: String, Body: String) {
        val intent = Intent(this, WelcomeScreen::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CAHNNEL_ID)
                .setSmallIcon(R.drawable.sendicon)
                .setContentTitle(title)
                .setContentText(Body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)

        /** with(NotificationManagerCompat.from(this)){
        notify(notificationID, notificationBuilder.build())
        }**/
        Notification.notificationManager.notify(
            Notification.notificationID /* ID of notification */,
            notificationBuilder.build()
        )
        Notification.notificationID++
    }





    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val notification: android.app.Notification = android.app.Notification.Builder(this,NOTIFICATION_CAHNNEL_ID)
                .setContentTitle("Example Service")
                .setContentText("input")
                .setPriority(PRIORITY_MIN)
                .setSmallIcon(R.drawable.sendicon)
                .build()
            startForeground(1, notification)
        }

        NotificationListener()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}
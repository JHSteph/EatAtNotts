package com.example.eatatnotts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.database.*

class NotificationService : Service() {//Gives notifications when changes applied to News and home page

    private lateinit var dbref: DatabaseReference
    private val channelID = "com.example.eatatnotts.channel1"
    private var notificationManager: NotificationManager? = null

    override fun onCreate() {//Initialization, and detect changes from Firebase
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelID, "DemoChannel", "This is a demo")

        dbref = FirebaseDatabase.getInstance().getReference("Activity")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    displayNotification()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    override fun onBind(intent: Intent?): IBinder? {//Runs codes independently from other codes
        return null
    }

    private fun createNotificationChannel(id: String, name: String, channelDescription: String) {//Creates a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun displayNotification() {//Display notification to users
        val notificationId = 45
        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("News")
            .setContentText("There is a new news!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager?.notify(notificationId, notification)
    }
}
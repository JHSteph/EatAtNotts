package com.example.eatatnotts.CustomerOrder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CustomerOrderPickUpNotifications : Service() {//Gives notifications when changes applied to Customer Pick Up Order

    private lateinit var dbref: DatabaseReference
    private lateinit var dbrefDeliveryPending:DatabaseReference
    private val channelID = "com.example.eatatnotts.CustomerOrder.channel2"
    private var notificationManager: NotificationManager? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate() {//Initialization, and detect changes from Firebase
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelID, "PickUpChannel", "There is A New Pick Up Order!")
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        dbref = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbref.orderByChild("uid").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        // Get the user type from the snapshot
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        dbrefDeliveryPending = FirebaseDatabase.getInstance().getReference("${userName} Pick Up")
                        dbrefDeliveryPending.addValueEventListener(object : ValueEventListener {
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
                }
            }override fun onCancelled(error: DatabaseError) {
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
            .setContentTitle("Pick Up Orders")
            .setContentText("You have a new pick up order!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager?.notify(notificationId, notification)
    }
}
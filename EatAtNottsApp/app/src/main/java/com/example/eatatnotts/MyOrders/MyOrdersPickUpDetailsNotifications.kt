package com.example.eatatnotts.MyOrders

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

class MyOrdersPickUpDetailsNotifications : Service() {//Gives notifications when changes applied to My Pick Up Order Details

    private lateinit var dbref: DatabaseReference
    private lateinit var dbrefDeliveryPending: DatabaseReference
    private val channelID = "com.example.eatatnotts.MyOrders.channel2"
    private var notificationManager: NotificationManager? = null
    private lateinit var auth: FirebaseAuth
    private var receiptNo: String? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(channelID, "MyOrdersPickUpDetailsChannel", "There is A New Status for Pick Up Order!")
        auth = FirebaseAuth.getInstance()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {//Detect changes from Firebase
        receiptNo = intent?.getStringExtra("receipt")
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
                        dbrefDeliveryPending = FirebaseDatabase.getInstance().getReference("${userName} Order Pick Up ${receiptNo}")
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
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })

        return START_STICKY
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

    private fun displayNotification() {//Display Notification
        val notificationId = 45
        val notification = NotificationCompat.Builder(this, channelID)
            .setContentTitle("Pick Up Order Details")
            .setContentText("Your Pick Up Order Status has been updated!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager?.notify(notificationId, notification)
    }
}

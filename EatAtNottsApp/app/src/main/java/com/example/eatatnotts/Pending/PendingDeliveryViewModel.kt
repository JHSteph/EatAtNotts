package com.example.eatatnotts.Pending

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eatatnotts.NotificationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PendingDeliveryViewModel(application: Application) : AndroidViewModel(application) {//Handles Logic for PendingDelivery

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _customerOrders = MutableLiveData<List<PendingDeliveryItem>>()
    val customerOrders: LiveData<List<PendingDeliveryItem>> get() = _customerOrders
    private var previousCount: Int = 0
    private val _emptyOrder = MutableLiveData<Boolean>()
    val emptyOrder: LiveData<Boolean> get() = _emptyOrder

    fun fetchUserData() {//Get User Data
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbRef.child("Users").orderByChild("uid").equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        getUserData(userName, email)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
            }
        })
    }

    private fun getUserData(userName: String?, email: String?) {//Get Pending Order Data from Firebase
        val userRef = dbRef.child("${userName} Delivery Pending")
        _emptyOrder.value=false
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                    val intent = Intent(getApplication(), PendingDeliveryNotification::class.java)
                    getApplication<Application>().startService(intent)
                val ordersList = mutableListOf<PendingDeliveryItem>()
                if (snapshot.exists()) {
                    for (orderSnapshot in snapshot.children) {
                        _emptyOrder.value=false
                        val order = orderSnapshot.getValue(PendingDeliveryItem::class.java)
                        ordersList.add(order!!)
                    }
                    _customerOrders.value = ordersList
                }else{
                    _emptyOrder.value=true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    fun extractEmailBeforeDot(email: String): String {//Extract username from email before @
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex + email.substring(firstDotIndex).indexOf('@'))
        } else {
            email // In case there's no dot, return the original email
        }
    }
}

package com.example.eatatnotts.CustomerOrder

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eatatnotts.Pending.PendingDeliveryNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CustomerOrderDeliveryViewModel(application: Application) : AndroidViewModel(application) {//Handles Logic for CustomerOrderDeliveryFragment
    private val _customerOrders = MutableLiveData<List<CustomerOrderNumberDelivery>>()
    val customerOrders: LiveData<List<CustomerOrderNumberDelivery>> = _customerOrders

    private val _emptyOrder = MutableLiveData<Boolean>()
    val emptyOrder: LiveData<Boolean> get() = _emptyOrder

    private var previousCount: Int = 0

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getUserAndOrderData(userId: String?) {//Get User and Order Data from Firebase
        if (userId == null) return

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
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }

    private fun getUserData(userName: String?, email: String?) {//Get Customer Delivery Order Data
        if (userName == null || email == null) return

        val userDeliveryRef = dbRef.child("$userName Delivery")
        _emptyOrder.value=false
        userDeliveryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val intent = Intent(getApplication(), CustomerOrderDeliveryNotifications::class.java)
                getApplication<Application>().startService(intent)
                val orders = mutableListOf<CustomerOrderNumberDelivery>()
                if (snapshot.exists()) {
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(CustomerOrderNumberDelivery::class.java)
                        _emptyOrder.value=false
                        if (order != null) {
                            orders.add(order)
                        }
                    }
                    _customerOrders.value = orders
                }else{
                    _emptyOrder.value=true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching orders: ${error.message}")
            }
        })
    }
}

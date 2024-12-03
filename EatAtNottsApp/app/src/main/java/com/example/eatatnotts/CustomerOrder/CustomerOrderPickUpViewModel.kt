package com.example.eatatnotts.CustomerOrder

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class CustomerOrderPickUpViewModel (application: Application) : AndroidViewModel(application) {//Handles logic for CustomerOrderPickUpFragment
    private val _orders = MutableLiveData<List<CustomerOrderNumber>>()
    val orders: LiveData<List<CustomerOrderNumber>> = _orders

    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("Users")

    private val _emptyOrder = MutableLiveData<Boolean>()
    val emptyOrder: LiveData<Boolean> get() = _emptyOrder

    private var previousCount: Int = 0

    init {//Do this
        fetchUserOrders()
    }

    private fun fetchUserOrders() {//Fetch Hawker info and fetch customer orders
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val query: Query = dbRef.orderByChild("uid").equalTo(userId)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val userName = userSnapshot.child("username").getValue(String::class.java)
                            if (userName != null) {
                                fetchOrders(userName)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                }
            })
        }
    }

    private fun fetchOrders(userName: String) {//Fetch Customer Pick Up Orders from ${userName} Pick Up in Firebase
        val dbRef = FirebaseDatabase.getInstance().getReference("${userName} Pick Up")
        _emptyOrder.value=false
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                    val intent = Intent(getApplication(), CustomerOrderPickUpNotifications::class.java)
                    getApplication<Application>().startService(intent)
                val orders = mutableListOf<CustomerOrderNumber>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        _emptyOrder.value=false
                        val order = userSnapshot.getValue(CustomerOrderNumber::class.java)
                        if (order != null) {
                            orders.add(order)
                        }
                    }
                }else{
                    _emptyOrder.value=true
                }
                _orders.value = orders
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching orders: ${error.message}")
            }
        })
    }
}

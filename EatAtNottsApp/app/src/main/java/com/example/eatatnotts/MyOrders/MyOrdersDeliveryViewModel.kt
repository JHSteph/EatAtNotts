package com.example.eatatnotts.MyOrders

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyOrdersDeliveryViewModel(application: Application) : AndroidViewModel(application) {//Handles Logic for MyOrdersDeliveryFragment

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val _myOrdersList = MutableLiveData<List<MyOrdersNumberDelivery>>()
    val myOrdersList: LiveData<List<MyOrdersNumberDelivery>> get() = _myOrdersList
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _emptyOrder = MutableLiveData<Boolean>()
    val emptyOrder: LiveData<Boolean> get() = _emptyOrder

    fun fetchUserData() {//Fetch Hawker info and fetch customer orders
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        email?.let {
                            fetchOrdersData(extractEmailBeforeDot(it))
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }

    private fun fetchOrdersData(sanitizedEmail: String) {//Fetch customer orders
        _emptyOrder.value=false
        val ordersRef = FirebaseDatabase.getInstance().getReference("$sanitizedEmail Order Delivery")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ordersList = mutableListOf<MyOrdersNumberDelivery>()
                if (snapshot.exists()) {
                    for (orderSnapshot in snapshot.children) {
                        _emptyOrder.value=false
                        val order = orderSnapshot.getValue(MyOrdersNumberDelivery::class.java)
                        order?.let { ordersList.add(it) }
                    }
                    _myOrdersList.value = ordersList
                }
                else{
                    _emptyOrder.value=true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching orders data: ${error.message}")
            }
        })
    }

    private fun extractEmailBeforeDot(email: String): String {//Extract username from email before @
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex + email.substring(firstDotIndex).indexOf('@'))
        } else {
            email
        }
    }
}

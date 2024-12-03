package com.example.eatatnotts.MyOrders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyOrdersPickUpViewModel : ViewModel() {//Handles Logic for MyOrdersPickUpFragment
    private val _myOrdersList = MutableLiveData<List<MyOrdersNumber>>()
    val myOrdersList: LiveData<List<MyOrdersNumber>> get() = _myOrdersList

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _emptyOrder = MutableLiveData<Boolean>()
    val emptyOrder: LiveData<Boolean> get() = _emptyOrder

    private lateinit var dbref: DatabaseReference


    fun getUserData(userId: String) {//Fetch Hawker info and fetch customer orders
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        _userName.value = userSnapshot.child("username").getValue(String::class.java)
                        _userEmail.value = userSnapshot.child("email").getValue(String::class.java)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Error fetching user data: ${error.message}"
            }
        })
    }

    fun fetchOrderData(userName: String?, email: String?) {//Fetch customer orders
        _emptyOrder.value=false
        val sanitizedEmail = extractEmailBeforeDot(email.toString())
        dbref = FirebaseDatabase.getInstance().getReference("${sanitizedEmail} Order Pick Up")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ordersList = mutableListOf<MyOrdersNumber>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        _emptyOrder.value=false
                        val myorder = userSnapshot.getValue(MyOrdersNumber::class.java)
                        ordersList.add(myorder!!)
                    }
                    _myOrdersList.value = ordersList
                }
                else{
                    _emptyOrder.value=true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun extractEmailBeforeDot(email: String): String {//Extract username from email before @
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex + email.substring(firstDotIndex).indexOf('@'))
        } else {
            email // In case there's no dot, return the original email
        }
    }
}

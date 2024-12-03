package com.example.eatatnotts.MyHistory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyHistoryPickUpDatesViewModel : ViewModel() {//Handles logic for MyHistoryPickUpDates
    private val _pickUpDates = MutableLiveData<List<MyHistoryDateItem>>()
    val pickUpDates: LiveData<List<MyHistoryDateItem>> get() = _pickUpDates
    private val _emptyPickUpHistory = MutableLiveData<Boolean>()
    val emptyPickUpHistory: LiveData<Boolean> get() = _emptyPickUpHistory

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    fun getUserData() {//Get User Data and generate RecyclerView
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        _emptyPickUpHistory.value=false

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        email?.let {
                            val sanitizedEmail = extractEmailBeforeDot(it)
                            val dbRefUser = FirebaseDatabase.getInstance().getReference("${sanitizedEmail} Pick Up History Date")
                            dbRefUser.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val dates = mutableListOf<MyHistoryDateItem>()
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            val date = userSnapshot.getValue(MyHistoryDateItem::class.java)
                                            Log.i("MYTAG", "$date")
                                            date?.let { dates.add(it) }
                                            _emptyPickUpHistory.value=false
                                        }
                                        _pickUpDates.postValue(dates)
                                    }else{
                                        _emptyPickUpHistory.value=true
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle possible errors.
                                }
                            })
                        }
                    }
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
            email.substring(0, firstDotIndex)
        } else {
            email // In case there's no dot, return the original email
        }
    }
}

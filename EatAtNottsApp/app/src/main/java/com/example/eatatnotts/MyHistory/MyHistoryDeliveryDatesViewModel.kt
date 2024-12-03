package com.example.eatatnotts.MyHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyHistoryDeliveryDatesViewModel : ViewModel() {//Handles logic for MyHistoryDeliveryDates
    private val _deliveryDateList = MutableLiveData<List<MyHistoryDateItem>>()
    val deliveryDateList: LiveData<List<MyHistoryDateItem>> get() = _deliveryDateList
    private val _emptyDeliveryHistory = MutableLiveData<Boolean>()
    val emptyDeliveryHistory: LiveData<Boolean> get() = _emptyDeliveryHistory

    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("Users")

    fun getUserData() {//Get User Data and generate RecyclerView
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        _emptyDeliveryHistory.value=false

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        if (email != null) {
                            val sanitizedEmail = extractEmailBeforeDot(email)
                            val dbRefUser = FirebaseDatabase.getInstance()
                                .getReference("${sanitizedEmail} Delivery History Date")
                            dbRefUser.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val tempList = mutableListOf<MyHistoryDateItem>()
                                    if (snapshot.exists()) {
                                        for (dateSnapshot in snapshot.children) {
                                            val date = dateSnapshot.getValue(MyHistoryDateItem::class.java)
                                            _emptyDeliveryHistory.value=false
                                            if (date != null) {
                                                tempList.add(date)
                                            }
                                        }
                                    }
                                    else{
                                        _emptyDeliveryHistory.value=true
                                    }
                                    _deliveryDateList.value = tempList
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

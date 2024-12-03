package com.example.eatatnotts.History

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryPickUpDatesViewModel : ViewModel() {//Handles logic for HistoryPickUpDates
    private val _pickUpDates = MutableLiveData<List<HistoryDateItem>>()
    val pickUpDates: LiveData<List<HistoryDateItem>> get() = _pickUpDates

    private val dbref: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _emptyPickUpHistory= MutableLiveData<Boolean>()
    val emptyPickUpHistory: LiveData<Boolean> get() = _emptyPickUpHistory

    init {//Do this
        getUserData()
    }

    private fun getUserData() {//Get User Data and generate RecyclerView
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbref.orderByChild("uid").equalTo(userId)
        Log.i("MYTAG", "userID is $userId")
        _emptyPickUpHistory.value=false
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.i("MYTAG", "snapshot does exist!")
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("username").getValue(String::class.java)

                        if (userName != null) {
                            val dbRefUser = FirebaseDatabase.getInstance().getReference("${userName} Pick Up History Date")
                            Log.i("MYTAG", "${userName} Pick Up History Date")
                            dbRefUser.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val dateList = mutableListOf<HistoryDateItem>()
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            _emptyPickUpHistory.value=false
                                            val date = userSnapshot.getValue(HistoryDateItem::class.java)
                                            Log.i("MYTAG", "$date")
                                            if (date != null) {
                                                dateList.add(date)
                                            }
                                        }
                                        _pickUpDates.value = dateList
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
}

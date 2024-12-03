// HistoryDeliveryDatesViewModel.kt
package com.example.eatatnotts.History

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryDeliveryDatesViewModel : ViewModel() {//Handles logic for HistoryDeliveryDates

    private val _deliveryDateList = MutableLiveData<List<HistoryDateItem>>()
    val deliveryDateList: LiveData<List<HistoryDateItem>> = _deliveryDateList

    private val dbref: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _emptyDeliveryHistory= MutableLiveData<Boolean>()
    val emptyDeliveryHistory: LiveData<Boolean> get() = _emptyDeliveryHistory

    init {//Do this
        getUserData()
    }

    private fun getUserData() {//Get User Data and generate RecyclerView
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbref.orderByChild("uid").equalTo(userId)
        Log.i("MYTAG", "userID is $userId")
        _emptyDeliveryHistory.value=false
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.i("MYTAG", "snapshot does exist!")
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        if (userName != null) {
                            val dbRefUser = FirebaseDatabase.getInstance().getReference("${userName} Delivery History Date")
                            dbRefUser.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val deliveryDates = ArrayList<HistoryDateItem>()
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            _emptyDeliveryHistory.value=false
                                            val date = userSnapshot.getValue(HistoryDateItem::class.java)
                                            date?.let { deliveryDates.add(it) }
                                        }
                                        _deliveryDateList.value = deliveryDates
                                    }else{
                                        _emptyDeliveryHistory.value=true
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

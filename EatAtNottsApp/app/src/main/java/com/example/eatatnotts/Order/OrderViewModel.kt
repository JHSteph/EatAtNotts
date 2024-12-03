package com.example.eatatnotts.Order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class OrderViewModel : ViewModel() {//Handles logic for Order page
    private val _hawkersList = MutableLiveData<List<HawkersList>>()
    val hawkersList: LiveData<List<HawkersList>> get() = _hawkersList

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Hawkers")

    init {
        fetchHawkersData()
    }

    private fun fetchHawkersData() {//Get Hawker Data and generate RecyclerView
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hawkerList = ArrayList<HawkersList>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val hawker = userSnapshot.getValue(HawkersList::class.java)
                        hawker?.let { hawkerList.add(it) }
                    }
                    _hawkersList.value = hawkerList
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
}

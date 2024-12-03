// PendingPickUpViewModel.kt
package com.example.eatatnotts.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eatatnotts.Pending.PendingPickUpItem
import com.example.eatatnotts.Pending.PendingPickUpNotifications
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PendingPickUpViewModel(application: Application) : AndroidViewModel(application) {//Handles Logic for PendingPickUp

    private val _pendingPickUpItems = MutableLiveData<List<PendingPickUpItem>>()
    val pendingPickUpItems: LiveData<List<PendingPickUpItem>> = _pendingPickUpItems

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    private val _emptyOrder = MutableLiveData<Boolean>()
    val emptyOrder: LiveData<Boolean> get() = _emptyOrder

    private var previousCount: Int = 0

    init {
        fetchUserData()
    }

    private fun fetchUserData() {//Get User Data
        val currentUser = auth.currentUser
        val userId = currentUser?.uid


        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        fetchPendingPickUpItems(userName, email)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun fetchPendingPickUpItems(userName: String?, email: String?) {//Get Pending Order Data from Firebase
        if (userName == null) return
        _emptyOrder.value=false
        val dbRef = FirebaseDatabase.getInstance().getReference("$userName Pick Up Pending")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val intent = Intent(getApplication(), PendingPickUpNotifications::class.java)
                getApplication<Application>().startService(intent)
                val items = mutableListOf<PendingPickUpItem>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        _emptyOrder.value=false
                        val item = userSnapshot.getValue(PendingPickUpItem::class.java)
                        item?.let { items.add(it) }
                    }
                }
                else{
                    _emptyOrder.value=true
                }
                _pendingPickUpItems.value = items
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }
}

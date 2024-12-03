package com.example.eatatnotts.Payment

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eatatnotts.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PaymentDeliveryViewModel(application: Application) : AndroidViewModel(application) {//Handles logic for PaymentDelivery Fragment

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val dbRefUser: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private val databaseLocations: DatabaseReference = FirebaseDatabase.getInstance().getReference("Locations")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _paymentList = MutableLiveData<List<PaymentItem>>()
    val paymentList: LiveData<List<PaymentItem>> get() = _paymentList

    private val _walletAmount = MutableLiveData<Float>(0f) // Initialize with default value
    val walletAmount: LiveData<Float> get() = _walletAmount

    private val _totalCost = MutableLiveData<Float>(0f) // Initialize with default value
    val totalCost: LiveData<Float> get() = _totalCost

    private val _deliveryCost = MutableLiveData<String>()
    val deliveryCost: LiveData<String> get() = _deliveryCost

    private val _locations = MutableLiveData<List<String>>()
    val locations: LiveData<List<String>> get() = _locations

    private val _selectedLocation = MutableLiveData<String>()
    val selectedLocation: LiveData<String> get() = _selectedLocation

    init {
        _paymentList.value = emptyList()
        _locations.value = emptyList()
    }

    fun fetchUserData() {//Fetch user data and handle price calculations
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        _deliveryCost.value = "RM 5.00"

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        // Handle nullable Float correctly
                        val walletAmount = userSnapshot.child("walletAmount").getValue(Float::class.java) ?: 0f
                        _walletAmount.value = walletAmount

                        val sanitizedEmail = email?.substringBefore("@") ?: ""
                        val userCartRef = FirebaseDatabase.getInstance().getReference("${sanitizedEmail}Cart")
                        val cartQuery: Query = userCartRef.orderByChild("checkbox").equalTo(true)

                        cartQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(cartSnapshot: DataSnapshot) {
                                val paymentItems = mutableListOf<PaymentItem>()
                                var totalCost = 5.0f  // Include default cost
                                cartSnapshot.children.forEach { itemSnapshot ->
                                    val price = itemSnapshot.child("price").getValue(String::class.java)?.replace("RM ", "")?.toFloat()
                                    val quantity = itemSnapshot.child("quantity").getValue(Int::class.java)
                                    val remarks = itemSnapshot.child("remarks").getValue(String::class.java)

                                    if (price != null && quantity != null) {
                                        totalCost += price * quantity
                                        paymentItems.add(
                                            PaymentItem(
                                                hawkerName = itemSnapshot.child("hawkerName").getValue(String::class.java) ?: "",
                                                foodName = itemSnapshot.child("foodName").getValue(String::class.java) ?: "",
                                                price = "RM ${"%.2f".format(price)}",
                                                remarks = remarks ?: "",
                                                quantity = quantity
                                            )
                                        )
                                    }
                                }
                                _paymentList.value = paymentItems
                                _totalCost.value = totalCost
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchLocations() {//Fetch available locations from Firebase
        databaseLocations.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val locations = mutableListOf<String>()
                snapshot.children.forEach {
                    it.getValue(String::class.java)?.let { location ->
                        locations.add(location)
                    }
                }
                _locations.value = locations
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation
            }
        })
    }

    fun selectLocation(location: String) {//Select locatin after user click on location
        _selectedLocation.value = location
    }
}

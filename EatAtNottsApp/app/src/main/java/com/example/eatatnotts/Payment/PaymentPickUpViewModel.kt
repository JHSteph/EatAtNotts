package com.example.eatatnotts.Payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PaymentPickUpViewModel : ViewModel() {//Handles logic for PaymentPickUp Fragment
    private val _paymentItems = MutableLiveData<List<PaymentItem>>()
    val paymentItems: LiveData<List<PaymentItem>> get() = _paymentItems

    private val _totalCost = MutableLiveData<Float>()
    val totalCost: LiveData<Float> get() = _totalCost

    private val _walletAmount = MutableLiveData<Float>()
    val walletAmount: LiveData<Float> get() = _walletAmount

    private val _remarks = MutableLiveData<String>()
    val remarks: LiveData<String> get() = _remarks

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    fun fetchUserData() {//Fetch user data
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        val walletAmount = userSnapshot.child("walletAmount").getValue(Float::class.java)
                        val remarks = userSnapshot.child("remarks").getValue(String::class.java)

                        _walletAmount.value = walletAmount ?: 0.0f
                        _remarks.value = remarks ?: ""

                        if (email != null) {
                            fetchCartData(email)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun fetchCartData(email: String) {//Fetch Cart Item and Handle price calculation
        val sanitizedEmail = extractEmailBeforeDot(email)
        val dbRefUser = FirebaseDatabase.getInstance().getReference("${sanitizedEmail}Cart")
        val query: Query = dbRefUser.orderByChild("checkbox").equalTo(true)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val paymentList = mutableListOf<PaymentItem>()
                    var totalCost = 0.0f

                    for (userSnapshot in snapshot.children) {
                        val price = userSnapshot.child("price").getValue(String::class.java)
                        val amount = userSnapshot.child("quantity").getValue(Int::class.java)
                        val priceWithoutRM = price?.replace("RM ", "")?.toFloat() ?: 0.0f

                        totalCost += priceWithoutRM * (amount ?: 0)

                        val cart = userSnapshot.getValue(PaymentItem::class.java)
                        if (cart != null) {
                            paymentList.add(cart)
                        }
                    }
                    _totalCost.value = totalCost
                    _paymentItems.value = paymentList
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
            email
        }
    }
}

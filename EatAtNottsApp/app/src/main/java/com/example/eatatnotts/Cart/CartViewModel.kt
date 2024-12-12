package com.example.eatatnotts.Cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartViewModel : ViewModel() {//Handles logic for Cart Activity
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dbref: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems
    private val _emptyCart = MutableLiveData<Boolean>()
    val emptyCart: LiveData<Boolean> get() = _emptyCart

    init {
        fetchCartItems()
    }

    private fun extractEmailBeforeDot(email: String): String {//Extract username of users from email before @
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex)
        } else {
            email // In case there's no dot, return the original email
        }
    }

    private fun fetchCartItems() {//Get Cart Item Data from Firebase
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbref.orderByChild("uid").equalTo(userId)
        _emptyCart.value=false

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        if (email != null) {
                            val sanitizedEmail = extractEmailBeforeDot(email)
                            val dbRefUser = FirebaseDatabase.getInstance().getReference("${sanitizedEmail}Cart")
                            dbRefUser.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val cartList = ArrayList<CartItem>()
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            _emptyCart.value=false
                                            val cartItem = userSnapshot.getValue(CartItem::class.java)
                                            if (cartItem != null) {
                                                cartList.add(cartItem)
                                            }
                                        }
                                    }
                                    else{
                                        _emptyCart.value=true
                                    }
                                    _cartItems.value = cartList
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

    fun updateCheckbox(cartItem: CartItem) {//Update Checkbox status in Firebase if checked/unchecked
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val sanitizedEmail = extractEmailBeforeDot(currentUser.email ?: "")
            val dbRefUser = FirebaseDatabase.getInstance().getReference("${sanitizedEmail}Cart")
            dbRefUser.child("${cartItem.hawkerName} ${cartItem.foodName} ${cartItem.remarks}/checkbox")
                .setValue(cartItem.checkbox)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("CartViewModel", "Checkbox status updated successfully.")
                    } else {
                        Log.e("CartViewModel", "Failed to update checkbox status.", task.exception)
                    }
                }
        }
    }

    fun removeCheckedItems() {//Remove Checked Item in Firebase if user clicked on Remove button
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbref.orderByChild("uid").equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        if (email != null) {
                            val sanitizedEmail = extractEmailBeforeDot(email)
                            val dbRefUser = FirebaseDatabase.getInstance().getReference("${sanitizedEmail}Cart")
                            val checkQuery: Query = dbRefUser.orderByChild("checkbox").equalTo(true)
                            checkQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            val foodName = userSnapshot.child("foodName").getValue(String::class.java)
                                            val hawkerName = userSnapshot.child("hawkerName").getValue(String::class.java)
                                            val remarks = userSnapshot.child("remarks").getValue(String::class.java)
                                            val removeRef = dbRefUser.child("$hawkerName $foodName $remarks")
                                            removeRef.removeValue().addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.i("Firebase", "Item successfully deleted.")
                                                } else {
                                                    Log.i("Firebase", "Failed to delete item: ${task.exception?.message}")
                                                }
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
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
}

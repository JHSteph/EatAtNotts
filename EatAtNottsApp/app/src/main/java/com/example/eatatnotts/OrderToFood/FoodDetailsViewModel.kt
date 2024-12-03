package com.example.eatatnotts.OrderToFood

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eatatnotts.Cart.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FoodDetailsViewModel : ViewModel() {//Handles logic for FoodDetails page
    private val _quantity = MutableLiveData(0)
    val quantity: LiveData<Int> get() = _quantity

    private val _cartAdded = MutableLiveData(3)
    val cartAdded: LiveData<Int> get() = _cartAdded

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    fun incrementQuantity() {//Increase Quantity
        _quantity.value = (_quantity.value ?: 0) + 1
    }

    fun decrementQuantity() {//Decrease Quantity
        if ((_quantity.value ?: 0) > 0) {
            _quantity.value = (_quantity.value ?: 0) - 1
        }
    }

    fun addToCart(remarks: String,hawkerName:String,foodName:String,price:String,photoUrl:String) {//Add item to cart in Firebase
        val checkbox = false
        val quantity = _quantity.value ?: 0
        Log.i("MYTAG","_cartAdded.value is ${_cartAdded.value} at start")
        _cartAdded.value = 3

        if (quantity > 0) {
            auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val userId = currentUser?.uid

            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            val query: Query = dbRef.orderByChild("uid").equalTo(userId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val email = userSnapshot.child("email").getValue(String::class.java)
                            if (email != null) {
                                val sanitizedEmail = extractEmailBeforeDot(email)
                                val dbRefUser = FirebaseDatabase.getInstance().getReference("${sanitizedEmail}Cart")
                                val cartItem = CartItem(
                                    hawkerName,
                                    foodName,
                                    price,
                                    quantity,
                                    photoUrl,
                                    remarks,
                                    checkbox
                                )
                                Log.i("MYTAG","In View Model hawkerName is ${hawkerName},foodName is ${foodName}, price is ${price}, photoUrl is ${photoUrl}")
                                dbRefUser.child("$hawkerName $foodName $remarks").setValue(cartItem)
                                    .addOnSuccessListener {
                                        _cartAdded.value = 1
                                    }.addOnFailureListener {
                                        _cartAdded.value = 0
                                    }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                }
            })
        } else {
            _cartAdded.value = 2
        }
        Log.i("MYTAG","_cartAdded.value is ${_cartAdded.value} at end")
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

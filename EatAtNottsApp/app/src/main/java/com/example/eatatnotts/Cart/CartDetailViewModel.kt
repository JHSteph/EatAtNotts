package com.example.eatatnotts.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eatatnotts.Cart.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartDetailViewModel : ViewModel() {//Handles logic of CartDetails Activity

    private val _cartItem = MutableLiveData<CartItem>()
    val cartItem: LiveData<CartItem> = _cartItem

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> = _navigateBack

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    fun getFoodData(
        hawkerName: String?,
        foodName: String?,
        price: String?,
        amount: String?,
        remarks: String?,
        photoUrl: String?
    ) {//Create a cartItem item to house data
        val cartItem = CartItem(
            hawkerName ?: "",
            foodName ?: "",
            price ?: "",
            amount?.toIntOrNull() ?: 0,
            photoUrl ?: "",
            remarks ?: ""
        )
        _cartItem.value = cartItem
    }

    fun changeQuantity(currentAmount: Int, isIncrement: Boolean): Int {//Change quantity of food
        return if (isIncrement) currentAmount + 1 else maxOf(0, currentAmount - 1)
    }

    private fun extractEmailBeforeDot(email: String): String {//Extract username of users before @
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex + email.substring(firstDotIndex).indexOf('@'))
        } else {
            email
        }
    }

    fun addCart(AmtNumber: Int?, hawkerName: String, foodName: String, price: String, photoUrl: String, remarks: String,previousremarks:String) {//Save New Cart Data into Firebase
        var newremarks:String?=null
        if (AmtNumber != null && AmtNumber > 0) {
            database = FirebaseDatabase.getInstance().getReference("Users")
            auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val userId = currentUser?.uid
            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            val query: Query = dbRef.orderByChild("uid").equalTo(userId)
            if (remarks==""){
                newremarks="-"
            }
            else{
                newremarks=remarks
            }

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val EMail = userSnapshot.child("email").getValue(String::class.java)
                            val finalRemarks = if (remarks.isEmpty()) "-" else remarks
                            if (EMail != null) {
                                val sanitizedEmail = extractEmailBeforeDot(EMail)
                                val dbRefUser = FirebaseDatabase.getInstance().getReference("${sanitizedEmail}Cart")
                                val cart = CartItem(hawkerName, foodName, price, AmtNumber, photoUrl, finalRemarks, false)
                                dbRefUser.child("$hawkerName $foodName $finalRemarks").setValue(cart)
                                    .addOnSuccessListener {
                                        _toastMessage.value = "Successfully Saved"
                                        _navigateBack.value = true // Trigger navigation back
                                    }.addOnFailureListener {
                                        _toastMessage.value = "Failed to save user data"
                                    }
                            }
                        }
                        removeOrderBefore(hawkerName, foodName, newremarks,previousremarks)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                }
            })
        } else {
            _toastMessage.value = "Quantity must be more than 0!"
        }
    }

    private fun removeOrderBefore(newhawkerName: String, newfoodName: String, newremarks: String,previousremarks: String) {//Removes old orders from Firebase if the food details is edited
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)

        if (newremarks != previousremarks) {
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val EMail = userSnapshot.child("email").getValue(String::class.java)
                            if (EMail != null) {
                                val sanitizedEmail = extractEmailBeforeDot(EMail)
                                val dbRefUser = FirebaseDatabase.getInstance().getReference("${sanitizedEmail}Cart")
                                val removeRef = dbRefUser.child("${newhawkerName} ${newfoodName} ${_cartItem.value?.remarks}")
                                removeRef.removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.i("Firebase", "Successfully removed previous item")
                                    } else {
                                        Log.i("Firebase", "Failed to remove previous item")
                                    }
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
        else{

        }
    }
}

package com.example.eatatnotts.Pending

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.eatatnotts.MainpageHawker
import com.example.eatatnotts.MyOrders.MyOrderItem
import com.example.eatatnotts.databinding.ActivityRejectReasonBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class RejectReason : AppCompatActivity() {
    private lateinit var dbrefHawker: DatabaseReference
    private lateinit var dbrefHawkerNumber: DatabaseReference
    private lateinit var dbrefHawkerNumberPending: DatabaseReference
    private lateinit var dbrefFoodPrice: DatabaseReference
    private lateinit var dbrefCustomer: DatabaseReference
    private lateinit var dbRef: DatabaseReference
    private lateinit var binding: ActivityRejectReasonBinding
    private lateinit var auth: FirebaseAuth
    private var TotalPrice = 0.0
    private var CustomerEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {//Create View for RejectReason Activity, and check for pending order for rejection
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRejectReasonBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        val receipt = intent.getStringExtra("receipt")
        val method = intent.getStringExtra("method")
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        binding.RejecttopAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        // Get the user type from the snapshot
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        dbrefHawker = FirebaseDatabase.getInstance()
                            .getReference("${userName} ${method} ${receipt} Pending/${receipt}")

                        binding.btnRejectUpdate.setOnClickListener {
                            val rejectmessage = binding.etRejectReason.text.toString()
                            if (rejectmessage.isNotEmpty()) {
                                val AlertMessage = AlertDialog.Builder(this@RejectReason)
                                AlertMessage.setTitle("Reject Confirmation")
                                AlertMessage.setMessage("Are you sure you want to reject this order?")
                                AlertMessage.setCancelable(false)
                                AlertMessage.setPositiveButton("Yes") { dialog, which ->
                                    dbrefHawker.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                for (userSnapshot in snapshot.children) {
                                                    val customerEmail =
                                                        userSnapshot.child("customerEmail")
                                                            .getValue(String::class.java)
                                                    CustomerEmail = (customerEmail).toString()
                                                    val foodName = userSnapshot.child("foodName")
                                                        .getValue(String::class.java)
                                                    val method = userSnapshot.child("method")
                                                        .getValue(String::class.java)
                                                    val quantity = ((userSnapshot.child("quantity")
                                                        .getValue(Int::class.java)).toString()).toInt()
                                                    val receipt = ((userSnapshot.child("receipt")
                                                        .getValue(String::class.java)).toString())
                                                    val status = userSnapshot.child("status")
                                                        .getValue(String::class.java)
                                                    val remarks = userSnapshot.child("remarks")
                                                        .getValue(String::class.java)
                                                    val sanitizedEmail =
                                                        extractEmailBeforeDot((customerEmail).toString())
                                                    dbrefCustomer = FirebaseDatabase.getInstance()
                                                        .getReference("${sanitizedEmail} Order ${method} ${receipt}")
                                                    val customerorder = MyOrderItem(
                                                        receipt,
                                                        (userName).toString(),
                                                        (foodName).toString(),
                                                        "Rejected",
                                                        (method).toString(),
                                                        quantity,
                                                        (remarks).toString(),
                                                        rejectmessage
                                                    )
                                                    dbrefCustomer.child("${userName} ${foodName} ${remarks}")
                                                        .setValue(customerorder)
                                                        .addOnSuccessListener {
                                                            Toast.makeText(
                                                                this@RejectReason,
                                                                "Succesfully Added Order!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    RemovePendingOrderNumber(
                                                        userName,
                                                        receipt,
                                                        method
                                                    )
                                                    RemovePendingOrder(
                                                        userName,
                                                        receipt,
                                                        foodName,
                                                        remarks,
                                                        method
                                                    )
                                                    ReturnMoneyAmount(
                                                        customerEmail,
                                                        foodName,
                                                        quantity,
                                                        userName
                                                    )
                                                }
                                            }
                                            ReturnMoney()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Handle the error, log it, or show a message
                                            Log.e(
                                                "FirebaseError",
                                                "Error fetching user data: ${error.message}"
                                            )
                                        }
                                    })
                                    val intent =
                                        Intent(this@RejectReason, MainpageHawker::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                AlertMessage.setNegativeButton("No") {
                                    // If user click no then dialog box is canceled.
                                        dialog, which ->
                                    dialog.dismiss()
                                }
                                val alertDialog = AlertMessage.create()
                                // Show the Alert Dialog box
                                alertDialog.show()
                                // Show confirmation dialog
                            } else {
                                Toast.makeText(
                                    this@RejectReason,
                                    "Please Enter a message!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })


        setContentView(binding.root)
    }

    private fun RemovePendingOrderNumber(userName: String?, receipt: String?, method: String?) {//Remove the pending order number from Firebase
        dbrefHawkerNumber =
            FirebaseDatabase.getInstance().getReference("${userName} ${method} Pending")
        val removeRef = dbrefHawkerNumber.child("${receipt}")

        removeRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("Firebase", "User successfully deleted.")
            } else {
                Log.i(
                    "Firebase",
                    "Failed to delete user: ${task.exception?.message}"
                )
            }
        }
    }

    private fun extractEmailBeforeDot(email: String): String {//Extract username from email before @
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex + email.substring(firstDotIndex).indexOf('@'))
        } else {
            email // In case there's no dot, return the original email
        }
    }

    private fun RemovePendingOrder(
        userName: String?,
        receipt: String?,
        foodName: String?,
        remarks: String?,
        method: String?
    ) {//Remove Pending Order Details from Firebase
        dbrefHawkerNumberPending = FirebaseDatabase.getInstance()
            .getReference("${userName} ${method} ${receipt} Pending")
        val removeRef = dbrefHawkerNumberPending.child("${receipt}")

        removeRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("Firebase", "User successfully deleted.")
            } else {
                Log.i(
                    "Firebase",
                    "Failed to delete user: ${task.exception?.message}"
                )
            }
        }
    }

    private fun ReturnMoneyAmount(
        customerEmail: String?,
        foodName: String?,
        quantity: Int,
        userName: String?
    ) {//Calculate the amount of money that needs to be returned
        dbrefFoodPrice = FirebaseDatabase.getInstance().getReference("Foods/${userName}")
        Log.i("MYTAG","Foods/${userName}")
        val Checkquery: Query = dbrefFoodPrice.orderByChild("foodName").equalTo(foodName)
        Checkquery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        Log.i("MYTAG","I am here! 1")
                        val price = (userSnapshot.child("price")
                            .getValue(Double::class.java)).toString().toFloat()
                        Log.i("MYTAG","${price}")
                        TotalPrice += (price * quantity)
                        Log.i("MYTAG","${TotalPrice}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })

    }

    private fun ReturnMoney() {//Return money to customer
        val ReturnQuery: Query = dbRef.orderByChild("email").equalTo(CustomerEmail)
        Log.i("MYTAG","I am here 2!")
        ReturnQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val walletAmount = (userSnapshot.child("walletAmount")
                            .getValue(Double::class.java)).toString().toFloat()
                        val uid = userSnapshot.child("uid")
                            .getValue(String::class.java)
                        val NewwalletAmount = walletAmount + TotalPrice
                        Log.i("MYTAG","New wallet Amount is ${NewwalletAmount}")
                        Log.i("MYTAG","${uid}/walletAmount")
                        dbRef.child("${uid}/walletAmount").setValue(NewwalletAmount)
                            .addOnSuccessListener {

                            }.addOnFailureListener {

                            }
                    }
                }
            }override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }
}




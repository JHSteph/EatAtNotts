package com.example.eatatnotts

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.CustomerOrder.CustomerOrderItem
import com.example.eatatnotts.CustomerOrder.CustomerOrderNumber
import com.example.eatatnotts.CustomerOrder.CustomerOrderNumberDelivery
import com.example.eatatnotts.LoginAndSingup.Customers
import com.example.eatatnotts.MyOrders.MyOrderItem
import com.example.eatatnotts.MyOrders.MyOrdersNumber
import com.example.eatatnotts.MyOrders.MyOrdersNumberDelivery
import com.example.eatatnotts.Payment.PaymentItem
import com.example.eatatnotts.databinding.ActivityCartBinding
import com.example.eatatnotts.databinding.ActivityThankPurchaseBinding
import com.example.eatatnotts.databinding.ActivityWalletReloadBinding
import com.example.eatatnotts.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.Date

class ThankPurchase : AppCompatActivity() {//Handle uploading data into Firebase when a customer confirms their orders
    private lateinit var binding: ActivityThankPurchaseBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var PaymentArrayList: ArrayList<PaymentItem>
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var databaseCreateOrder: DatabaseReference
    private lateinit var databaseOrder: DatabaseReference
    private lateinit var databaseOrderNumbers: DatabaseReference
    private lateinit var databaseReceipt:DatabaseReference
    private lateinit var databaseCreateOrderNumbers: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {//Create View for ThankPurchase Activity, and handle button logics
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityThankPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        dbref = FirebaseDatabase.getInstance().getReference("Users")
        dbRefUser = FirebaseDatabase.getInstance().getReference()
        database = FirebaseDatabase.getInstance().getReference()
        databaseReceipt = FirebaseDatabase.getInstance().getReference("receipt")
        Pay()

        binding.ThanksPurchasePickUptopAppBar.setNavigationOnClickListener {
            val intent = Intent(this@ThankPurchase, mainpage::class.java)
            startActivity(intent)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ThankPurchase, mainpage::class.java)
                startActivity(intent)
                finish()
                // Code that you need to execute on back press, e.g. finish()
            }
        })

    }

    private fun extractEmailBeforeDot(email: String): String {//Extract username from email before @
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex + email.substring(firstDotIndex).indexOf('@'))
        } else {
            email // In case there's no dot, return the original email
        }
    }

    private fun Pay() {//Check Type of Order and handle cost calculations
        var TotalCostFloat = 5.0f
        var TotalCost = ""
        var rejectReason="-"
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbref.orderByChild("uid").equalTo(userId)
        val method = intent.getStringExtra("method").toString()
        val status = "Pending"
        val sdf = SimpleDateFormat("dd-M-yyyy")
        val sdftime= SimpleDateFormat("HH:mm:ss")
        val RejectReason="-"
        val currentDate = sdf.format(Date())
        val currentTime=sdftime.format(Date())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {

                        // Get the user type from the snapshot
                        val EMail = userSnapshot.child("email").getValue(String::class.java)
                        if (EMail != null) {
                            val sanitizedEmail = extractEmailBeforeDot(EMail)
                            val dbRefUser =
                                FirebaseDatabase.getInstance().getReference("${sanitizedEmail}Cart")
                            val Checkquery: Query = dbRefUser.orderByChild("checkbox").equalTo(true)
                            Checkquery.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            val hawkerName = (userSnapshot.child("hawkerName")
                                                .getValue(String::class.java)).toString()
                                            val foodName = (userSnapshot.child("foodName")
                                                .getValue(String::class.java)).toString()
                                            val price = userSnapshot.child("price")
                                                .getValue(String::class.java)
                                            val amount = userSnapshot.child("quantity")
                                                .getValue(Int::class.java)
                                            val remarks = (userSnapshot.child("remarks")
                                                .getValue(String::class.java)).toString()
                                            Log.i("MYTAG","Remarks is ${remarks}")
                                            val priceWithoutRM = price?.replace("RM ", "")
                                            val realPrice = priceWithoutRM?.toFloat()
                                            if (realPrice != null && amount != null) {
                                                TotalCostFloat += (realPrice * amount)
                                                TotalCost = String.format("%.2f", TotalCostFloat)

                                                // Attach a listener to read the data at the "receipt" path
                                                databaseReceipt.addListenerForSingleValueEvent(object :
                                                    ValueEventListener {
                                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                        // Get the value of the receipt
                                                        val receiptValue =
                                                            (dataSnapshot.getValue(Int::class.java)).toString()
                                                        binding.tvOrderNo.text =
                                                            receiptValue.toString()
                                                        if (receiptValue != null) {
                                                            databaseCreateOrder =
                                                                FirebaseDatabase.getInstance()
                                                                    .getReference("${hawkerName} ${method} ${receiptValue} Pending")
                                                            databaseCreateOrderNumbers =
                                                                FirebaseDatabase.getInstance()
                                                                    .getReference("${hawkerName} ${method} Pending")
                                                            databaseOrder =
                                                                FirebaseDatabase.getInstance()
                                                                    .getReference("${sanitizedEmail} Order ${method} ${receiptValue}")
                                                            databaseOrderNumbers =
                                                                FirebaseDatabase.getInstance()
                                                                    .getReference("${sanitizedEmail} Order ${method}")
                                                            if (method=="Pick Up"){
                                                                val customerorder =
                                                                    CustomerOrderItem(
                                                                        receiptValue,
                                                                        EMail,
                                                                        foodName,
                                                                        amount,
                                                                        status,
                                                                        method,
                                                                        remarks
                                                                    )
                                                                val myorder =
                                                                    MyOrderItem(
                                                                        receiptValue,
                                                                        hawkerName,
                                                                        foodName,
                                                                        status,
                                                                        method,
                                                                        amount,
                                                                        remarks,
                                                                        rejectReason
                                                                    )
                                                                val myordernumber =
                                                                    MyOrdersNumber(
                                                                        currentDate,
                                                                        currentTime,
                                                                        EMail,
                                                                        receiptValue,
                                                                        method
                                                                    )
                                                                val customerordernumber=
                                                                    CustomerOrderNumber(
                                                                        currentDate,
                                                                        currentTime,
                                                                        EMail,
                                                                        receiptValue,
                                                                        status
                                                                    )

                                                                databaseOrderNumbers.child("${receiptValue}")
                                                                    .setValue(myordernumber)
                                                                    .addOnSuccessListener {
                                                                        Toast.makeText(
                                                                            this@ThankPurchase,
                                                                            "Succesfully Submitted Order!",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                                databaseCreateOrder.child("${receiptValue}/${hawkerName} ${foodName} ${remarks}")
                                                                    .setValue(customerorder)
                                                                    .addOnSuccessListener {
                                                                    }
                                                                databaseOrder.child("${hawkerName} ${foodName} ${remarks}")
                                                                    .setValue(myorder)
                                                                    .addOnSuccessListener {
                                                                    }
                                                                databaseCreateOrderNumbers.child("${receiptValue}")
                                                                    .setValue(customerordernumber)
                                                                    .addOnSuccessListener {

                                                                    }
                                                            }
                                                            else if (method=="Delivery"){
                                                                val location = intent.getStringExtra("location").toString()
                                                                val customerorder =
                                                                    CustomerOrderItem(
                                                                        receiptValue,
                                                                        EMail,
                                                                        foodName,
                                                                        amount,
                                                                        status,
                                                                        method,
                                                                        remarks
                                                                    )
                                                                val myorder =
                                                                    MyOrderItem(
                                                                        receiptValue,
                                                                        hawkerName,
                                                                        foodName,
                                                                        status,
                                                                        method,
                                                                        amount,
                                                                        remarks,
                                                                        rejectReason
                                                                    )
                                                                val myordernumber =
                                                                    MyOrdersNumberDelivery(
                                                                        currentDate,
                                                                        currentTime,
                                                                        EMail,
                                                                        receiptValue,
                                                                        method,
                                                                        location
                                                                    )
                                                                val customerordernumber=
                                                                    CustomerOrderNumberDelivery(
                                                                        currentDate,
                                                                        currentTime,
                                                                        EMail,
                                                                        receiptValue,
                                                                        status,
                                                                        location
                                                                    )

                                                                databaseOrderNumbers.child("${receiptValue}")
                                                                    .setValue(myordernumber)
                                                                    .addOnSuccessListener {
                                                                        Toast.makeText(
                                                                            this@ThankPurchase,
                                                                            "Succesfully Submitted Order!",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                                databaseCreateOrder.child("${receiptValue}/${hawkerName} ${foodName} ${remarks}")
                                                                    .setValue(customerorder)
                                                                    .addOnSuccessListener {
                                                                    }
                                                                databaseOrder.child("${hawkerName} ${foodName} ${remarks}")
                                                                    .setValue(myorder)
                                                                    .addOnSuccessListener {

                                                                    }
                                                                databaseCreateOrderNumbers.child("${receiptValue}")
                                                                    .setValue(customerordernumber)
                                                                    .addOnSuccessListener {
                                                                    }
                                                            }
                                                        }
                                                        RemoveCart(remarks)
                                                    }

                                                    override fun onCancelled(databaseError: DatabaseError) {
                                                        // Handle possible errors
                                                        println("Error reading data: ${databaseError.message}")
                                                    }
                                                })
                                            }
                                        }
                                    }
                                    UpdateReceiptNo()
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Handle possible errors
                                    println("Error reading data: ${databaseError.message}")
                                }
                            })
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                println("Error reading data: ${databaseError.message}")
            }
        })
    }

    private fun UpdateReceiptNo() {//Update the receipt number in Firebase into a new number to not confuse same order number between customers
        // Attach a listener to read the data at the "receipt" path
        databaseReceipt.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the value of the receipt
                val receiptValue =
                    ((dataSnapshot.getValue(Int::class.java)).toString()).toInt()
                val NewreceiptValue = receiptValue + 1
                databaseReceipt.setValue(NewreceiptValue).addOnSuccessListener {
                }
                UpdateWallet()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                println("Error reading data: ${databaseError.message}")
            }
        })
    }

    private fun RemoveCart(remarks:String) {//Remove Cart Item
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val query: Query = dbref.orderByChild("uid").equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {

                        // Get the user type from the snapshot
                        val EMail = userSnapshot.child("email").getValue(String::class.java)
                        if (EMail != null) {
                            val sanitizedEmail = extractEmailBeforeDot(EMail)
                            val dbRefUser =
                                FirebaseDatabase.getInstance().getReference("${sanitizedEmail}Cart")
                            val Checkquery: Query = dbRefUser.orderByChild("checkbox").equalTo(true)
                            Checkquery.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            val hawkerName = userSnapshot.child("hawkerName")
                                                .getValue(String::class.java)
                                            val foodName = userSnapshot.child("foodName")
                                                .getValue(String::class.java)
                                            val removeRef =
                                                dbRefUser.child("${hawkerName} ${foodName} ${remarks}")
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
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Handle possible errors
                                    println("Error reading data: ${databaseError.message}")
                                }
                            })
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                println("Error reading data: ${databaseError.message}")
            }
        })
    }

    private fun UpdateWallet() {//Update wallet amount of customer
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        // Get the user type from the snapshot
                        val WalletAmount =
                            userSnapshot.child("walletAmount").getValue(Double::class.java) ?: 0.0
                        val lastAmount = intent.getFloatExtra("lastAmount", 0.0f)
                        database = FirebaseDatabase.getInstance().getReference("Users")
                        val uid = auth.currentUser?.uid
                        database.child("${uid}/walletAmount").setValue(lastAmount)
                            .addOnSuccessListener {
                            }.addOnFailureListener {
                            }
                    }
                }
            }override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                println("Error reading data: ${databaseError.message}")
            }
        })
    }

}

package com.example.eatatnotts.MyOrders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.History.HistoryPickUpDetailItem
import com.example.eatatnotts.History.HistoryPickUpNumberItem
import com.example.eatatnotts.MyHistory.MyHistoryDateItem
import com.example.eatatnotts.MyHistory.MyHistoryPickUpDetailItem
import com.example.eatatnotts.MyHistory.MyHistoryPickUpNumberItem
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.ActivityMyOrdersPickUpDetailsBinding
import com.example.eatatnotts.mainpage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyOrdersPickUpDetails : AppCompatActivity(), MyOrdersAdapter.OnItemClickListener {//Create View for MyOrdersPickUpDetails Activity and retrieve user data and handle button logics
    private lateinit var binding: ActivityMyOrdersPickUpDetailsBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var dbrefCustomer: DatabaseReference
    private lateinit var dbrefNumber: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var MyOrdersArrayList: ArrayList<MyOrderItem>
    private lateinit var auth: FirebaseAuth
    private lateinit var checkBoxComplete: CheckBox
    private var CustomerStatus: String? = null
    private var date: String? = null
    private var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyOrdersPickUpDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receiptNo = intent.getStringExtra("receipt")
        binding.MyOrderstopAppBar.title = "Order No. $receiptNo"
        binding.MyOrderstopAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkBoxComplete = binding.CheckboxOrderComplete

        // Start the notification service

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val EMail = userSnapshot.child("email").getValue(String::class.java)
                        Log.i("MYTAG", "I'm in OnCreate")
                        userRecyclerview = binding.MyOrdersDetailslist
                        userRecyclerview.layoutManager =
                            LinearLayoutManager(this@MyOrdersPickUpDetails)
                        userRecyclerview.setHasFixedSize(true)

                        MyOrdersArrayList = arrayListOf()

                        getUserData(userName, EMail)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })


        binding.btnMyOrdersUpdate.setOnClickListener {
            val allStatusesPending = MyOrdersArrayList.all {
                it.status == "Pending Customer Approval" || it.status == "Rejected"
            }

            val allStatusesReady = MyOrdersArrayList.all {
                it.status == "Ready for Pick Up" || it.status == "Rejected"
            }

            if (allStatusesPending) {
                if (checkBoxComplete.isChecked) {
                    val alertMessage = AlertDialog.Builder(this@MyOrdersPickUpDetails)
                    alertMessage.setTitle("Status Update Confirmation")
                    alertMessage.setMessage("Are you sure you want to update the status of this order?")
                    alertMessage.setCancelable(false)
                    alertMessage.setPositiveButton("Yes") { dialog, which ->
                        CustomerStatus = "Order Completed"
                        UpdateStatus()
                        val mainPageIntent = Intent(this@MyOrdersPickUpDetails, mainpage::class.java)
                        startActivity(mainPageIntent)
                        finish()
                    }
                    alertMessage.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                    alertMessage.show()
                } else {
                    Toast.makeText(
                        this@MyOrdersPickUpDetails,
                        "You didn't check the checkbox!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (allStatusesReady) {
                if (checkBoxComplete.isChecked) {
                    val alertMessage = AlertDialog.Builder(this@MyOrdersPickUpDetails)
                    alertMessage.setTitle("Status Update Confirmation")
                    alertMessage.setMessage("Are you sure you want to update the status of this order?")
                    alertMessage.setCancelable(false)
                    alertMessage.setPositiveButton("Yes") { dialog, which ->
                        CustomerStatus = "Pending Hawker Approval"
                        UpdateStatusOnly()
                        val mainPageIntent = Intent(this@MyOrdersPickUpDetails, mainpage::class.java)
                        startActivity(mainPageIntent)
                        finish()
                    }
                    alertMessage.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                    alertMessage.show()
                } else {
                    Toast.makeText(
                        this@MyOrdersPickUpDetails,
                        "You didn't check the checkbox!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@MyOrdersPickUpDetails,
                    "Your order is not completed!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getUserData(userName: String?, EMail: String?) {// Get user data and search for Order Details from Firebase Real-Time Database
        val sanitizedEmail = extractEmailBeforeDot(EMail.toString())
        val receiptNo = intent.getStringExtra("receipt")
        dbref =
            FirebaseDatabase.getInstance()
                .getReference("${sanitizedEmail} Order Pick Up $receiptNo")
        Log.i("MYTAG", "I am in getUserData $sanitizedEmail Order Pick Up $receiptNo")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                MyOrdersArrayList.clear()
                val notificationintent = Intent(this@MyOrdersPickUpDetails, MyOrdersPickUpDetailsNotifications::class.java)
                notificationintent.putExtra("receipt", receiptNo)
                this@MyOrdersPickUpDetails.startService(notificationintent)
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val myorder = userSnapshot.getValue(MyOrderItem::class.java)
                        Log.i("MYTAG", "myorder is ${myorder}")
                        if (myorder != null) {
                            MyOrdersArrayList.add(myorder)
                        }
                    }
                    userRecyclerview.adapter?.notifyDataSetChanged()
                }
                if (userRecyclerview.adapter == null) {
                    userRecyclerview.adapter =
                        MyOrdersAdapter(MyOrdersArrayList, this@MyOrdersPickUpDetails)
                } else {
                    userRecyclerview.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    override fun onItemClick(myordersnumber: MyOrderItem) {
    }

    private fun extractEmailBeforeDot(email: String): String {//Extract username from email before @
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex)
        } else {
            email // In case there's no dot, return the original email
        }
    }

    private fun UpdateStatusOnly() {//Update Status of order and not delete the order
        val receiptNo = intent.getStringExtra("receipt").toString()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        val sanitizedEmail = extractEmailBeforeDot(email.toString())
                        dbrefCustomer = FirebaseDatabase.getInstance()
                            .getReference("${sanitizedEmail} Order Pick Up ${receiptNo}")
                        val ReceiptQuery: Query =
                            dbrefCustomer.orderByChild("orderNo").equalTo(receiptNo)
                        Log.i(
                            "MYTAG",
                            "I am in UpdateStatus $sanitizedEmail Order Pick Up $receiptNo"
                        )

                        ReceiptQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (userSnapshot in snapshot.children) {
                                        val foodName = userSnapshot.child("orderList")
                                            .getValue(String::class.java)
                                        val hawkerName = userSnapshot.child("hawker")
                                            .getValue(String::class.java)
                                        val remarks = userSnapshot.child("remarks")
                                            .getValue(String::class.java)
                                        val currentstatus = userSnapshot.child("status")
                                            .getValue(String::class.java)

                                        if (currentstatus != "Rejected") {
                                            dbrefCustomer.child("${hawkerName} ${foodName} ${remarks}/status")
                                                .setValue(CustomerStatus)
                                            dbrefNumber = FirebaseDatabase.getInstance()
                                                .getReference("${hawkerName} Pick Up/${receiptNo}")
                                            dbrefNumber.child("status").setValue(CustomerStatus)
                                            val dbrefHawkerOrderDetail =
                                                FirebaseDatabase.getInstance()
                                                    .getReference("${hawkerName} Pick Up ${receiptNo}/${receiptNo}/${hawkerName} ${foodName} ${remarks}")
                                            dbrefHawkerOrderDetail.child("status")
                                                .setValue(CustomerStatus)
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }

    private fun UpdateStatus() {//Update status of order and delete the order
        val receiptNo = intent.getStringExtra("receipt").toString()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        val sanitizedEmail = extractEmailBeforeDot(email.toString())
                        dbrefCustomer = FirebaseDatabase.getInstance()
                            .getReference("${sanitizedEmail} Order Pick Up ${receiptNo}")
                        val ReceiptQuery: Query =
                            dbrefCustomer.orderByChild("orderNo").equalTo(receiptNo)
                        Log.i(
                            "MYTAG",
                            "I am in UpdateStatus $sanitizedEmail Order Pick Up $receiptNo"
                        )

                        ReceiptQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (userSnapshot in snapshot.children) {
                                        val foodName = userSnapshot.child("orderList")
                                            .getValue(String::class.java)
                                        val hawkerName = userSnapshot.child("hawker")
                                            .getValue(String::class.java)
                                        val remarks = userSnapshot.child("remarks")
                                            .getValue(String::class.java)
                                        val currentstatus = userSnapshot.child("status")
                                            .getValue(String::class.java)

                                        if (currentstatus != "Rejected") {
                                            dbrefCustomer.child("${hawkerName} ${foodName} ${remarks}/status")
                                                .setValue(CustomerStatus)
                                            dbrefNumber = FirebaseDatabase.getInstance()
                                                .getReference("${hawkerName} Pick Up/${receiptNo}")
                                            dbrefNumber.child("status").setValue(CustomerStatus)
                                            val dbrefHawkerOrderDetail =
                                                FirebaseDatabase.getInstance()
                                                    .getReference("${hawkerName} Pick Up ${receiptNo}/${receiptNo}/${hawkerName} ${foodName} ${remarks}")
                                            dbrefHawkerOrderDetail.child("status")
                                                .setValue(CustomerStatus)
                                        }
                                    }
                                    AddHistory()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }

    private fun AddHistory() {//Add order into history
        val receiptNo = intent.getStringExtra("receipt").toString()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        Log.i("MYTAG", "I am in AddHistory 1")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.i("MYTAG", "I am in AddHistory 2")
                    for (userSnapshot in snapshot.children) {
                        Log.i("MYTAG", "I am in AddHistory 3")
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        val sanitizedEmail = extractEmailBeforeDot(email.toString())
                        val dbrefCustomer = FirebaseDatabase.getInstance()
                            .getReference("${sanitizedEmail} Order Pick Up")
                        val ReceiptQuery: Query =
                            dbrefCustomer.orderByChild("receipt").equalTo(receiptNo)
                        ReceiptQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                Log.i("MYTAG", "I am in AddHistory 5")
                                if (snapshot.exists()) {
                                    Log.i("MYTAG", "I am in AddHistory 4")
                                    for (userSnapshot in snapshot.children) {
                                        val customerEmail = userSnapshot.child("customerEmail")
                                            .getValue(String::class.java)
                                        val sanitizedEmail =
                                            extractEmailBeforeDot(customerEmail.toString())
                                        date =
                                            userSnapshot.child("date").getValue(String::class.java)
                                        val method = userSnapshot.child("method")
                                            .getValue(String::class.java)
                                        val time =
                                            userSnapshot.child("time").getValue(String::class.java)
                                        val HistoryPickUp = MyHistoryPickUpNumberItem(
                                            time, date, customerEmail, receiptNo, method
                                        )
                                        val dateitem= MyHistoryDateItem(
                                            date
                                        )
                                        Log.i("MYTAG", "time is ${time}")
                                        val dbRefMyHistoryDate = FirebaseDatabase.getInstance()
                                            .getReference("${sanitizedEmail} Pick Up History Date")
                                        val dbRefMyHistoryReceiptNo = FirebaseDatabase.getInstance()
                                            .getReference("${sanitizedEmail} History Pick Up/${date}")
                                        dbRefMyHistoryDate.child("${date}")
                                            .setValue(dateitem)
                                            .addOnSuccessListener {

                                            }
                                        dbRefMyHistoryReceiptNo.child("${receiptNo}")
                                            .setValue(HistoryPickUp)
                                            .addOnSuccessListener {

                                            }

                                    }
                                    AddHistoryHawker()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }

    private fun AddHistoryHawker() {//Add order into hawker history
        val receiptNo = intent.getStringExtra("receipt").toString()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        Log.i("MYTAG", "I am in AddHistoryHawker 1")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.i("MYTAG", "I am in AddHistoryHawker 2")
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        val sanitizedEmail = extractEmailBeforeDot(email.toString())
                        dbrefCustomer = FirebaseDatabase.getInstance()
                            .getReference("$sanitizedEmail Order Pick Up $receiptNo")
                        val ReceiptQuery: Query =
                            dbrefCustomer.orderByChild("orderNo").equalTo(receiptNo)
                        Log.i("MYTAG", "$sanitizedEmail Order Pick Up $receiptNo")
                        ReceiptQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    Log.i("MYTAG", "I am in AddHistoryHawker 3")
                                    for (userSnapshot in snapshot.children) {
                                        val foodName = userSnapshot.child("orderList")
                                            .getValue(String::class.java)
                                        val hawkerName = userSnapshot.child("hawker")
                                            .getValue(String::class.java)
                                        val remarks = userSnapshot.child("remarks")
                                            .getValue(String::class.java)
                                        val currentstatus = userSnapshot.child("status")
                                            .getValue(String::class.java)
                                        val dbrefHawker=FirebaseDatabase.getInstance()
                                            .getReference("$sanitizedEmail Order Pick Up")
                                        val ReceiptQuery: Query =
                                            dbrefHawker.orderByChild("receipt").equalTo(receiptNo)
                                        ReceiptQuery.addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    for (userSnapshot in snapshot.children) {
                                                        val time =
                                                            userSnapshot.child("time")
                                                                .getValue(String::class.java)
                                                        val HistoryPickUp = HistoryPickUpNumberItem(
                                                            email,
                                                            date,
                                                            receiptNo,
                                                            currentstatus,
                                                            time
                                                        )
                                                        val dbRefHistoryDate =
                                                            FirebaseDatabase.getInstance()
                                                                .getReference("${hawkerName} Pick Up History Date")
                                                        val dbRefHistoryReceiptNo =
                                                            FirebaseDatabase.getInstance()
                                                                .getReference("${hawkerName} History Pick Up/${date}")
                                                        val dateitem= MyHistoryDateItem(
                                                            date
                                                        )
                                                        dbRefHistoryDate.child("${date}")
                                                            .setValue(dateitem)
                                                            .addOnSuccessListener {

                                                            }
                                                        dbRefHistoryReceiptNo.child("${receiptNo}")
                                                            .setValue(HistoryPickUp)
                                                            .addOnSuccessListener {

                                                            }
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Log.e(
                                                    "FirebaseError",
                                                    "Error fetching user data: ${error.message}"
                                                )
                                            }
                                        })
                                    }
                                    AddHistoryDetails()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }


    private fun AddHistoryDetails() {//Add order details into history
        Log.i("MYTAG", "I am in AddHistoryDetails 1")
        val receiptNo = intent.getStringExtra("receipt").toString()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        Log.i("MYTAG", "I am in AddHistoryDetails 2")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.i("MYTAG", "I am in AddHistoryDetails 3")
                    for (userSnapshot in snapshot.children) {
                        Log.i("MYTAG", "I am in AddHistoryDetails 4")
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        val sanitizedEmail = extractEmailBeforeDot(email.toString())
                        val dbrefCustomerDetail = FirebaseDatabase.getInstance()
                            .getReference("$sanitizedEmail Order Pick Up ${receiptNo}")
                        Log.i("MYTAG", "$sanitizedEmail Order Pick Up ${receiptNo}")
                        val ReceiptQuery: Query =
                            dbrefCustomerDetail.orderByChild("orderNo").equalTo(receiptNo)
                        ReceiptQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (userSnapshot in snapshot.children) {
                                        val hawkerName = userSnapshot.child("hawker")
                                            .getValue(String::class.java)
                                        val method =
                                            userSnapshot.child("method")
                                                .getValue(String::class.java)
                                        val foodName = userSnapshot.child("orderList")
                                            .getValue(String::class.java)
                                        val quantity =
                                            (userSnapshot.child("quantity")
                                                .getValue(Int::class.java)).toString().toInt()
                                        val rejectreason =
                                            userSnapshot.child("rejectreason")
                                                .getValue(String::class.java)
                                        val remarks =
                                            userSnapshot.child("remarks")
                                                .getValue(String::class.java)
                                        val status =
                                            userSnapshot.child("status")
                                                .getValue(String::class.java)
                                        val HistoryPickUp = MyHistoryPickUpDetailItem(
                                            hawkerName,
                                            method,
                                            foodName,
                                            receiptNo,
                                            quantity,
                                            rejectreason,
                                            remarks,
                                            status
                                        )
                                        val dbRefMyHistoryDetail = FirebaseDatabase.getInstance()
                                            .getReference("${sanitizedEmail} Pick Up History ${date}")
                                        dbRefMyHistoryDetail.child("${receiptNo}/${hawkerName} ${foodName} ${remarks}")
                                            .setValue(HistoryPickUp)
                                            .addOnSuccessListener {

                                            }
                                    }
                                    AddHistoryDetailsHawker()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }

    private fun AddHistoryDetailsHawker() {// Add order details into hawker history
        val receiptNo = intent.getStringExtra("receipt").toString()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        Log.i("MYTAG", "I am in AddHistoryDetailsHawker 1")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.i("MYTAG", "I am in AddHistoryDetailsHawker 2")
                    for (userSnapshot in snapshot.children) {
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        val sanitizedEmail = extractEmailBeforeDot(email.toString())
                        val dbrefCustomerDetail = FirebaseDatabase.getInstance()
                            .getReference("$sanitizedEmail Order Pick Up ${receiptNo}")
                        val ReceiptQuery: Query =
                            dbrefCustomerDetail.orderByChild("orderNo").equalTo(receiptNo)
                        ReceiptQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    Log.i("MYTAG", "I am in AddHistoryDetailsHawker 3")
                                    for (userSnapshot in snapshot.children) {
                                        val hawkerName = userSnapshot.child("hawker")
                                            .getValue(String::class.java)
                                        val method =
                                            userSnapshot.child("method")
                                                .getValue(String::class.java)
                                        val foodName = userSnapshot.child("orderList")
                                            .getValue(String::class.java)
                                        val quantity =
                                            (userSnapshot.child("quantity")
                                                .getValue(Int::class.java)).toString().toInt()
                                        val remarks =
                                            userSnapshot.child("remarks")
                                                .getValue(String::class.java)
                                        val status =
                                            userSnapshot.child("status")
                                                .getValue(String::class.java)
                                        val HistoryPickUp = HistoryPickUpDetailItem(
                                            email,
                                            foodName,
                                            method,
                                            quantity,
                                            receiptNo,
                                            remarks,
                                            status
                                        )
                                        val dbRefMyHistoryDetail = FirebaseDatabase.getInstance()
                                            .getReference("${hawkerName} Pick Up History ${date}")
                                        dbRefMyHistoryDetail.child("${receiptNo}/${hawkerName} ${foodName} ${remarks}")
                                            .setValue(HistoryPickUp)
                                            .addOnSuccessListener {

                                            }
                                        RemoveOrder(sanitizedEmail,hawkerName,foodName,remarks)
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }

    private fun RemoveOrder(sanitizedEmail:String?,hawkerName:String?,foodName:String?,remarks:String?) {//Removes completed orders
        val receiptNo = intent.getStringExtra("receipt").toString()
        val dbrefHawker = FirebaseDatabase.getInstance()
            .getReference("${hawkerName} Pick Up ${receiptNo}")
        val removeHawkerRef = dbrefHawker.child("${receiptNo}")
        val dbrefHawkerNumber = FirebaseDatabase.getInstance()
            .getReference("${hawkerName} Pick Up")
        val removeHawkerNumberRef = dbrefHawkerNumber.child("${receiptNo}")
        val dbrefCustomer = FirebaseDatabase.getInstance()
            .getReference("${sanitizedEmail} Order Pick Up ${receiptNo}")
        val removeCustomerRef = dbrefCustomer.child("${hawkerName} ${foodName} ${remarks}")
        val dbrefCustomerNumber = FirebaseDatabase.getInstance()
            .getReference("${sanitizedEmail} Order Pick Up")
        val removeCustomerNumberRef = dbrefCustomerNumber.child("${receiptNo}")


        removeHawkerRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("Firebase", "User successfully deleted.")
            } else {
                Log.i(
                    "Firebase",
                    "Failed to delete user: ${task.exception?.message}"
                )
            }
        }
        removeHawkerNumberRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("Firebase", "User successfully deleted.")
            } else {
                Log.i(
                    "Firebase",
                    "Failed to delete user: ${task.exception?.message}"
                )
            }
        }
        removeCustomerRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("Firebase", "User successfully deleted.")
            } else {
                Log.i(
                    "Firebase",
                    "Failed to delete user: ${task.exception?.message}"
                )
            }
        }
        removeCustomerNumberRef.removeValue().addOnCompleteListener { task ->
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



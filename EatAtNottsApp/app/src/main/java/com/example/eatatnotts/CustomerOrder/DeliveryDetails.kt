package com.example.eatatnotts.CustomerOrder

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
import com.example.eatatnotts.MainpageHawker
import com.example.eatatnotts.databinding.ActivityCustomerOrderDeliveryDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class DeliveryDetails : AppCompatActivity(), DeliveryDetailsAdapter.OnItemClickListener {
    private lateinit var binding: ActivityCustomerOrderDeliveryDetailsBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var dbrefNumber: DatabaseReference
    private lateinit var dbrefCustomerNumber: DatabaseReference
    private lateinit var dbrefCustomer: DatabaseReference
    private lateinit var dbrefCustomerOrderStatus: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var MyOrdersArrayList: ArrayList<CustomerOrderItem>
    private lateinit var auth: FirebaseAuth
    private lateinit var checkBoxPreparing: CheckBox
    private lateinit var checkBoxReady: CheckBox
    private lateinit var checkBoxComplete: CheckBox
    private lateinit var checkBoxOnDelivery: CheckBox
    private var OrderStatus: String? = null
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {//Create View for Delivery Details Activity and Handle Button Logics
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCustomerOrderDeliveryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val receiptNo = intent.getStringExtra("receipt")
        binding.DeliveryDetailstopAppBar.title = "Order No. $receiptNo"
        binding.DeliveryDetailstopAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkBoxPreparing = binding.CheckboxPreparing
        checkBoxReady = binding.CheckboxReady
        checkBoxOnDelivery = binding.CheckboxOnDelivery
        checkBoxComplete = binding.CheckboxOrderComplete

        getUser()

        binding.btnDeliveryDetailsUpdate.setOnClickListener {
            if (!checkBoxPreparing.isChecked && !checkBoxReady.isChecked && !checkBoxComplete.isChecked && !checkBoxOnDelivery.isChecked) {
                Toast.makeText(
                    this@DeliveryDetails,
                    "You did not check on any of the checkboxes!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val alertMessage = AlertDialog.Builder(this@DeliveryDetails)
                alertMessage.setTitle("Status Update Confirmation")
                alertMessage.setMessage("Are you sure you want to update the status of this order?")
                alertMessage.setCancelable(false)
                alertMessage.setPositiveButton("Yes") { dialog, which ->
                    UpdateStatusOnly()
                    val mainPageIntent = Intent(this@DeliveryDetails, MainpageHawker::class.java)
                    startActivity(mainPageIntent)
                    finish()
                }

                alertMessage.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                alertMessage.show()
            }
        }
    }


    private fun getUser() {//Get user data and checkbox status
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
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        userRecyclerview = binding.DeliveryDetaillist
                        userRecyclerview.layoutManager = LinearLayoutManager(this@DeliveryDetails)
                        userRecyclerview.setHasFixedSize(true)

                        MyOrdersArrayList = arrayListOf()

                        getUserData(userName, email)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })

        checkBoxPreparing.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                uncheckAllExcept(checkBoxPreparing)
                OrderStatus = "Preparing"
            }
        }

        checkBoxOnDelivery.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                uncheckAllExcept(checkBoxReady)
                OrderStatus = "Delivery On The Way"
            }
        }

        checkBoxReady.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                uncheckAllExcept(checkBoxReady)
                OrderStatus = "Ready for Delivery"
            }
        }

        checkBoxComplete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                uncheckAllExcept(checkBoxComplete)
                CheckCurrentStatus()
            }
        }
    }

    private fun uncheckAllExcept(except: CheckBox) {//Uncheck other checkboxes when one checkbox is checked
        if (checkBoxPreparing != except) {
            checkBoxPreparing.isChecked = false
        }
        if (checkBoxReady != except) {
            checkBoxReady.isChecked = false
        }
        if (checkBoxComplete != except) {
            checkBoxComplete.isChecked = false
        }
    }

    private fun getUserData(userName: String?, email: String?) {//Get User Data and Delivery Order Data from Firebase and add into RecyclerView
        val receiptNo = intent.getStringExtra("receipt").toString()
        dbref = FirebaseDatabase.getInstance()
            .getReference("${userName} Delivery ${receiptNo}/${receiptNo}")
        Log.i("MYTAG", "${userName} Delivery ${receiptNo}/${receiptNo}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MyOrdersArrayList.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val myorder = userSnapshot.getValue<CustomerOrderItem>()
                        if (myorder != null) {
                            MyOrdersArrayList.add(myorder)
                        }
                    }
                    userRecyclerview.adapter?.notifyDataSetChanged()
                }
                if (userRecyclerview.adapter == null) {
                    userRecyclerview.adapter =
                        DeliveryDetailsAdapter(MyOrdersArrayList, this@DeliveryDetails)
                } else {
                    userRecyclerview.adapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun UpdateStatusOnly() {//Get Delivery Order Path and Update Status of Delivery Order
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
                        // Get the user type from the snapshot
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        dbrefCustomer = FirebaseDatabase.getInstance()
                            .getReference("${userName} Delivery ${receiptNo}/${receiptNo}")
                        dbrefNumber = FirebaseDatabase.getInstance()
                            .getReference("${userName} Delivery/${receiptNo}")
                        Log.i("MYTAG", "I am here ${userName} Delivery ${receiptNo}")

                        // Update the order status
                        updateOrderStatusOnly(userName, receiptNo, email)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }

    private fun updateOrderStatusOnly(userName: String?, receiptNo: String, email: String?) {//Update New Delivery Order Status
        dbrefCustomer.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val foodName = userSnapshot.child("foodName").getValue(String::class.java)
                        val customerEmail =
                            userSnapshot.child("customerEmail").getValue(String::class.java)
                        val remarks = userSnapshot.child("remarks").getValue(String::class.java)
                        dbrefCustomer.child("${userName} ${foodName} ${remarks}/status")
                            .setValue(OrderStatus)
                        dbrefNumber.child("status").setValue(OrderStatus)
                        val sanitizedEmail = extractEmailBeforeDot(customerEmail.toString())
                        val dbrefCustomerNumber = FirebaseDatabase.getInstance()
                            .getReference("${sanitizedEmail} Order Delivery ${receiptNo}/${userName} ${foodName} ${remarks}")
                        dbrefCustomerNumber.child("status").setValue(OrderStatus)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    override fun onItemClick(myordersnumber: CustomerOrderItem) {
    }

    private fun extractEmailBeforeDot(email: String): String {//Extract Username of user before @ in their email
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex + email.substring(firstDotIndex).indexOf('@'))
        } else {
            email // In case there's no dot, return the original email
        }
    }

    private fun CheckCurrentStatus() {//Check the current status of the order
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
                        // Get the user type from the snapshot
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        dbrefCustomerOrderStatus = FirebaseDatabase.getInstance()
                            .getReference("${userName} Delivery ${receiptNo}/${receiptNo}")
                        val StatusQuery: Query =
                            dbrefCustomerOrderStatus.orderByChild("receipt").equalTo(receiptNo)
                        StatusQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (userSnapshot in snapshot.children) {
                                        val Status = userSnapshot.child("status")
                                            .getValue(String::class.java).toString()
                                        if (Status == "Pending Hawker Approval") {
                                            OrderStatus = "Order Completed"
                                        } else {
                                            OrderStatus = "Pending Customer Approval"
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle the error, log it, or show a message
                                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }
}
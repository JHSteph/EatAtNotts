package com.example.eatatnotts.Pending

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.CustomerOrder.CustomerOrderItem
import com.example.eatatnotts.CustomerOrder.CustomerOrderNumber
import com.example.eatatnotts.CustomerOrder.CustomerOrderNumberDelivery
import com.example.eatatnotts.MainpageHawker
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.ActivityPendingDeliveryDetailsBinding
import com.example.eatatnotts.databinding.ActivityPendingPickUpDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PendingPickUpDetails : AppCompatActivity(), PendingPickUpDetailsAdapter.OnItemClickListener {
    private lateinit var binding: ActivityPendingPickUpDetailsBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var dbrefNumber:DatabaseReference
    private lateinit var dbrefHawker: DatabaseReference
    private lateinit var dbrefHawkerNumber: DatabaseReference
    private lateinit var dbrefHawkerNumberPending: DatabaseReference
    private lateinit var dbrefCustomer: DatabaseReference
    private lateinit var dbrefUpdateCustomerNumber: DatabaseReference
    private lateinit var dbrefNewPickUpNumber: DatabaseReference
    private lateinit var dbrefFoodPrice: DatabaseReference
    private lateinit var dbrefHawkerWallet: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var MyOrdersArrayList: ArrayList<PendingItem>
    private lateinit var auth: FirebaseAuth
    private var OrderStatus:String?=null
    private var CustomerUserName:String?=null
    private var TotalPrice=0.0

    override fun onCreate(savedInstanceState: Bundle?) {//Create View for Pending Pick Up Details Activity and Handle Button Logics
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPendingPickUpDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val receiptNo = intent.getStringExtra("receipt").toString()
        binding.PendingPickUpDetailtopAppBar.title = "Order No. $receiptNo"
        binding.PendingPickUpDetailtopAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Start the notification service
        val intent = Intent(this@PendingPickUpDetails, NotificationService::class.java)
        this@PendingPickUpDetails.startService(intent)
        getUser()

        binding.btnAccept.setOnClickListener{
            val AlertMessage = AlertDialog.Builder(this@PendingPickUpDetails)
            AlertMessage.setTitle("Accept Confirmation")
            AlertMessage.setMessage("Are you sure you want to accept this order?")
            AlertMessage.setCancelable(false)
            AlertMessage.setPositiveButton("Yes") { dialog, which ->
                UpdateOrderNumber()
                UpdateOrder()
                val intent = Intent(this@PendingPickUpDetails, MainpageHawker::class.java)
                startActivity(intent)
                finish()
            }
            AlertMessage.setNegativeButton("No") {
                // If user click no then dialog box is canceled.
                    dialog, which -> dialog.dismiss()
            }

            // Create the Alert dialog
            val alertDialog = AlertMessage.create()
            // Show the Alert Dialog box
            alertDialog.show()
            // Show confirmation dialog
        }
        binding.btnReject.setOnClickListener{
            UpdateCustomerStatus()
            val intent = Intent(this@PendingPickUpDetails, RejectReason::class.java)
            intent.putExtra("receipt",receiptNo)
            intent.putExtra("method","Pick Up")
            intent.putExtra("customerUsername",CustomerUserName)
            startActivity(intent)
            finish()
        }

    }

    private fun getUser() {//Get user data
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
                        val EMail = userSnapshot.child("email").getValue(String::class.java)
                        userRecyclerview = binding.PendingPickUpDetaillist
                        userRecyclerview.layoutManager = LinearLayoutManager(this@PendingPickUpDetails)
                        userRecyclerview.setHasFixedSize(true)

                        MyOrdersArrayList = arrayListOf()

                        getUserData(userName, EMail)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }



    private fun getUserData(userName: String?, email: String?) {//Get user data, pending order details and create RecyclerView
        val receiptNo = intent.getStringExtra("receipt").toString()
        dbref = FirebaseDatabase.getInstance().getReference("${userName} Pick Up ${receiptNo} Pending/${receiptNo}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MyOrdersArrayList.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val myorder = userSnapshot.getValue(PendingItem::class.java)
                        if (myorder != null) {
                            MyOrdersArrayList.add(myorder!!)
                        }
                    }
                    userRecyclerview.adapter = PendingPickUpDetailsAdapter(MyOrdersArrayList, this@PendingPickUpDetails)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }


    private fun UpdateOrder() {//Get Pick Up Order Path, update status of delivery order and add order to customer order page
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
                        dbrefHawker = FirebaseDatabase.getInstance().getReference("${userName} Pick Up ${receiptNo} Pending/${receiptNo}")
                        dbrefHawker.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (userSnapshot in snapshot.children) {
                                        val customerEmail = userSnapshot.child("customerEmail")
                                            .getValue(String::class.java)
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
                                        val remarks=userSnapshot.child("remarks")
                                            .getValue(String::class.java)
                                        val sanitizedEmail = extractEmailBeforeDot((customerEmail).toString())
                                        dbrefUpdateCustomerNumber=FirebaseDatabase.getInstance().getReference("${sanitizedEmail} Order Pick Up ${receiptNo}")
                                        val customerorder = CustomerOrderItem(
                                            receipt,
                                            customerEmail,
                                            foodName,
                                            quantity,
                                            "Preparing",
                                            method,
                                            remarks
                                        )
                                        AddMoneyAmount(
                                            foodName,
                                            quantity,
                                            userName
                                        )
                                        dbrefNewPickUpNumber = FirebaseDatabase.getInstance()
                                            .getReference("${userName} Pick Up ${receiptNo}/${receiptNo}")
                                        dbrefNewPickUpNumber.child("${userName} ${foodName} ${remarks}")
                                            .setValue(customerorder)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    this@PendingPickUpDetails,
                                                    "Succesfully Added Order!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        ChangeUserStatus(sanitizedEmail,receiptNo,status,foodName,remarks,userName)
                                        RemovePendingOrder(userName,receiptNo,foodName,remarks)
                                    }
                                }
                                AddMoney()

                            }override fun onCancelled(error: DatabaseError) {
                                // Handle the error, log it, or show a message
                                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                            }
                        })
                    }
                }
            }override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")


            }
        })
    }

    private fun UpdateOrderNumber() {//Get Pick Up Order Number Path
        val receiptNo = intent.getStringExtra("receipt")
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
                        dbrefHawkerNumber = FirebaseDatabase.getInstance().getReference("${userName} Pick Up Pending")
                        dbrefHawkerNumber.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (userSnapshot in snapshot.children) {
                                        AddOrder(userName,receiptNo)
                                        RemovePendingOrderNumber(userName,receiptNo)
                                    }
                                }


                            }override fun onCancelled(error: DatabaseError) {
                                // Handle the error, log it, or show a message
                                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                            }
                        })
                    }
                }
            }override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")


            }
        })
    }

    private fun AddOrder(userName:String?,receiptNo:String?){//Add order to customer order page
        val Checkquery: Query = dbrefHawkerNumber.orderByChild("receipt").equalTo(receiptNo)
        Checkquery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val customerEmail = userSnapshot.child("customerEmail")
                            .getValue(String::class.java)
                        val date = userSnapshot.child("date")
                            .getValue(String::class.java)
                        val time = userSnapshot.child("time")
                            .getValue(String::class.java)
                        val receipt = userSnapshot.child("receipt")
                            .getValue(String::class.java)
                        val location = userSnapshot.child("location")
                            .getValue(String::class.java)
                        Log.i("MYTAG", "receipt is ${receipt}")
                        val customerorder = CustomerOrderNumber(
                            date,
                            time,
                            customerEmail,
                            receipt,
                            "Preparing"
                        )

                        dbrefNewPickUpNumber = FirebaseDatabase.getInstance()
                            .getReference("${userName} Pick Up")
                        dbrefNewPickUpNumber.child("${receiptNo}")
                            .setValue(customerorder)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@PendingPickUpDetails,
                                    "Succesfully Added Order!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }

    private fun ChangeUserStatus(sanitizedEmail:String,receiptNo:String?,status:String?=null,foodName:String?=null,remarks:String?=null,userName:String?=null){//Change Order Status at My Orders Page
        dbrefUpdateCustomerNumber.child("${userName} ${foodName} ${remarks}/status").setValue("Preparing")
            .addOnSuccessListener{Log.i("MYTAG","Data saved succesfully!")}
        Log.i("MYTAG","This is current status ${status}")
    }



    private fun RemovePendingOrderNumber(userName:String?,receipt:String?){//Remove Pending Pick Up Order Number
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

    private fun RemovePendingOrder(userName:String?,receipt:String?,foodName:String?,remarks:String?){//Remove Pending Pick Up Order Details
        dbrefHawkerNumberPending = FirebaseDatabase.getInstance()
            .getReference("${userName} Pick Up ${receipt} Pending")
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

    private fun UpdateCustomerStatus() {//Update Status of Order into rejected when order is rejected
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
                        dbrefHawkerNumber = FirebaseDatabase.getInstance()
                            .getReference("${userName} Pick Up Pending")
                        val Checkquery: Query =
                            dbrefHawkerNumber.orderByChild("receipt").equalTo(receiptNo)
                        Checkquery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (userSnapshot in snapshot.children) {
                                        val customerEmail = userSnapshot.child("customerEmail")
                                            .getValue(String::class.java)
                                        val date = userSnapshot.child("date")
                                            .getValue(String::class.java)
                                        val time = userSnapshot.child("time")
                                            .getValue(String::class.java)
                                        val receipt = userSnapshot.child("receipt")
                                            .getValue(String::class.java)
                                        val location = userSnapshot.child("location")
                                            .getValue(String::class.java)
                                        val sanitizedEmail = extractEmailBeforeDot((customerEmail).toString())
                                        CustomerUserName=sanitizedEmail
                                        Log.i("MYTAG", "receipt is ${receipt}")
                                        val customerorder = CustomerOrderNumber(
                                            date,
                                            time,
                                            customerEmail,
                                            receipt,
                                            "Rejected"
                                        )
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
            }override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
    }



//    private fun updateOrderStatus(userName: String?, receiptNo: String, email: String?) {
//        dbrefCustomer.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    for (userSnapshot in snapshot.children) {
//                        val foodName = userSnapshot.child("foodName").getValue(String::class.java)
//                        val customerEmail = userSnapshot.child("customerEmail").getValue(String::class.java)
//                        dbrefCustomer.child("${userName} ${foodName}/status").setValue(OrderStatus)
//                        dbrefNumber.child("status").setValue(OrderStatus)
//                        val sanitizedEmail = extractEmailBeforeDot(customerEmail.toString())
//                        val dbrefCustomerNumber = FirebaseDatabase.getInstance().getReference("${sanitizedEmail} Order ${receiptNo}/${userName} ${foodName}")
//                        dbrefCustomerNumber.child("status").setValue(OrderStatus)
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle possible errors.
//            }
//        })
//    }

    override fun onItemClick(myordersnumber: PendingItem) {//Check which item is clicked
        Toast.makeText(this@PendingPickUpDetails, "This item is clicked!", Toast.LENGTH_SHORT).show()
    }

    private fun extractEmailBeforeDot(email: String): String {//Extract username from email before @
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex + email.substring(firstDotIndex).indexOf('@'))
        } else {
            email // In case there's no dot, return the original email
        }
    }
    private fun AddMoneyAmount(foodName:String?,quantity:Int,userName:String?) {//Calculate amount of money to return to customer
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

    private fun AddMoney(){//Add money to customer account if order is rejected
        dbrefHawkerWallet=FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val ReturnQuery: Query = dbrefHawkerWallet.orderByChild("uid").equalTo(userId)
        ReturnQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val walletAmount = (userSnapshot.child("walletAmount")
                            .getValue(Double::class.java)).toString().toFloat()
                        val NewwalletAmount = walletAmount + TotalPrice
                        dbrefHawkerWallet.child("${userId}/walletAmount").setValue(NewwalletAmount)
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
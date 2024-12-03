package com.example.eatatnotts.History

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.History.HistoryDeliveryDetailsAdapter
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.ActivityHistoryDeliveryDetailsBinding
import com.example.eatatnotts.mainpage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryDeliveryDetails: AppCompatActivity(), HistoryDeliveryDetailsAdapter.OnItemClickListener {
    private lateinit var binding: ActivityHistoryDeliveryDetailsBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var MyOrdersArrayList: ArrayList<HistoryDeliveryDetailItem>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {//Create View for HisotryDeliveryDetails Activity

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityHistoryDeliveryDetailsBinding.inflate(layoutInflater)

        // Start the notification service
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        val receiptNo = intent.getStringExtra("receipt").toString()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        // Get the user type from the snapshot
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val EMail = userSnapshot.child("email").getValue(String::class.java)
                        userRecyclerview = binding.DeliveryDetaillist
                        userRecyclerview.layoutManager = LinearLayoutManager(this@HistoryDeliveryDetails)
                        userRecyclerview.setHasFixedSize(true)

                        MyOrdersArrayList = arrayListOf()

                        getUserData(userName, EMail)

                    }
                }
            }override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })
        val date = intent.getStringExtra("Date").toString()
        binding.HistoryDeliveryDetailstopAppBar.title = "Order No. ${receiptNo}"
        binding.HistoryDeliveryDetailstopAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setContentView(binding.root)
    }

    private fun getUserData(userName:String?, EMail:String?) {//Get user Data and create recyclerView
        val receiptNo = intent.getStringExtra("receipt").toString()
        val date = intent.getStringExtra("Date").toString()
        val sanitizedEmail = extractEmailBeforeDot((EMail).toString())
        dbref = FirebaseDatabase.getInstance().getReference("${userName} Delivery History ${date}/${receiptNo}")
        Log.i("MYTAG","The path is ${sanitizedEmail} History Delivery ${date}/${receiptNo} in HistoryDeliveryDetails")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MyOrdersArrayList.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val myorder = userSnapshot.getValue(HistoryDeliveryDetailItem::class.java)
                        Log.i("MYTAG", "${myorder}")
                        MyOrdersArrayList.add(myorder!!)
                    }
                    userRecyclerview.adapter = HistoryDeliveryDetailsAdapter(MyOrdersArrayList,this@HistoryDeliveryDetails)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    override fun onItemClick(HistoryDeliverydetail: HistoryDeliveryDetailItem) {
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




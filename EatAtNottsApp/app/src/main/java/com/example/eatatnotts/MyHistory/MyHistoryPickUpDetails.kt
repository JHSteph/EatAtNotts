package com.example.eatatnotts.MyHistory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.MyOrders.MyHistoryPickUpDetailsAdapter
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.ActivityMyHistoryPickUpDetailsBinding
import com.example.eatatnotts.mainpage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyHistoryPickUpDetails: AppCompatActivity(), MyHistoryPickUpDetailsAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMyHistoryPickUpDetailsBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var MyOrdersArrayList: ArrayList<MyHistoryPickUpDetailItem>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {//Create View for MyHistoryPickUpDetails Activity

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMyHistoryPickUpDetailsBinding.inflate(layoutInflater)

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
                        userRecyclerview = binding.MyHistoryDetailslist
                        userRecyclerview.layoutManager = LinearLayoutManager(this@MyHistoryPickUpDetails)
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
        binding.MyHistoryDetailtopAppBar.title = "Order No. ${receiptNo}"
        binding.MyHistoryDetailtopAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setContentView(binding.root)
    }

    private fun getUserData(userName:String?, EMail:String?) {//Get user Data and create recyclerView
        val receiptNo = intent.getStringExtra("receipt").toString()
        val date = intent.getStringExtra("Date").toString()
        val sanitizedEmail = extractEmailBeforeDot((EMail).toString())
        dbref = FirebaseDatabase.getInstance().getReference("${sanitizedEmail} Pick Up History ${date}/${receiptNo}")
        Log.i("MYTAG","The path is ${sanitizedEmail} History Pick Up ${date}/${receiptNo} in MyHistoryPickUpDetails")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MyOrdersArrayList.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val myorder = userSnapshot.getValue(MyHistoryPickUpDetailItem::class.java)
                        Log.i("MYTAG", "${myorder}")
                        MyOrdersArrayList.add(myorder!!)
                    }
                    userRecyclerview.adapter = MyHistoryPickUpDetailsAdapter(MyOrdersArrayList,this@MyHistoryPickUpDetails)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    override fun onItemClick(myhistorypickupdetail: MyHistoryPickUpDetailItem) {
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




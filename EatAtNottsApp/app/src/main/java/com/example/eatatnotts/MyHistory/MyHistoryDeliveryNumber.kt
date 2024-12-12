package com.example.eatatnotts.MyHistory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.ActivityMyHistoryDeliveryNumberBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyHistoryDeliveryNumber : AppCompatActivity(), MyHistoryDeliveryNumberAdapter.OnItemClickListener {//Shows My History Delivery Order Number in a List of RecyclerView
    private lateinit var binding: ActivityMyHistoryDeliveryNumberBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var MyOrdersArrayList: ArrayList<MyHistoryDeliveryNumberItem>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {//Create View, and handle button logics

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMyHistoryDeliveryNumberBinding.inflate(layoutInflater)

        // Start the notification service
        val date = intent.getStringExtra("Date").toString()
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
                        userRecyclerview = binding.MyOrderslist
                        userRecyclerview.layoutManager = LinearLayoutManager(this@MyHistoryDeliveryNumber)
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
        binding.MyHistoryDeliverytopAppBar.title = "Deliveries on ${date}"
        binding.MyHistoryDeliverytopAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setContentView(binding.root)
    }

    private fun getUserData(userName:String?, EMail:String?) {//Get User History Delivery Order Data
        val date = intent.getStringExtra("Date").toString()
        val sanitizedEmail = extractEmailBeforeDot((EMail).toString())
        dbref = FirebaseDatabase.getInstance().getReference("${sanitizedEmail} History Delivery/${date}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MyOrdersArrayList.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val myorder = userSnapshot.getValue(MyHistoryDeliveryNumberItem::class.java)
                        Log.i("MYTAG", "${myorder}")
                        MyOrdersArrayList.add(myorder!!)
                    }
                    userRecyclerview.adapter = MyHistoryDeliveryNumberAdapter(MyOrdersArrayList,this@MyHistoryDeliveryNumber)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    override fun onItemClick(myordersnumber: MyHistoryDeliveryNumberItem) {//Check which item is clicked and start another activity
        val intent = Intent(this@MyHistoryDeliveryNumber, MyHistoryDeliveryDetails::class.java)
        intent.putExtra("Date",myordersnumber.date)
        intent.putExtra("receipt",myordersnumber.receipt)
        startActivity(intent)
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




package com.example.eatatnotts.History

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.ActivityHistoryPickUpNumberBinding
import com.example.eatatnotts.mainpage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryPickUpNumber : AppCompatActivity(), HistoryPickUpNumberAdapter.OnItemClickListener {//Shows History Pick Up Order Number in a List of RecyclerView
    private lateinit var binding: ActivityHistoryPickUpNumberBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var MyOrdersArrayList: ArrayList<HistoryPickUpNumberItem>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {//Create View, and handle button logics

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityHistoryPickUpNumberBinding.inflate(layoutInflater)

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
                        // Get the user type from the snapshot
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val EMail = userSnapshot.child("email").getValue(String::class.java)
                        userRecyclerview = binding.MyOrderslist
                        userRecyclerview.layoutManager = LinearLayoutManager(this@HistoryPickUpNumber)
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
        binding.HistoryPickUptopAppBar.title = "Pick Ups on ${date}"
        binding.HistoryPickUptopAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setContentView(binding.root)
    }

    private fun getUserData(userName:String?, EMail:String?) {//Get User History Delivery Order Data
        val date = intent.getStringExtra("Date").toString()
        dbref = FirebaseDatabase.getInstance().getReference("${userName} History Pick Up/${date}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MyOrdersArrayList.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val myorder = userSnapshot.getValue(HistoryPickUpNumberItem::class.java)
                        Log.i("MYTAG", "${myorder}")
                        MyOrdersArrayList.add(myorder!!)
                    }
                    userRecyclerview.adapter = HistoryPickUpNumberAdapter(MyOrdersArrayList,this@HistoryPickUpNumber)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    override fun onItemClick(Historypickupnumber: HistoryPickUpNumberItem) {//Check which item is clicked and start another activity
        val intent = Intent(this@HistoryPickUpNumber, HistoryPickUpDetails::class.java)
        intent.putExtra("Date",Historypickupnumber.date)
        intent.putExtra("receipt",Historypickupnumber.receipt)
        startActivity(intent)
    }

}




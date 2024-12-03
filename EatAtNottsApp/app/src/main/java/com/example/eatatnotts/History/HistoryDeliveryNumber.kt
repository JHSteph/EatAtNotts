package com.example.eatatnotts.History

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.ActivityHistoryDeliveryNumberBinding
import com.example.eatatnotts.mainpage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryDeliveryNumber : AppCompatActivity(), HistoryDeliveryNumberAdapter.OnItemClickListener {//Shows History Delivery Order Number in a List of RecyclerView
    private lateinit var binding: ActivityHistoryDeliveryNumberBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var MyOrdersArrayList: ArrayList<HistoryDeliveryNumberItem>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {//Create View, and handle button logics

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityHistoryDeliveryNumberBinding.inflate(layoutInflater)

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
                        userRecyclerview.layoutManager = LinearLayoutManager(this@HistoryDeliveryNumber)
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
        binding.HistoryDeliverytopAppBar.title = "Deliveries on ${date}"
        binding.HistoryDeliverytopAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setContentView(binding.root)
    }

    private fun getUserData(userName:String?, EMail:String?) {//Get User History Delivery Order Data
        val date = intent.getStringExtra("Date").toString()
        dbref = FirebaseDatabase.getInstance().getReference("${userName} History Delivery/${date}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MyOrdersArrayList.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val myorder = userSnapshot.getValue(HistoryDeliveryNumberItem::class.java)
                        Log.i("MYTAG", "${myorder}")
                        MyOrdersArrayList.add(myorder!!)
                    }
                    userRecyclerview.adapter = HistoryDeliveryNumberAdapter(MyOrdersArrayList,this@HistoryDeliveryNumber)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    override fun onItemClick(HistoryDeliverynumber: HistoryDeliveryNumberItem) {//Check which item is clicked and start another activity
        val intent = Intent(this@HistoryDeliveryNumber, HistoryDeliveryDetails::class.java)
        intent.putExtra("Date",HistoryDeliverynumber.date)
        intent.putExtra("receipt",HistoryDeliverynumber.receipt)
        startActivity(intent)
    }

}




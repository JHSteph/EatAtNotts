package com.example.eatatnotts.MyOrders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.ActivityMyOrderDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyOrderDetails : AppCompatActivity(),MyOrdersAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMyOrderDetailsBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var dbrefNumber:DatabaseReference
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var MyOrdersArrayList: ArrayList<MyOrderItem>
    private lateinit var auth: FirebaseAuth
    private lateinit var checkBoxOrderNotYetCompleted: CheckBox
    private lateinit var checkBoxComplete: CheckBox
    private var CustomerStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {//Create View for MyOrderDetails Activity

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val receiptNo=intent.getStringExtra("receipt").toString()
        binding.MyOrderstopAppBar.title = "Order No. ${receiptNo}"
        binding.MyOrderstopAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkBoxOrderNotYetCompleted = binding.CheckboxNotYetCompleted
        checkBoxComplete = binding.CheckboxOrderComplete

        // Start the notification service
        val intent = Intent(this@MyOrderDetails, NotificationService::class.java)
        this@MyOrderDetails.startService(intent)
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
                        userRecyclerview = binding.MyOrdersDetailslist
                        userRecyclerview.layoutManager = LinearLayoutManager(this@MyOrderDetails)
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
        checkBoxOrderNotYetCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                uncheckAllExcept(checkBoxOrderNotYetCompleted)
                CustomerStatus = "Order Not Yet Completed"
            }
        }

        checkBoxComplete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                uncheckAllExcept(checkBoxComplete)
                CustomerStatus = "Order Completed"
            }
        }
    }

    private fun uncheckAllExcept(except: CheckBox) {
        if (checkBoxOrderNotYetCompleted != except) {
            checkBoxOrderNotYetCompleted.isChecked = false
        }
        if (checkBoxComplete != except) {
            checkBoxComplete.isChecked = false
        }
    }

    private fun getUserData(userName: String?, EMail: String?) {//Get user Data and create recyclerView
        val sanitizedEmail = extractEmailBeforeDot((EMail).toString())
        val receiptNo=intent.getStringExtra("receipt").toString()
        dbref = FirebaseDatabase.getInstance().getReference("${sanitizedEmail} Order Delivery ${receiptNo}")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MyOrdersArrayList.clear()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                                        val myorder = userSnapshot.getValue(MyOrderItem::class.java)

                                        if (myorder != null) {
                                            MyOrdersArrayList.add(myorder)
                                        }
                                    }
                                    userRecyclerview.adapter?.notifyDataSetChanged()

                    }
                    if (userRecyclerview.adapter == null) {
                        userRecyclerview.adapter = MyOrdersAdapter(MyOrdersArrayList, this@MyOrderDetails)
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
        Toast.makeText(this@MyOrderDetails,"This item is clicked!",Toast.LENGTH_SHORT).show()
    }

    private fun extractEmailBeforeDot(email: String): String {
        val firstDotIndex = email.indexOf("@")
        return if (firstDotIndex != -1) {
            email.substring(0, firstDotIndex + email.substring(firstDotIndex).indexOf('@'))
        } else {
            email // In case there's no dot, return the original email
        }
    }

}


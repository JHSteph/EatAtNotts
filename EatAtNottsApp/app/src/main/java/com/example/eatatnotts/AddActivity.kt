package com.example.eatatnotts

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.eatatnotts.Home.News
import com.example.eatatnotts.databinding.ActivityAddActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class AddActivity : AppCompatActivity() {//Add new Activity by hawkers
    // TODO: Rename and change types of parameters
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityAddActivityBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {//Create view for AddActivity Activity, handle button logics, and editext inputs to save into Firebase
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        auth = FirebaseAuth.getInstance()
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        binding = ActivityAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.HawkertopAppBar)
        binding.HawkertopAppBar.setNavigationOnClickListener {
            val intent = Intent(this@AddActivity, MainpageHawker::class.java)
            startActivity(intent)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@AddActivity, MainpageHawker::class.java)
                startActivity(intent)
                finish()
                // Code that you need to execute on back press, e.g. finish()
            }
        })
        binding.btnAddActivity.setOnClickListener {
//            if ((!TextUtils.isEmpty(binding.etHawkerName.text.toString())) &&
            if ((!TextUtils.isEmpty(binding.etActivityName.text.toString())) &&
                (!TextUtils.isEmpty(binding.etDescription.text.toString()))
            ) {
                val HE=binding.etActivityName.text.toString()
                val HP=binding.etDescription.text.toString()
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                // Get the user type from the snapshot
                                val userName = userSnapshot.child("username").getValue(String::class.java)
                                database = FirebaseDatabase.getInstance().getReference("Activity")
                                val news= News(userName,HE,HP)
                                database.child("${userName} ${HE}").setValue(news).addOnSuccessListener {
//                                    binding.etHawkerName.text.clear()
                                    binding.etActivityName.text.clear()
                                    binding.etDescription.text.clear()
                                    Toast.makeText(this@AddActivity, "Succesfully Saved", Toast.LENGTH_SHORT).show()

                                }.addOnFailureListener {
                                    Toast.makeText(this@AddActivity, "Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }override fun onCancelled(error: DatabaseError) {
                        // Handle the error, log it, or show a message
                        Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                    }
                })
//            } else if ((!TextUtils.isEmpty(binding.etHawkerName.text.toString())) &&
//                (!TextUtils.isEmpty(binding.etDescription.text.toString()))
//            ) {
//                Toast.makeText(activity, "Please enter an Activity Name!", Toast.LENGTH_LONG).show()
//            } else if ((!TextUtils.isEmpty(binding.etDescription.text.toString())) &&
//                (!TextUtils.isEmpty(binding.etActivityName.text.toString()))
//            ) {
//                Toast.makeText(activity, "Please enter an Hawker Name!", Toast.LENGTH_LONG).show()
//            } else if ((!TextUtils.isEmpty(binding.etHawkerName.text.toString())) &&
//                (!TextUtils.isEmpty(binding.etActivityName.text.toString()))
//            ) {
//                Toast.makeText(activity, "Please enter a Description!", Toast.LENGTH_LONG).show()
//            } else if (!TextUtils.isEmpty(binding.etHawkerName.text.toString())) {
//                Toast.makeText(activity, "Please enter a Description and Activity Name!", Toast.LENGTH_LONG)
//                    .show()
            } else if (!TextUtils.isEmpty(binding.etDescription.text.toString())) {
                Toast.makeText(this@AddActivity, "Please enter an Activity Name!", Toast.LENGTH_LONG)
                    .show()
            } else if (!TextUtils.isEmpty(binding.etActivityName.text.toString())) {
                Toast.makeText(this@AddActivity, "Please enter a Description!", Toast.LENGTH_LONG)
                    .show()
            }
            else{
                Toast.makeText(this@AddActivity, "Please enter a Description and Activity Name!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}
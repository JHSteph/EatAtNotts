package com.example.eatatnotts.Profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eatatnotts.MainActivity
import com.example.eatatnotts.MyHistory.MyHistoryDates
import com.example.eatatnotts.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(//Create view for Profile Fragment,retrieve user data and handle button logics
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        // Get the user type from the snapshot
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val EMail = userSnapshot.child("email").getValue(String::class.java)
                        val WalletAmount = userSnapshot.child("walletAmount").getValue(Double::class.java)
                        val WalletAmount2Dec=String.format("%.2f",WalletAmount)


                        binding.tvProfile.text = userName
                        binding.tvProfileEmail.text=EMail
                        binding.tvWalletAmount.text="RM "+WalletAmount2Dec
                    }
                }
            }override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })

        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnReload.setOnClickListener {
            val Reloadintent = Intent(activity, WalletReload::class.java)
            startActivity(Reloadintent)
        }

        binding.btnMyHistory.setOnClickListener{
            val Historyintent = Intent(activity, MyHistoryDates::class.java)
            startActivity(Historyintent)
        }

        return binding.root
    }
}
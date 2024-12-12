package com.example.eatatnotts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eatatnotts.History.HistoryDates
import com.example.eatatnotts.databinding.FragmentProfileHawkerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileHawker : Fragment() {//Profile page of hawker
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentProfileHawkerBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(//Creates view for ProfileHawker Fragment, and handle button logics
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        binding = FragmentProfileHawkerBinding.inflate(inflater, container, false)

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

                        binding.tvProfileHawker.text = userName
                        binding.tvProfileHawkerEMail.text=EMail
                        binding.tvProfileHawkerWallet.text="RM "+WalletAmount2Dec
                    }
                }
            }override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
            }
        })

        binding.btnSignOutHawker.setOnClickListener {
            auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnHawkerHistory.setOnClickListener {
            val intent = Intent(activity, HistoryDates::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}
package com.example.eatatnotts.Profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.eatatnotts.Cart.Cart
import com.example.eatatnotts.R
import com.example.eatatnotts.ThankPurchase
import com.example.eatatnotts.databinding.ActivityWalletReloadBinding
import com.example.eatatnotts.mainpage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class WalletReload : AppCompatActivity() {//Reload wallet
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityWalletReloadBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {//Create View for WalletReload Activity, calculate reload amount and handle button logics
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wallet_reload)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        binding = ActivityWalletReloadBinding.inflate(layoutInflater)
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        binding.WallettopAppBar.title = "Wallet Reload"
        setSupportActionBar(binding.WallettopAppBar)
        binding.WallettopAppBar.setNavigationOnClickListener {
            val intent = Intent(this, mainpage::class.java)
            startActivity(intent)
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        // Get the user type from the snapshot
                        val WalletAmount =
                            userSnapshot.child("walletAmount").getValue(Double::class.java) ?: 0.0

                        binding.btnWalletReload.setOnClickListener {
                            val reloadAmountText = binding.etReloadAmount.text.toString()
                            if (reloadAmountText.isNotEmpty()) {
                                val alertMessage = AlertDialog.Builder(this@WalletReload)
                                alertMessage.setTitle("Wallet Reload Confirmation")
                                alertMessage.setMessage("Are you sure you want to reload your wallet?")
                                alertMessage.setCancelable(false)
                                alertMessage.setPositiveButton("Yes") { dialog, which ->
                                    val ReloadAmount = reloadAmountText.toDouble()
                                    val TotalAmount = ReloadAmount + WalletAmount
                                    database = FirebaseDatabase.getInstance().getReference("Users")
                                    val uid = auth.currentUser?.uid
                                    database.child("${uid}/walletAmount").setValue(TotalAmount)
                                        .addOnSuccessListener {
                                            binding.etReloadAmount.text.clear()
                                            Toast.makeText(
                                                this@WalletReload,
                                                "Update Wallet Amount Succesfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                this@WalletReload,
                                                "Failed to save user data",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    val intent = Intent(this@WalletReload, mainpage::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                alertMessage.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                                alertMessage.show()
                            }else {
                                Toast.makeText(this@WalletReload,"You did not input an amount to reload!",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }override fun onCancelled(error: DatabaseError) {
                // Handle the error, log it, or show a message
                Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                    }
                })
        setContentView(binding.root)
    }

}

package com.example.eatatnotts

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.example.eatatnotts.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {//Starting of EatAtNotts Application
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {//Create View for MainActivity Activity, check user type and login into their respective home page
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)

        if (auth.currentUser != null) {
            val userId = currentUser?.uid
//
//            // Reference to your Firebase Realtime Database
            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            val query: Query = dbRef.orderByChild("uid").equalTo(userId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            // Get the user type from the snapshot
                            val userType = userSnapshot.child("type").getValue(String::class.java)
//                            Log.i("UserType", "User type for $name is: $userType")
                            // Perform actions based on user type
                            if (userType == "Customer") {
                                val intent = Intent(this@MainActivity, mainpage::class.java)
                                startActivity(intent)
                                finish()
                            } else if (userType == "Hawker") {
                                val intent = Intent(this@MainActivity, MainpageHawker::class.java)
                                startActivity(intent)
                                finish()
                                // Navigate to lecturer-specific screens
                            }
                        }
                    } else {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error, log it, or show a message
                    Log.e("FirebaseError", "Error fetching user data: ${error.message}")
                }
            })
        }
        setContentView(binding.root)
    }
}
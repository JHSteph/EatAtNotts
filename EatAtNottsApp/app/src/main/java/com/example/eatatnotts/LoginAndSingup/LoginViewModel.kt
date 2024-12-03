package com.example.eatatnotts.LoginAndSingup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class LoginViewModel : ViewModel() {//Handles logic for Login Activity
    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("Users")

    private val _loginStatus = MutableLiveData<LoginStatus>()
    val loginStatus: LiveData<LoginStatus> get() = _loginStatus

    fun loginUser(email: String, password: String) {//Chech Login User Type from Firebase Real-Time Database
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    val userId = currentUser?.uid
                    val query: Query = dbRef.orderByChild("uid").equalTo(userId)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (userSnapshot in snapshot.children) {
                                    val userType = userSnapshot.child("type").getValue(String::class.java)
                                    val userName = userSnapshot.child("username").getValue(String::class.java)
                                    if (userType == "Customer") {
                                        _loginStatus.value = LoginStatus.Success(userName = userName, userType = "Customer")
                                    } else if (userType == "Hawker") {
                                        _loginStatus.value = LoginStatus.Success(userName = userName, userType = "Hawker")
                                    }
                                }
                            } else {
                                _loginStatus.value = LoginStatus.Error("User not found")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _loginStatus.value = LoginStatus.Error(error.message)
                        }
                    })
                } else {
                    _loginStatus.value = LoginStatus.Error("Invalid Email or Password!")
                }
            }
    }

    sealed class LoginStatus {//Checks Login Status
        data class Success(val userName: String?, val userType: String?) : LoginStatus()
        data class Error(val message: String) : LoginStatus()
    }
}
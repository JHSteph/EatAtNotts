package com.example.eatatnotts.LoginAndSingup

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.eatatnotts.R
import com.example.eatatnotts.databinding.FragmentSignupStudentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupStudent : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentSignupStudentBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {//Create View
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(//When View is Created, handle button logics and check whether the signup criteria is fulfilled
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupStudentBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        binding.btnStudentSignup.setOnClickListener {
                if ((!TextUtils.isEmpty(binding.etStudentEmail.text.toString())) &&
                    (!TextUtils.isEmpty(binding.etStudentPassword.text.toString())) &&
                    (!TextUtils.isEmpty(binding.etStudentConfirmPassword.text.toString())) &&
                    (binding.etStudentPassword.text.toString()==binding.etStudentConfirmPassword.text.toString()
                            &&(binding.etStudentEmail.text.toString().contains("@nottingham.edu.my")))
                ) {
                    val type = "Customer"
                    val WalletAmount = 0.00
                    val CE = binding.etStudentEmail.text.toString()
                    val CP = binding.etStudentPassword.text.toString()
                    signUpCustomer(CE, CP, type, WalletAmount)
                } else if ((!TextUtils.isEmpty(binding.etStudentEmail.text.toString())) &&
                    (!TextUtils.isEmpty(binding.etStudentPassword.text.toString())) &&
                    (!TextUtils.isEmpty(binding.etStudentConfirmPassword.text.toString())) &&
                    (!binding.etStudentEmail.text.toString().contains("@nottingham.edu.my"))&&
                    (binding.etStudentPassword.text.toString()==binding.etStudentConfirmPassword.text.toString())
                ) {
                    Toast.makeText(
                        activity,
                        "This is not a valid E-Mail!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if ((!TextUtils.isEmpty(binding.etStudentEmail.text.toString())) &&
                    (!TextUtils.isEmpty(binding.etStudentPassword.text.toString())) &&
                    (!TextUtils.isEmpty(binding.etStudentConfirmPassword.text.toString())) &&
                    (!binding.etStudentEmail.text.toString().contains("@nottingham.edu.my"))&&
                    (binding.etStudentPassword.text.toString()!=binding.etStudentConfirmPassword.text.toString())
                ) {
                    Toast.makeText(
                        activity,
                        "Your Password and Confirm Password Is Different!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if ((!TextUtils.isEmpty(binding.etStudentEmail.text.toString())) &&
                    (!TextUtils.isEmpty(binding.etStudentPassword.text.toString()))
                ) {
                    Toast.makeText(activity, "Please Confirm Your Password!", Toast.LENGTH_LONG)
                        .show()
                } else if ((!TextUtils.isEmpty(binding.etStudentPassword.text.toString())) &&
                    (!TextUtils.isEmpty(binding.etStudentConfirmPassword.text.toString()))
                ) {
                    Toast.makeText(activity, "Please Enter An E-Mail!", Toast.LENGTH_LONG).show()
                } else if ((!TextUtils.isEmpty(binding.etStudentEmail.text.toString())) &&
                    (!TextUtils.isEmpty(binding.etStudentConfirmPassword.text.toString()))
                ) {
                    Toast.makeText(activity, "Please Enter A Password!", Toast.LENGTH_LONG).show()
                } else if (!TextUtils.isEmpty(binding.etStudentEmail.text.toString())) {
                    Toast.makeText(
                        activity,
                        "Please Enter A Password And Confirm Your Password!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else if (!TextUtils.isEmpty(binding.etStudentPassword.text.toString())) {
                    Toast.makeText(
                        activity,
                        "Please Enter An E-Mail And Confirm Your Password!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else if (!TextUtils.isEmpty(binding.etStudentConfirmPassword.text.toString())) {
                    Toast.makeText(
                        activity,
                        "Please Enter An E-Mail and Password!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                else {
                    Toast.makeText(
                        activity,
                        "Please Enter An E-Mail, Password And Confirm Your Password!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

            }
        binding.btnStudentLogin.setOnClickListener{
            it.findNavController().navigate(R.id.StudentSignuptoLogin)
        }
        binding.StudentToHawker.setOnClickListener{
            it.findNavController().navigate(R.id.CustomerToHawker)
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    findNavController().navigate(R.id.StudentSignuptoLogin)
                }
                else {

                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        activity,
                        "Please click back again to go back to Login Page",
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        doubleBackToExitPressedOnce = false
                    }, 2000)
                    // Code that you need to execute on back press, e.g. finish()
                }
            }
        })
        return binding.root
    }
    private fun signUpCustomer(CustomerEmail: String, CustomerPassword: String, Type:String,WalletAmount:Double) {//Sign up User into Firebase Authentication
        val CustomerUsername=extractEmailBeforeDot(CustomerEmail)
        auth.createUserWithEmailAndPassword(CustomerEmail, CustomerPassword)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // User successfully created, now retrieve UID
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        // Save user info in the database
                        saveUserToDatabase(uid, CustomerEmail, CustomerPassword,CustomerUsername,Type,WalletAmount)
                    }
                } else {
                    Toast.makeText(context, "Sign-Up Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserToDatabase(uid:String,CustomerEmail: String, CustomerPassword: String,CustomerUsername:String,Type:String,WalletAmount:Double){//Save user data into Firebase Real-Time Database
        database = FirebaseDatabase.getInstance().getReference("Users")
        val customer =
            Customers(uid, CustomerEmail, CustomerPassword, CustomerUsername,Type,WalletAmount)
        database.child(uid).setValue(customer).addOnSuccessListener {
            binding.etStudentEmail.text.clear()
            binding.etStudentPassword.text.clear()
            binding.etStudentConfirmPassword.text.clear()
            Toast.makeText(activity, "Successfully Saved", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.StudentToHome) // Navigate after saving
        }.addOnFailureListener {
            Toast.makeText(activity, "Failed to save user data", Toast.LENGTH_SHORT).show()
        }
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


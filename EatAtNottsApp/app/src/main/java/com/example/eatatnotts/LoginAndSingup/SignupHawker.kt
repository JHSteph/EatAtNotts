package com.example.eatatnotts.LoginAndSingup

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.eatatnotts.Order.Hawkers
import com.example.eatatnotts.Order.HawkersList
import com.example.eatatnotts.R
import com.example.eatatnotts.databinding.FragmentSignupHawkerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupHawker : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentSignupHawkerBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupHawkerBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        binding.btnHawkerSignup.setOnClickListener {
            if ((!TextUtils.isEmpty(binding.etHawkerUsername.text.toString())) &&
                (!TextUtils.isEmpty(binding.etHawkerPassword.text.toString())) &&
                (!TextUtils.isEmpty(binding.etHawkerEMail.text.toString()))&&
                (binding.etHawkerPassword.text.toString().length>=6)
            ) {
                val type="Hawker"
                val HU=binding.etHawkerUsername.text.toString()
                val HE=binding.etHawkerEMail.text.toString()
                val HP=binding.etHawkerPassword.text.toString()
                val HL=binding.etLocation.text.toString()
                val HD=binding.etHawkerDescription.text.toString()
                val bundleHU =
                    bundleOf("HawkerUsername" to binding.etHawkerUsername.text.toString())
                val bundleHP =
                    bundleOf("HawkerPassword" to binding.etHawkerPassword.text.toString())
                val bundleHE = bundleOf("HawkerEMail" to binding.etHawkerEMail.text.toString())
                signUpHawker(HE,HP,HU,type,HL,HD)
            }
            else if ((!TextUtils.isEmpty(binding.etHawkerUsername.text.toString())) &&
                (!TextUtils.isEmpty(binding.etHawkerPassword.text.toString())) &&
                (!TextUtils.isEmpty(binding.etHawkerEMail.text.toString()))&&
                (binding.etHawkerPassword.text.toString().length<=6)
            ){
                Toast.makeText(activity, "Password should be more than 6 Characters!", Toast.LENGTH_SHORT).show()
            }else if ((!TextUtils.isEmpty(binding.etHawkerUsername.text.toString())) &&
                (!TextUtils.isEmpty(binding.etHawkerPassword.text.toString()))
            ) {
                Toast.makeText(activity, "Please enter an E-Mail!", Toast.LENGTH_LONG).show()
            } else if ((!TextUtils.isEmpty(binding.etHawkerPassword.text.toString())) &&
                (!TextUtils.isEmpty(binding.etHawkerEMail.text.toString()))
            ) {
                Toast.makeText(activity, "Please enter an Username!", Toast.LENGTH_LONG).show()
            } else if ((!TextUtils.isEmpty(binding.etHawkerUsername.text.toString())) &&
                (!TextUtils.isEmpty(binding.etHawkerEMail.text.toString()))
            ) {
                Toast.makeText(activity, "Please enter a Password!", Toast.LENGTH_LONG).show()
            } else if (!TextUtils.isEmpty(binding.etHawkerUsername.text.toString())) {
                Toast.makeText(activity, "Please enter a Password and E-Mail!", Toast.LENGTH_LONG)
                    .show()
            } else if (!TextUtils.isEmpty(binding.etHawkerPassword.text.toString())) {
                Toast.makeText(activity, "Please enter a Username and E-Mail!", Toast.LENGTH_LONG)
                    .show()
            } else if (!TextUtils.isEmpty(binding.etHawkerEMail.text.toString())) {
                Toast.makeText(activity, "Please enter a Username and Password!", Toast.LENGTH_LONG)
                    .show()
            }
            else{
                Toast.makeText(activity, "Please enter a Username, Password and E-Mail!", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.btnHawkerLogin.setOnClickListener{
            it.findNavController().navigate(R.id.HawkerSignuptoLogin)
        }
        binding.HawkerToStudent.setOnClickListener{
            it.findNavController().navigate(R.id.HawkerToCustomer)
        }
        return binding.root
    }

    private fun signUpHawker(HawkerEmail: String, HawkerPassword: String, HawkerUsername: String, Type:String,HawkerLocation: String,HawkerDescription: String) {
        auth.createUserWithEmailAndPassword(HawkerEmail, HawkerPassword)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // User successfully created, now retrieve UID
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        // Save user info in the database
                        saveUserToDatabase(uid, HawkerEmail, HawkerPassword,HawkerUsername,Type,HawkerLocation,HawkerDescription)
                    }
                } else {
                    Toast.makeText(context, "Sign-Up Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserToDatabase(uid:String,HawkerEmail: String, HawkerPassword: String,HawkerUsername:String,Type:String, HawkerLocation: String,HawkerDescription: String){
        database = FirebaseDatabase.getInstance().getReference("Hawkers")
        val HawkerList = HawkersList(HawkerUsername, HawkerLocation, HawkerDescription)
        database.child(HawkerUsername).setValue(HawkerList).addOnSuccessListener {
            Toast.makeText(activity, "Successfully Saved Username!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(activity, "Failed to save username", Toast.LENGTH_SHORT).show()
        }
        database = FirebaseDatabase.getInstance().getReference("Users")
        val customer = Hawkers(uid, HawkerEmail, HawkerPassword, HawkerUsername,Type) // Assuming Customers class has a username field
        database.child(uid).setValue(customer).addOnSuccessListener {
            binding.etHawkerUsername.text.clear()
            binding.etHawkerEMail.text.clear()
            binding.etHawkerPassword.text.clear()
            Toast.makeText(activity, "Successfully Saved", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.HawkerToHome) // Navigate after saving
        }.addOnFailureListener {
            Toast.makeText(activity, "Failed to save user data", Toast.LENGTH_SHORT).show()
        }
    }
    }



package com.example.eatatnotts.LoginAndSingup

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.eatatnotts.R
import com.example.eatatnotts.databinding.FragmentLoginBinding

class Login : Fragment() {//Login Fragment for user login into EatAtNotts Application
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private var doubleBackToExitPressedOnce = false

    override fun onCreateView(//Creates View, and request notification permission, and handle button and login logics
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {//Handle toolbar backpress, backpress twice then exits the application
                if (doubleBackToExitPressedOnce) {
                    requireActivity().finish()
                    System.exit(0)
                }

                doubleBackToExitPressedOnce = true
                Toast.makeText(activity, "Please click back again to exit the app", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
                // Code that you need to execute on back press, e.g. finish()
            }
        })

        loginViewModel.loginStatus.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                is LoginViewModel.LoginStatus.Success -> {
                    navigateToHome(status.userName, status.userType)
                }
                is LoginViewModel.LoginStatus.Error -> {
                    Toast.makeText(context, status.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        binding.btnLogin.setOnClickListener {
            if (!isInternetAvailable(requireContext())) {
                Toast.makeText(requireContext(), "There is no internet connection!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val emailText = binding.etLoginEMail.text.toString()
            val passwordText = binding.etLoginPassword.text.toString()

            Log.i("MYTAG", emailText)
            Log.i("MYTAG", passwordText)

            if (!TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(passwordText)) {
                binding.tvEMailError?.text = ""
                binding.tvErrorPassword?.text = ""
                loginViewModel.loginUser(emailText, passwordText)
            } else if (!TextUtils.isEmpty(emailText)) {
                binding.tvEMailError?.text = ""
                binding.tvErrorPassword?.text = "Please enter a Password!"
            } else if (!TextUtils.isEmpty(passwordText)) {
                binding.tvErrorPassword?.text = ""
                binding.tvEMailError?.text = "Please enter an E-mail!"
            } else {
                binding.tvEMailError?.text = "Please enter an E-mail!"
                binding.tvErrorPassword?.text = "Please enter a Password!"
            }
        }

        binding.btnLoginSignup.setOnClickListener {
            if (!isInternetAvailable(requireContext())) {
                Toast.makeText(requireContext(), "There is no internet connection!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }else {
                it.findNavController().navigate(R.id.LoginToStudentSignup)
            }
        }

        return binding.root
    }

    private fun navigateToHome(userName: String?, userType: String?) {//Check user type and brings user to their respective home page
        val bundle = when (userType) {
            "Customer" -> bundleOf("StudentUsername" to userName)
            "Hawker" -> bundleOf("HawkerUsername" to userName)
            else -> null
        }

        if (userType == "Customer") {
            findNavController().navigate(R.id.LoginToHome, bundle)
        } else if (userType == "Hawker") {
            findNavController().navigate(R.id.LoginToHawker, bundle)
        }
    }
    private fun requestNotificationPermission() {//Request Notification Permission
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                POST_NOTIFICATIONS
            ) -> {
            }

            else -> {
                // Directly request the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted
        } else {
            Toast.makeText(activity,"You Must Allow Notification Permission To Use The Application!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {//Check whether Internet is on or not
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
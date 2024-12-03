package com.example.eatatnotts.Payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatatnotts.Cart.Cart
import com.example.eatatnotts.R
import com.example.eatatnotts.ThankPurchase
import com.example.eatatnotts.databinding.FragmentPaymentPickUpBinding

class PaymentPickUp : Fragment() {//Shows Payment item in a list
    private lateinit var binding: FragmentPaymentPickUpBinding
    private lateinit var paymentAdapter: PaymentAdapter

    private val viewModel: PaymentPickUpViewModel by viewModels()

    override fun onCreateView(//Creates View for PaymentPickUp Fragment, and handle button logics
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentPickUpBinding.inflate(inflater, container, false)
        paymentAdapter = PaymentAdapter(arrayListOf())

        binding.Paymentlist.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = paymentAdapter
        }

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.PaymentPickUptopAppBar)
        binding.PaymentPickUptopAppBar.setNavigationOnClickListener {
            val intent = Intent(activity, Cart::class.java)
            startActivity(intent)
            (activity as? AppCompatActivity)?.finish()
        }

        viewModel.fetchUserData()
        observeViewModel()

        binding.btnPickUpDelivery.setOnClickListener {
            it.findNavController().navigate(R.id.PickUpToDelivery)
        }

        binding.btnPickUpPay.setOnClickListener {
            val walletAmount = viewModel.walletAmount.value ?: 0.0f
            val totalCost = viewModel.totalCost.value ?: 0.0f
            if (totalCost > walletAmount) {
                Toast.makeText(activity, "You do not have enough amount of money!", Toast.LENGTH_SHORT).show()
            } else {
                showConfirmationDialog(walletAmount, totalCost)
            }
        }

        return binding.root
    }

    private fun showConfirmationDialog(walletAmount: Float, totalCost: Float) {//Show Order Confirmation Dialog to users
        val alertMessage = AlertDialog.Builder(requireContext())
        alertMessage.setTitle("Order Confirmation")
        alertMessage.setMessage("Are you sure you want to confirm your order?")
        alertMessage.setCancelable(false)
        alertMessage.setPositiveButton("Yes") { _, _ ->
            val lastAmount = walletAmount - totalCost
            val intent = Intent(activity, ThankPurchase::class.java)
            intent.putExtra("remarks", viewModel.remarks.value)
            intent.putExtra("method", "Pick Up")
            intent.putExtra("lastAmount", lastAmount)
            startActivity(intent)
        }
        alertMessage.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        alertMessage.create().show()
    }

    private fun observeViewModel() {//Observe View Model
        viewModel.paymentItems.observe(viewLifecycleOwner, { paymentItems ->
            paymentAdapter.updatePaymentList(paymentItems)
        })
        viewModel.totalCost.observe(viewLifecycleOwner, { totalCost ->
            binding.tvTotalPickUpPrice.text = "RM %.2f".format(totalCost)
        })
    }
}

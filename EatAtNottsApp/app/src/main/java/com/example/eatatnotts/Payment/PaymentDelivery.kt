package com.example.eatatnotts.Payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.eatatnotts.R
import com.example.eatatnotts.ThankPurchase
import com.example.eatatnotts.databinding.FragmentPaymentDeliveryBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.Cart.Cart

class PaymentDelivery : Fragment() {//Shows Payment item in a list with a searchable spinner for location input

    private lateinit var binding: FragmentPaymentDeliveryBinding
    private lateinit var viewModel: PaymentDeliveryViewModel
    private lateinit var paymentAdapter: PaymentAdapter
    private lateinit var locationSpinner: AutoCompleteTextView

    override fun onCreateView(//Creates View for PaymentDelivery Fragment, handle button logics and searchable spinner inputs
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentDeliveryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(PaymentDeliveryViewModel::class.java)

        setupRecyclerView()
        observeViewModel()

        binding.btnDeliveryPickUp.setOnClickListener {
            it.findNavController().navigate(R.id.DeliveryToPickup)
        }

        binding.btnDeliveryPay.setOnClickListener {
            val selectedLocation = viewModel.selectedLocation.value
            val totalCost = viewModel.totalCost.value ?: 0f
            val walletAmount = viewModel.walletAmount.value ?: 0f

            Log.i("MYTAG","${selectedLocation}")

            if (selectedLocation.isNullOrEmpty()) {
                Toast.makeText(activity, "You didn't select a location!", Toast.LENGTH_SHORT).show()
            } else if (totalCost > walletAmount) {
                Toast.makeText(activity, "You do not have enough amount of money!", Toast.LENGTH_SHORT).show()
            } else {
                showOrderConfirmationDialog(selectedLocation, totalCost, walletAmount)
            }
        }

        locationSpinner = binding.locationSpinner
        locationSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, emptyList<String>()))

        locationSpinner.setOnItemClickListener { _, _, position, _ ->
            val selectedLocation = locationSpinner.adapter.getItem(position).toString()
            viewModel.selectLocation(selectedLocation) // Update the ViewModel with the selected location
        }


        binding.PaymentDelivertytopAppBar.setNavigationOnClickListener {
            val intent = Intent(activity, Cart::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return binding.root
    }

    private fun setupRecyclerView() {//Setup RecyclerView
        binding.Paymentlist.layoutManager = LinearLayoutManager(activity)
        binding.Paymentlist.setHasFixedSize(true)
        paymentAdapter = PaymentAdapter(emptyList())
        binding.Paymentlist.adapter = paymentAdapter
    }

    private fun observeViewModel() {//Observe ViewModel
        viewModel.paymentList.observe(viewLifecycleOwner) { paymentItems ->
            paymentAdapter.updatePaymentList(paymentItems)
        }

        viewModel.totalCost.observe(viewLifecycleOwner) { totalCost ->
            // Update the total cost text
            binding.tvTotalDeliveryCost.text = "RM ${"%.2f".format(totalCost)}"
        }

        viewModel.deliveryCost.observe(viewLifecycleOwner) { deliveryCost ->
            // Update the delivery cost text
            binding.tvDeliveryCost.text = deliveryCost
        }

        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            Log.d("MYTAG", "Locations updated: $locations") // Log locations to ensure they are correctly fetched
            locationSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, locations))
        }

        // Fetch user data and locations
        viewModel.fetchUserData()
        viewModel.fetchLocations()

        // Observe locations to update the spinner
        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            locationSpinner.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, locations))
        }
    }

    private fun showOrderConfirmationDialog(location: String, totalCost: Float, walletAmount: Float) {//Show Order Confirmation Dialog to users
        val alertMessage = AlertDialog.Builder(requireContext())
        alertMessage.setTitle("Order Confirmation")
        alertMessage.setMessage("Are you sure you want to confirm your order?")
        alertMessage.setCancelable(false)
        alertMessage.setPositiveButton("Yes") { dialog, which ->
            val lastAmount = walletAmount - totalCost
            val intent = Intent(activity, ThankPurchase::class.java).apply {
                putExtra("location", location)
                putExtra("method", "Delivery")
                putExtra("lastAmount", lastAmount)
            }
            startActivity(intent)
        }
        alertMessage.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
        alertMessage.show()
    }
}

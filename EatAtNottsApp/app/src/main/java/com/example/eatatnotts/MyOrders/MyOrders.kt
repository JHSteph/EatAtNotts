package com.example.eatatnotts.MyOrders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.eatatnotts.CustomerOrder.CustomerOrderDeliveryFragment
import com.example.eatatnotts.CustomerOrder.CustomerOrderPickUpFragment
import com.example.eatatnotts.R
import com.example.eatatnotts.databinding.FragmentCustomerOrderBinding
import com.example.eatatnotts.databinding.FragmentMyOrdersBinding

class MyOrders : Fragment() {
    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(//Creates View for My Orders Fragment
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When View Created, handle button logic to change between fragments of MyOrdersPickUpFragment and MyOrdersDeliveryFragment
        super.onViewCreated(view, savedInstanceState)

        // Initially load the PickUp fragment and set button colors
        replaceFragment(MyOrdersPickUpFragment())
        setButtonColors(binding.btnMyOrderPickUp, binding.btnMyOrderDelivery)

        binding.btnMyOrderPickUp.setOnClickListener {
            replaceFragment(MyOrdersPickUpFragment())
            setButtonColors(binding.btnMyOrderPickUp, binding.btnMyOrderDelivery)
        }

        binding.btnMyOrderDelivery.setOnClickListener {
            replaceFragment(MyOrdersDeliveryFragment())
            setButtonColors(binding.btnMyOrderDelivery, binding.btnMyOrderPickUp)
        }
    }

    private fun replaceFragment(fragment: Fragment) {//Replaces Fragment
        childFragmentManager.beginTransaction()
            .replace(R.id.MyOrderFrameLayout, fragment)
            .commit()
    }

    private fun setButtonColors(selectedButton: View, unselectedButton: View) {//Change button color when button pressed
        selectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorSelected))
        (selectedButton as Button).setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        unselectedButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorUnselected))
        (unselectedButton as Button).setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
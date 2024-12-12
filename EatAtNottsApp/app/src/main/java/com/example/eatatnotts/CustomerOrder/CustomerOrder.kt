package com.example.eatatnotts.CustomerOrder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.eatatnotts.R
import com.example.eatatnotts.databinding.FragmentCustomerOrderBinding

class CustomerOrder : Fragment() {//CustomerOrder handles the changes of view between two fragments, CustomerOrderDeliveryFragment and CustomerOrderPickUPFragment
    private var _binding: FragmentCustomerOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//After View is created, handles button to change between two fragments
        super.onViewCreated(view, savedInstanceState)

        // Initially load the PickUp fragment and set button colors
        replaceFragment(CustomerOrderPickUpFragment())
        setButtonColors(binding.btnCustomerOrderPickUp, binding.btnCustomerOrderDelivery)

        binding.btnCustomerOrderPickUp.setOnClickListener {
            replaceFragment(CustomerOrderPickUpFragment())
            setButtonColors(binding.btnCustomerOrderPickUp, binding.btnCustomerOrderDelivery)
        }

        binding.btnCustomerOrderDelivery.setOnClickListener {
            replaceFragment(CustomerOrderDeliveryFragment())
            setButtonColors(binding.btnCustomerOrderDelivery, binding.btnCustomerOrderPickUp)
        }
    }

    private fun replaceFragment(fragment: Fragment) {//Replace fragment
        childFragmentManager.beginTransaction()
            .replace(R.id.customerOrderFrameLayout, fragment)
            .commit()
    }

    private fun setButtonColors(selectedButton: View, unselectedButton: View) {//Change button color when buttons are pressed
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
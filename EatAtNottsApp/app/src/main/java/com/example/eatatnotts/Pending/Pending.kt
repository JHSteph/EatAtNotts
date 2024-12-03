package com.example.eatatnotts.Pending

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
import com.example.eatatnotts.databinding.FragmentPendingBinding

class Pending : Fragment() {//Pending handles the changes of view between two fragments, PendingDelivery and PendingPickUp
    private var _binding: FragmentPendingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//After View is created, handles button to change between two fragments
        super.onViewCreated(view, savedInstanceState)
        replaceFragment(PendingPickUp())
        setButtonColors(binding.btnPendingPickUp, binding.btnPendingDelivery)

        binding.btnPendingPickUp.setOnClickListener {
            replaceFragment(PendingPickUp())
            setButtonColors(binding.btnPendingPickUp, binding.btnPendingDelivery)
        }

        binding.btnPendingDelivery.setOnClickListener {
            replaceFragment(PendingDelivery())
            setButtonColors(binding.btnPendingDelivery, binding.btnPendingPickUp)
        }

        // Initially load the PickUp fragment and set button colors
    }

    private fun replaceFragment(fragment: Fragment) {//Replace fragment
        childFragmentManager.beginTransaction()
            .replace(R.id.PendingFrameLayout, fragment)
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
// HistoryDeliveryDates.kt
package com.example.eatatnotts.History

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatatnotts.MainpageHawker
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.R
import com.example.eatatnotts.databinding.FragmentHistoryDeliveryDatesBinding

class HistoryDeliveryDates : Fragment(), HistoryDeliveryDatesAdapter.OnItemClickListener {//Shows Hawker Delivery History Dates in a list of RecyclerView

    private lateinit var binding: FragmentHistoryDeliveryDatesBinding
    private val viewModel: HistoryDeliveryDatesViewModel by viewModels()

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHistoryDeliveryDatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When View Created, handle button logics and check whether the list is empty or not
        super.onViewCreated(view, savedInstanceState)

        // Start the notification service
        val intent = Intent(requireContext(), NotificationService::class.java)
        requireActivity().startService(intent)

        val userRecyclerview = binding.Datelist
        userRecyclerview.layoutManager = LinearLayoutManager(activity)
        userRecyclerview.setHasFixedSize(true)

        val adapter = HistoryDeliveryDatesAdapter(ArrayList(), this)
        userRecyclerview.adapter = adapter

        viewModel.deliveryDateList.observe(viewLifecycleOwner, Observer { deliveryDates ->
            adapter.updateData(ArrayList(deliveryDates))
        })

        binding.HistoryDatetopAppBar.setNavigationOnClickListener {
            val intent = Intent(activity, MainpageHawker::class.java)
            startActivity(intent)
            (activity as? AppCompatActivity)?.finish()
        }
        viewModel.emptyDeliveryHistory.observe(viewLifecycleOwner,{ emptyOrder ->
            if (emptyOrder) {
                binding.tvHistoryDeliveryEmpty.text = "There is no completed Delivery order for now. \n" +
                        "Please check any completed customer Pick Up order by clicking the Pick Up button."
            } else {
                binding.tvHistoryDeliveryEmpty.text = ""
            }
        })

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.btnHistoryPickUp.setOnClickListener {
            it.findNavController().navigate(R.id.HistoryDeliveryDatesToHistoryPickUpDates)
        }
    }

    override fun onItemClick(myhistorydate: HistoryDateItem) {//Check Clicked Item And Start Another Activity
        val intent = Intent(activity, HistoryDeliveryNumber::class.java)
        intent.putExtra("Date", myhistorydate.date)
        startActivity(intent)
    }
}

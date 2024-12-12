package com.example.eatatnotts.History

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.MainpageHawker
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.R
import com.example.eatatnotts.databinding.FragmentHistoryPickUpDatesBinding

class HistoryPickUpDates : Fragment(), HistoryPickUpDatesAdapter.OnItemClickListener {//Shows Hawker Pick Up History Dates in a list of RecyclerView
    private lateinit var binding: FragmentHistoryPickUpDatesBinding
    private lateinit var viewModel: HistoryPickUpDatesViewModel

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryPickUpDatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When View Created, handle button logics and check whether the list is empty or not
        super.onViewCreated(view, savedInstanceState)

        // Start the notification service
        val intent = Intent(requireContext(), NotificationService::class.java)
        requireActivity().startService(intent)

        viewModel = ViewModelProvider(this).get(HistoryPickUpDatesViewModel::class.java)

        binding.Datelist.layoutManager = LinearLayoutManager(activity)
        binding.Datelist.setHasFixedSize(true)

        val adapter = HistoryPickUpDatesAdapter(arrayListOf(), this)
        binding.Datelist.adapter = adapter

        viewModel.pickUpDates.observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })
        viewModel.emptyPickUpHistory.observe(viewLifecycleOwner,{ emptyOrder ->
            if (emptyOrder) {
                binding.tvHistoryPickUpEmpty.text = "There is no completed Pick Up order for now. \n" +
                        "Please check any completed customer Delivery order by clicking the Delivery button."
            } else {
                binding.tvHistoryPickUpEmpty.text = ""
            }
        })

        binding.HistoryDatetopAppBar.setNavigationOnClickListener {
            val intent = Intent(activity, MainpageHawker::class.java)
            startActivity(intent)
            (activity as? AppCompatActivity)?.finish()
        }


        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnHistoryDelivery.setOnClickListener {
            it.findNavController().navigate(R.id.HistoryPickUpDatesToHistoryDeliveryDates)
        }
    }

    override fun onItemClick(myhistorydate: HistoryDateItem) {//Check Clicked Item And Start Another Activity
        val intent = Intent(activity, HistoryPickUpNumber::class.java)
        intent.putExtra("Date", myhistorydate.date)
        startActivity(intent)
    }
}

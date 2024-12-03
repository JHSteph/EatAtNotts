package com.example.eatatnotts.MyHistory

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.R
import com.example.eatatnotts.databinding.FragmentMyHistoryPickUpDatesBinding
import com.example.eatatnotts.mainpage
import com.google.firebase.auth.FirebaseAuth

class MyHistoryPickUpDates : Fragment(), MyHistoryPickUpDatesAdapter.OnItemClickListener {//Shows My Pick Up Order History Dates in a list of RecyclerView
    private lateinit var binding: FragmentMyHistoryPickUpDatesBinding
    private lateinit var userRecyclerview: RecyclerView
    private val viewModel: MyHistoryPickUpDatesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyHistoryPickUpDatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start the notification service

        userRecyclerview = binding.Datelist
        userRecyclerview.layoutManager = LinearLayoutManager(activity)
        userRecyclerview.setHasFixedSize(true)

        val adapter = MyHistoryPickUpDatesAdapter(arrayListOf(), this)
        userRecyclerview.adapter = adapter

        viewModel.pickUpDates.observe(viewLifecycleOwner, Observer {
            adapter.updateList(it)
        })

        viewModel.emptyPickUpHistory.observe(viewLifecycleOwner,{ emptyDeliveryHistory ->
            if (emptyDeliveryHistory) {
                binding.tvMyHistoryPickUpEmpty.text = "There is nothing in your Pick Up order history now. \n" +
                        "Let's go and place some orders in the Order page!"
            } else {
                binding.tvMyHistoryPickUpEmpty.text = ""
            }
        })

        viewModel.getUserData()

        binding.MyHistoryDatetopAppBar.setNavigationOnClickListener {
            val intent = Intent(activity, mainpage::class.java)
            startActivity(intent)
            (activity as? AppCompatActivity)?.finish()
        }
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnMyHistoryDelivery.setOnClickListener {
            it.findNavController().navigate(R.id.MyHistoryPickUpDateToMyHistoryDeliveryDate)
        }
    }

    override fun onItemClick(myhistorydate: MyHistoryDateItem) {
        val intent = Intent(activity, MyHistoryPickUpNumber::class.java)
        intent.putExtra("Date", myhistorydate.date)
        startActivity(intent)
    }
}

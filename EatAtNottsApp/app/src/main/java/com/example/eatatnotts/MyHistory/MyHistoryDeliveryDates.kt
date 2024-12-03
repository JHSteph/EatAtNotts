package com.example.eatatnotts.MyHistory

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
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.R
import com.example.eatatnotts.databinding.FragmentMyHistoryDeliveryDatesBinding
import com.example.eatatnotts.mainpage

class MyHistoryDeliveryDates : Fragment(), MyHistoryDeliveryDatesAdapter.OnItemClickListener {//Shows My Delivery Order History Dates in a list of RecyclerView
    private lateinit var binding: FragmentMyHistoryDeliveryDatesBinding
    private val viewModel: MyHistoryDeliveryDatesViewModel by viewModels()

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyHistoryDeliveryDatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When View Created, handle button logics and check whether the list is empty or not
        super.onViewCreated(view, savedInstanceState)

        // Start the notification service

        binding.Datelist.layoutManager = LinearLayoutManager(activity)
        binding.Datelist.setHasFixedSize(true)

        binding.MyHistoryDatetopAppBar.setNavigationOnClickListener {
            val intent = Intent(activity, mainpage::class.java)
            startActivity(intent)
            (activity as? AppCompatActivity)?.finish()
        }
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnMyHistoryPickUp.setOnClickListener {
            it.findNavController().navigate(R.id.MyHistoryDeliveryDateToMyHistoryPickUpDate)
        }

        val adapter = MyHistoryDeliveryDatesAdapter(arrayListOf(), this)
        binding.Datelist.adapter = adapter

        viewModel.deliveryDateList.observe(viewLifecycleOwner, Observer { deliveryDates ->
            adapter.updateData(deliveryDates)
        })
        viewModel.emptyDeliveryHistory.observe(viewLifecycleOwner,{ emptyDeliveryHistory ->
            if (emptyDeliveryHistory) {
                binding.tvMyHistoryDeliveryEmpty.text = "There is nothing in your Delivery order history now. \n" +
                        "Let's go and place some orders in the Order page!"
            } else {
                binding.tvMyHistoryDeliveryEmpty.text = ""
            }
        })

        viewModel.getUserData()
    }

    override fun onItemClick(myhistorydate: MyHistoryDateItem) {//Check Clicked Item And Start Another Activity
        val intent = Intent(activity, MyHistoryDeliveryNumber::class.java)
        intent.putExtra("Date", myhistorydate.date)
        startActivity(intent)
    }
}

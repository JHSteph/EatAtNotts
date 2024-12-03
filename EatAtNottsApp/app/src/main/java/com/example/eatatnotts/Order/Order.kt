package com.example.eatatnotts.Order

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.OrderToFood.HawkerFoodList
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.FragmentOrderBinding

class Order : Fragment(), HawkerAdapter.OnItemClickListener {//Shows Hawkers available in campus in a list of RecyclerView
    private lateinit var binding: FragmentOrderBinding
    private val viewModel: OrderViewModel by viewModels()
    private lateinit var userRecyclerview: RecyclerView

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When View Created, Add for items in RecyclerView
        super.onViewCreated(view, savedInstanceState)

        // Start the notification service

        userRecyclerview = binding.Hawkerlist
        userRecyclerview.layoutManager = LinearLayoutManager(activity)
        userRecyclerview.setHasFixedSize(true)

        viewModel.hawkersList.observe(viewLifecycleOwner, Observer { hawkers ->
            // Convert List<HawkersList> to ArrayList<HawkersList>
            userRecyclerview.adapter = HawkerAdapter(ArrayList(hawkers), this@Order)
        })
    }

    override fun onItemClick(hawker: HawkersList) {//Check Clicked Item And Start Another Activity
        val intent = Intent(activity, HawkerFoodList::class.java)
        intent.putExtra("hawkerName", hawker.username)
        startActivity(intent)
    }
}

package com.example.eatatnotts.CustomerOrder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.FragmentCustomerOrderDeliveryBinding
import com.google.firebase.auth.FirebaseAuth

class CustomerOrderDeliveryFragment : Fragment(), CustomerOrderDeliveryAdapter.OnItemClickListener {//Shows Customer Delivery Order in a list
    private lateinit var binding: FragmentCustomerOrderDeliveryBinding
    private lateinit var userRecyclerview: RecyclerView
    private val viewModel: CustomerOrderDeliveryViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCustomerOrderDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When view created, handle button logics, and check whether the list is empty
        super.onViewCreated(view, savedInstanceState)

        // Start the notification service

        auth = FirebaseAuth.getInstance()
        userRecyclerview = binding.DeliveryRecyclerView
        userRecyclerview.layoutManager = LinearLayoutManager(activity)
        userRecyclerview.setHasFixedSize(true)

        viewModel.customerOrders.observe(viewLifecycleOwner, Observer { orders ->
            userRecyclerview.adapter = CustomerOrderDeliveryAdapter(ArrayList(orders), this@CustomerOrderDeliveryFragment)
        })

        viewModel.emptyOrder.observe(viewLifecycleOwner,{ emptyOrder ->
            if (emptyOrder) {
                binding.tvEmptyCustomerDeliveryOrders.text = "There is no customer Delivery order for now. \n" +
                        "Please check any available customer Pick Up order by clicking the Pick Up button.\n"+
                        "If you want to check customer order history, please go to Profile->History"
            } else {
                binding.tvEmptyCustomerDeliveryOrders.text = ""
            }
        })

        viewModel.getUserAndOrderData(auth.currentUser?.uid)
    }

    override fun onItemClick(myordersnumber: CustomerOrderNumberDelivery) {//Check clicked Item and start another activity
        val intent = Intent(activity, DeliveryDetails::class.java)
        intent.putExtra("receipt", myordersnumber.receipt)
        startActivity(intent)
    }
}

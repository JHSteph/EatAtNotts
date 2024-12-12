package com.example.eatatnotts.CustomerOrder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.Cart.CartDetails
import com.example.eatatnotts.Cart.CartItem
import com.example.eatatnotts.CustomerOrder.CustomerOrderItem
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.FragmentCustomerOrderDeliveryBinding
import com.example.eatatnotts.databinding.FragmentCustomerOrderPickUpBinding
import com.example.eatatnotts.databinding.FragmentMyOrdersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CustomerOrderPickUpFragment : Fragment(), CustomerOrderPickUpAdapter.OnItemClickListener {//Shows Customer Pick Up Order in a list of recyclerView
    private lateinit var binding: FragmentCustomerOrderPickUpBinding
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var adapter: CustomerOrderPickUpAdapter
    private val viewModel: CustomerOrderPickUpViewModel by viewModels()

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerOrderPickUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When view created, handle button logics, and check whether the list is empty
        super.onViewCreated(view, savedInstanceState)


        userRecyclerview = binding.PickUpRecyclerView
        userRecyclerview.layoutManager = LinearLayoutManager(activity)
        userRecyclerview.setHasFixedSize(true)

        adapter = CustomerOrderPickUpAdapter(arrayListOf(), this)
        userRecyclerview.adapter = adapter

        viewModel.orders.observe(viewLifecycleOwner, { orders ->
            adapter.updateOrders(ArrayList(orders))
        })

        viewModel.emptyOrder.observe(viewLifecycleOwner,{ emptyOrder ->
            if (emptyOrder) {
                binding.tvEmptyCustomerPickUpOrders.text = "There is no customer Pick Up order for now. \n" +
                        "Please check any available customer Delivery order by clicking the Delivery button. \n"+
                        "If you want to check customer order history, please go to Profile->History"
            } else {
                binding.tvEmptyCustomerPickUpOrders.text = ""
            }
        })
    }

    override fun onItemClick(myordersnumber: CustomerOrderNumber) {//Check clicked Item and start another activity
        val intent = Intent(activity, PickUpDetails::class.java)
        intent.putExtra("customerEmail", myordersnumber.customerEmail)
        intent.putExtra("receipt", myordersnumber.receipt)
        startActivity(intent)
    }
}



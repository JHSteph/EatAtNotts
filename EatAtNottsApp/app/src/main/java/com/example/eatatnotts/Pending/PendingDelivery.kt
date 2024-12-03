package com.example.eatatnotts.Pending

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.databinding.FragmentCustomerOrderDeliveryBinding

class PendingDelivery : Fragment(), PendingDeliveryAdapter.OnItemClickListener {//Shows Pending Delivery Order Number in a list
    private lateinit var binding: FragmentCustomerOrderDeliveryBinding
    private lateinit var userRecyclerview: RecyclerView
    private val viewModel: PendingDeliveryViewModel by viewModels()

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerOrderDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When view created, and check whether the list is empty
        super.onViewCreated(view, savedInstanceState)

        userRecyclerview = binding.DeliveryRecyclerView
        userRecyclerview.layoutManager = LinearLayoutManager(activity)
        userRecyclerview.setHasFixedSize(true)

        viewModel.fetchUserData()
        viewModel.customerOrders.observe(viewLifecycleOwner, Observer { orders ->
            userRecyclerview.adapter = PendingDeliveryAdapter(ArrayList(orders), this)
        })
        viewModel.emptyOrder.observe(viewLifecycleOwner,{ emptyOrder ->
            if (emptyOrder) {
                binding.tvEmptyCustomerDeliveryOrders.text = "There is no pending customer Delivery order for now. \n" +
                        "Please check any available pending customer Pick Up order by clicking the Pick Up button. \n"+
                        "If you want to check customer order history, please go to Profile->History"
            } else {
                binding.tvEmptyCustomerDeliveryOrders.text = ""
            }
        })
    }

    override fun onItemClick(pendingDelivery: PendingDeliveryItem) {//Check clicked Item and start another activity
        val intent = Intent(activity, PendingDeliveryDetails::class.java)
        intent.putExtra("receipt", pendingDelivery.receipt)
        Log.i("MYTAG", "${pendingDelivery.receipt}")
        startActivity(intent)
    }
}

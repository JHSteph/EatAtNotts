package com.example.eatatnotts.MyOrders

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
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.FragmentMyOrdersDeliveryBinding

class MyOrdersDeliveryFragment : Fragment(), MyOrdersNumberDeliveryAdapter.OnItemClickListener {//Shows My Delivery Order in a list

    private lateinit var binding: FragmentMyOrdersDeliveryBinding
    private val viewModel: MyOrdersDeliveryViewModel by viewModels()
    private lateinit var myOrdersAdapter: MyOrdersNumberDeliveryAdapter

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyOrdersDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When view created, handle button logics, and check whether the list is empty
        super.onViewCreated(view, savedInstanceState)
        Log.i("MYTAG", "I'm here Delivery 1")

        // Start the notification service
        val intent = Intent(requireContext(), NotificationService::class.java)
        requireActivity().startService(intent)

        // Initialize RecyclerView
        binding.MyOrderslist.layoutManager = LinearLayoutManager(activity)
        myOrdersAdapter = MyOrdersNumberDeliveryAdapter(arrayListOf(), this)
        binding.MyOrderslist.adapter = myOrdersAdapter

        // Observe ViewModel data
        viewModel.myOrdersList.observe(viewLifecycleOwner, Observer { orders ->
            orders?.let {
                myOrdersAdapter.updateOrders(it)
            }
        })

        viewModel.emptyOrder.observe(viewLifecycleOwner) { emptyOrder ->
            if (emptyOrder) {
                binding.tvEmptyDeliveryOrders.text = "There are no Delivery orders now. \n" +
                        "Please check available pick up orders by clicking on the Pick Up button. \n" +
                        "Please go to profile->History to check order history.\n"
            } else {
                binding.tvEmptyDeliveryOrders.text = ""
            }
        }

        // Fetch user data
        viewModel.fetchUserData()
    }

    override fun onItemClick(myordersnumber: MyOrdersNumberDelivery) {//Check clicked Item and start another activity
        val intent = Intent(activity, MyOrdersDeliveryDetails::class.java)
        intent.putExtra("receipt", myordersnumber.receipt)
        Log.i("MYTAG", "${myordersnumber.receipt}")
        startActivity(intent)
    }
}

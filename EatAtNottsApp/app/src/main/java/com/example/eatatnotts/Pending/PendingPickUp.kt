// PendingPickUpFragment.kt
package com.example.eatatnotts.Pending

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
import com.example.eatatnotts.databinding.FragmentPendingPickUpBinding
import com.example.eatatnotts.viewmodel.PendingPickUpViewModel

class PendingPickUp : Fragment(), PendingPickUpAdapter.OnItemClickListener {//Shows Pending Pick Up Order Number in a list

    private lateinit var binding: FragmentPendingPickUpBinding
    private val viewModel: PendingPickUpViewModel by viewModels()
    private lateinit var adapter: PendingPickUpAdapter

    override fun onCreateView(//Creates View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPendingPickUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When view created, and check whether the list is empty
        super.onViewCreated(view, savedInstanceState)

        binding.MyOrderslist.layoutManager = LinearLayoutManager(activity)
        binding.MyOrderslist.setHasFixedSize(true)
        adapter = PendingPickUpAdapter(arrayListOf(), this)
        binding.MyOrderslist.adapter = adapter

        viewModel.pendingPickUpItems.observe(viewLifecycleOwner, Observer { items ->
            adapter.updateItems(ArrayList(items))
        })

        viewModel.emptyOrder.observe(viewLifecycleOwner,{ emptyOrder ->
            if (emptyOrder) {
                binding.tvEmptyPendingPickUpOrders.text = "There is no pending customer Pick Up order for now. \n" +
                        "Please check any available pending customer Delivery order by clicking the Delivery button. \n"+
                        "If you want to check customer order history, please go to Profile->History"
            } else {
                binding.tvEmptyPendingPickUpOrders.text = ""
            }
        })
    }

    override fun onItemClick(pendingpickup: PendingPickUpItem) {//Check clicked Item and start another activity
        val intent = Intent(activity, PendingPickUpDetails::class.java)
        intent.putExtra("receipt", pendingpickup.receipt)
        Log.i("MYTAG", "${pendingpickup.receipt}")
        startActivity(intent)
    }
}

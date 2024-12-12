package com.example.eatatnotts.MyOrders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.FragmentMyOrdersPickUpBinding
import com.google.firebase.auth.FirebaseAuth

class MyOrdersPickUpFragment : Fragment(), MyOrdersNumberAdapter.OnItemClickListener {//Shows My Pick Up Order in a list
    private lateinit var binding: FragmentMyOrdersPickUpBinding
    private lateinit var userRecyclerview: RecyclerView
    private lateinit var viewModel: MyOrdersPickUpViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyOrdersPickUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When view created, handle button logics, and check whether the list is empty
        super.onViewCreated(view, savedInstanceState)

        // Start the notification service
        val intent = Intent(requireContext(), NotificationService::class.java)
        requireActivity().startService(intent)

        viewModel = ViewModelProvider(this).get(MyOrdersPickUpViewModel::class.java)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            viewModel.getUserData(userId)
        }

        userRecyclerview = binding.MyOrderslist
        userRecyclerview.layoutManager = LinearLayoutManager(activity)
        userRecyclerview.setHasFixedSize(true)

        viewModel.myOrdersList.observe(viewLifecycleOwner) { myOrders ->
            userRecyclerview.adapter = MyOrdersNumberAdapter(ArrayList(myOrders), this)
        }

        viewModel.userName.observe(viewLifecycleOwner) { userName ->
            viewModel.userEmail.observe(viewLifecycleOwner) { email ->
                viewModel.fetchOrderData(userName, email)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("FirebaseError", errorMessage)
        }

        viewModel.emptyOrder.observe(viewLifecycleOwner) { emptyOrder ->
            if (emptyOrder) {
                binding.tvEmptyPickUpOrders.text = "There are no Pick Up orders now. \n" +
                        "Please check available delivery orders by clicking on the Delivery button. \n" +
                        "Please go to profile->History to check order history.\n"
            } else {
                binding.tvEmptyPickUpOrders.text = ""
            }
        }
    }

    override fun onItemClick(myordersnumber: MyOrdersNumber) {//Check clicked Item and start another activity
        val intent = Intent(activity, MyOrdersPickUpDetails::class.java)
        intent.putExtra("receipt", myordersnumber.receipt)
        Log.i("MYTAG", "${myordersnumber.receipt}")
        startActivity(intent)
    }
}

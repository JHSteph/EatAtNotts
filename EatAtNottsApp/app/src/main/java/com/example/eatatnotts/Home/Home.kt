package com.example.eatatnotts.Home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.NotificationService
import com.example.eatatnotts.databinding.FragmentHomeBinding

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var userRecyclerview: RecyclerView

    override fun onCreateView(//Create View
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {//When View Created, Start Notification and check whether list is empty or not
        super.onViewCreated(view, savedInstanceState)

        // Start the notification service
        val intent = Intent(requireContext(), NotificationService::class.java)
        requireActivity().startService(intent)

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        userRecyclerview = binding.list
        userRecyclerview.layoutManager = LinearLayoutManager(activity)
        userRecyclerview.setHasFixedSize(true)

        homeViewModel.newsList.observe(viewLifecycleOwner, { newsList ->
            userRecyclerview.adapter = NewsAdapter(newsList)
        })

        homeViewModel.emptyNews.observe(viewLifecycleOwner,{ emptyOrder ->
            if (emptyOrder) {
                binding.tvEmptyNews.text = "There is no news for now."
            } else {
                binding.tvEmptyNews.text = ""
            }
        })
    }
}

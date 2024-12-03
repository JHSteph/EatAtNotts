package com.example.eatatnotts.OrderToFood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.databinding.ActivityHawkerFoodListBinding
import com.example.eatatnotts.OrderToFood.FoodDetails

class HawkerFoodList : AppCompatActivity(), FoodAdapter.OnItemClickListener {
    private lateinit var binding: ActivityHawkerFoodListBinding
    private val viewModel: HawkerFoodListViewModel by viewModels()
    private lateinit var foodRecyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {//Creates View for HawkerFoodList Activity, and handle button logics
        super.onCreate(savedInstanceState)
        binding = ActivityHawkerFoodListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hawkerName = intent.getStringExtra("hawkerName")
        Log.i("MYTAG", hawkerName ?: "")
        foodRecyclerview = binding.Foodlist
        foodRecyclerview.layoutManager = LinearLayoutManager(this)
        foodRecyclerview.setHasFixedSize(true)
        binding.topAppBar.title = "${hawkerName} Foods"
        setSupportActionBar(binding.topAppBar)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow

        viewModel.foodList.observe(this, Observer { foodList ->
            foodRecyclerview.adapter = FoodAdapter(ArrayList(foodList), this)
        })

        hawkerName?.let {
            viewModel.getFoodData(it)
        }
    }

    override fun onItemClick(food: NewFood) {//Check Clicked Item And Start Another Activity
        val hawkerName = intent.getStringExtra("hawkerName")
        val intent = Intent(this, FoodDetails::class.java)
        intent.putExtra("hawkerName", hawkerName)
        intent.putExtra("foodName", food.foodName)
        intent.putExtra("price", "RM " + String.format("%.2f", food.price))
        intent.putExtra("photoUrl", food.photoUrl)
        startActivity(intent)
    }
}

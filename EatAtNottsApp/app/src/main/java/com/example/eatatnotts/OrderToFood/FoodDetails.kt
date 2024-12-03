package com.example.eatatnotts.OrderToFood

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.eatatnotts.OrderToFood.FoodDetailsViewModel
import com.example.eatatnotts.R
import com.example.eatatnotts.databinding.ActivityFoodDetailsBinding
import com.google.firebase.auth.FirebaseAuth

class FoodDetails : AppCompatActivity() {
    private lateinit var binding: ActivityFoodDetailsBinding
    private val viewModel: FoodDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {//Create View for FoodDetails Activity, and handle button logics
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hawkerName = intent.getStringExtra("hawkerName")
        val foodName = intent.getStringExtra("foodName")
        val price = intent.getStringExtra("price")
        val photoUrl = intent.getStringExtra("photoUrl")

        setupUI(hawkerName, foodName, price, photoUrl)
        observeViewModel()

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.faBtnPlus.setOnClickListener {
            viewModel.incrementQuantity()
        }

        binding.faBtnMinus.setOnClickListener {
            viewModel.decrementQuantity()
        }

        binding.btnAddToCart.setOnClickListener {
            val remarks = if (TextUtils.isEmpty(binding.etRemarks.text.toString())) {
                "-"
            } else {
                binding.etRemarks.text.toString()
            }
            val hawkerName = intent.getStringExtra("hawkerName").toString()
            val foodName = intent.getStringExtra("foodName").toString()
            val price = intent.getStringExtra("price").toString()
            val photoUrl = intent.getStringExtra("photoUrl").toString()
            Log.i("MYTAG","hawkerName is ${hawkerName},foodName is ${foodName}, price is ${price}, photoUrl is ${photoUrl}")
            viewModel.addToCart(remarks,hawkerName,foodName,price,photoUrl)
            observeSaveData()
        }
    }

    private fun setupUI(hawkerName: String?, foodName: String?, price: String?, photoUrl: String?) {//Update available food data into the view of CartDetails
        binding.topAppBar.title = "${hawkerName} Foods"
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.tvFoodDetailName.text = foodName
        binding.tvFoodDetailPrice.text = price
        Glide.with(this).load(photoUrl).into(binding.DetailfoodImage)
    }

    private fun observeViewModel() {//Observe ViewModel, and observe quantity of food
        viewModel.quantity.observe(this, Observer { quantity ->
            binding.tvAmount.text = quantity.toString()
        })
    }

    private fun observeSaveData(){//Check whether saving data is successful and show toast message
        viewModel.cartAdded.observe(this, Observer { success ->
            if (success==1) {
                Toast.makeText(this, "Successfully Added To Cart!", Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            } else if (success==2) {
                Toast.makeText(this, "Quantity must be more than 0!", Toast.LENGTH_SHORT).show()
            } else if (success==0) {
                Toast.makeText(this, "Cannot Connect To Cloud!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

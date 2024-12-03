package com.example.eatatnotts.Cart

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.eatatnotts.databinding.ActivityCartDetailsBinding
import com.example.eatatnotts.viewmodel.CartDetailViewModel

class CartDetails : AppCompatActivity() {
    private lateinit var binding: ActivityCartDetailsBinding
    private val cartViewModel: CartDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {//Create CartDetails Activity View, and handle button inputs
        super.onCreate(savedInstanceState)
        binding = ActivityCartDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hawkerName = intent.getStringExtra("hawkerName")
        val foodName = intent.getStringExtra("foodName")
        val price = intent.getStringExtra("price")
        val amount = intent.getIntExtra("quantity", 0).toString()
        val remarks = intent.getStringExtra("remarks")
        val photoUrl = intent.getStringExtra("photoUrl")

        Log.i("MYTAG","${remarks}")

        binding.topAppBar.title = hawkerName
        setSupportActionBar(binding.topAppBar)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show the back arrow

        cartViewModel.getFoodData(hawkerName, foodName, price, amount, remarks, photoUrl)
        cartViewModel.cartItem.observe(this, Observer { cartItem ->
            updateUI(cartItem)
        })
        cartViewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

        changeQuantity(amount,remarks)
        cartViewModel.navigateBack.observe(this, Observer { navigate ->
            if (navigate) {
                onBackPressedDispatcher.onBackPressed()
            }
        })
    }

    private fun updateUI(cartItem: CartItem) {//Update available food data into the view of CartDetails
        binding.tvCartDetailName.text = cartItem.foodName
        binding.tvCartDetailPrice.text = cartItem.price
        binding.tvCartAmount.text = cartItem.quantity.toString()
        if (cartItem.remarks == "-") {
            binding.etCartRemarks.setText("")
        } else {
            binding.etCartRemarks.setText(cartItem.remarks)
        }
        Glide.with(this)
            .load(cartItem.photoUrl)
            .into(binding.CartDetailfoodImage)
    }

    private fun changeQuantity(amount:String,remarks:String?) {// Change number of quantity based on the clicked button
        var AmtNumber = (amount).toInt()
        binding.tvCartAmount.text = AmtNumber.toString()
        binding.faBtnPlus.setOnClickListener {
            AmtNumber = cartViewModel.changeQuantity(AmtNumber, true)
            binding.tvCartAmount.text = AmtNumber.toString()
        }
        binding.faBtnMinus.setOnClickListener {
            AmtNumber = cartViewModel.changeQuantity(AmtNumber, false)
            binding.tvCartAmount.text = AmtNumber.toString()
        }
        binding.btnCartUpdate.setOnClickListener {
            val hawkerName = intent.getStringExtra("hawkerName").toString()
            val foodName = intent.getStringExtra("foodName").toString()
            val price = intent.getStringExtra("price").toString()
            val photoUrl = intent.getStringExtra("photoUrl").toString()
            val remarks = binding.etCartRemarks.text.toString()
            val previousremarks=intent.getStringExtra("remarks").toString()
            cartViewModel.addCart(AmtNumber, hawkerName, foodName, price, photoUrl, remarks,previousremarks)
        }
    }
}

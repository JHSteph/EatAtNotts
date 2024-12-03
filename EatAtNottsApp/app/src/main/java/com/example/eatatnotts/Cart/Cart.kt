package com.example.eatatnotts.Cart

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatatnotts.Payment.Payment
import com.example.eatatnotts.databinding.ActivityCartBinding
import com.example.eatatnotts.mainpage

class Cart : AppCompatActivity(), CartAdapter.OnItemClickListener {//Shows Cart of Customer
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {//Create view for Cart Activity, and handle buttons in Cart Activity
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.CarttopAppBar)

        binding.CarttopAppBar.setNavigationOnClickListener {
            navigateToMainPage()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToMainPage()
            }
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cartAdapter = CartAdapter(ArrayList(), this, cartViewModel)
        binding.Cartlist.apply {
            layoutManager = LinearLayoutManager(this@Cart)
            setHasFixedSize(true)
            adapter = cartAdapter
        }

        cartViewModel.cartItems.observe(this, Observer { cartItems ->
            cartAdapter.updateCartItems(cartItems)
        })

        cartViewModel.emptyCart.observe(this,{ emptyOrder ->
            if (emptyOrder) {
                binding.tvEmptyCart.text = "There is nothing in Cart now. \n" +
                        "Let's go and add some foods in the Order page!"
            } else {
                binding.tvEmptyCart.text = ""
            }
        })

        binding.btnCartRemove.setOnClickListener {
            val alertMessage = AlertDialog.Builder(this@Cart)
            alertMessage.setTitle("Food Removal Confirmation")
            alertMessage.setMessage("Are you sure you want to remove the checked foods?")
            alertMessage.setCancelable(false)
            alertMessage.setPositiveButton("Yes") { dialog, which ->
                checkIfAnyCheckBoxCheckedAndRemove()
            }

            alertMessage.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            alertMessage.show()
        }

        binding.btnPay.setOnClickListener {
            checkIfAnyCheckBoxChecked()
        }
    }

    private fun navigateToMainPage() {//Moves to mainpage if back button is pressed
        val intent = Intent(this@Cart, mainpage::class.java)
        startActivity(intent)
        finish()
    }

    override fun onItemClick(cart: CartItem) {//When clicking on one of the food item in RecyclerView
        val intent = Intent(this, CartDetails::class.java).apply {
            putExtra("hawkerName", cart.hawkerName)
            putExtra("foodName", cart.foodName)
            putExtra("price", cart.price)
            putExtra("quantity", cart.quantity)
            putExtra("remarks", cart.remarks)
            putExtra("photoUrl", cart.photoUrl)
        }
        startActivity(intent)
    }

    private fun checkIfAnyCheckBoxChecked() {//Check if any food item is checked for payment
        if (cartViewModel.cartItems.value?.any { it.checkbox } == true) {
            val intent = Intent(this, Payment::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please Select Something!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIfAnyCheckBoxCheckedAndRemove() {//Removes checked foods when btnCartRemove is pressed
        if (cartViewModel.cartItems.value?.any { it.checkbox } == true) {
            cartViewModel.removeCheckedItems()
        } else {
            Toast.makeText(this, "Please Select Something!", Toast.LENGTH_SHORT).show()
        }
    }
}

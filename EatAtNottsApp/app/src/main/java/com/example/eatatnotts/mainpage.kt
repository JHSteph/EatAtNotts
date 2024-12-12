package com.example.eatatnotts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.eatatnotts.Cart.Cart
import com.example.eatatnotts.Home.Home
import com.example.eatatnotts.MyOrders.MyOrders
import com.example.eatatnotts.Order.Order
import com.example.eatatnotts.Profile.Profile
import com.example.eatatnotts.databinding.ActivityMainpageBinding

class mainpage : AppCompatActivity() {//Mainpage for Customers, handle item selections of bottom navigation bar, and other button inputs
    private lateinit var binding: ActivityMainpageBinding
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainpageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())
        setSupportActionBar(binding.topAppBar)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish()
                    System.exit(0)
                }

                doubleBackToExitPressedOnce = true
                Toast.makeText(this@mainpage, "Please click back again to exit the app", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
                // Code that you need to execute on back press, e.g. finish()
            }
        })

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home->replaceFragment(Home())
                R.id.order->replaceFragment(Order())
                R.id.profile->replaceFragment(Profile())
                R.id.MyOrders->replaceFragment(MyOrders())
                else->{

                }

            }
            true
        }

        binding.faBtnCart.setOnClickListener{
            val intent = Intent(this, Cart::class.java)
            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.cart -> {
//                val intent = Intent(this, Cart::class.java)
//                startActivity(intent)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FrameLayout,fragment)
        fragmentTransaction.commit()
    }
}

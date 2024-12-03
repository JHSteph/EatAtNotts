package com.example.eatatnotts

import android.content.Intent
import com.example.eatatnotts.databinding.ActivityMainpageHawkerBinding
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.eatatnotts.AddFood.AddFood
import com.example.eatatnotts.Cart.Cart
import com.example.eatatnotts.CustomerOrder.CustomerOrder
import com.example.eatatnotts.Home.Home
import com.example.eatatnotts.Pending.Pending


class MainpageHawker : AppCompatActivity() {//Mainpage for hawkers, handle item selection of bottom navigation bar, and handle other button logics
    private lateinit var binding: ActivityMainpageHawkerBinding
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MYTAG","I'm here 4")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainpageHawkerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())
        setSupportActionBar(binding.HawkertopAppBar)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish()
                    System.exit(0)
                }

                doubleBackToExitPressedOnce = true
                Toast.makeText(this@MainpageHawker, "Please click back again to exit the app", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
                // Code that you need to execute on back press, e.g. finish()
            }
        })

        binding.bottomNavigationViewHawker.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home->replaceFragment(Home())
                R.id.customerorder->replaceFragment(CustomerOrder())
                R.id.profilehawker->replaceFragment(ProfileHawker())
                R.id.pending ->replaceFragment(Pending())
                else->{

                }
            }
            true
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.hawker_top_app_bar,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.AddActivity -> {
                val intent = Intent(this, AddActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.AddFood -> {
                val intent = Intent(this, AddFood::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FrameLayoutHawker,fragment)
        fragmentTransaction.commit()
    }
}
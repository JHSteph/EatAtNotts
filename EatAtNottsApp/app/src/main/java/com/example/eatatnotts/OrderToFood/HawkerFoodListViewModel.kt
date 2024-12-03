package com.example.eatatnotts.OrderToFood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class HawkerFoodListViewModel : ViewModel() {//Handles logic for HawkerFoodList Activity
    private val _foodList = MutableLiveData<List<NewFood>>()
    val foodList: LiveData<List<NewFood>> get() = _foodList

    private lateinit var dbref: DatabaseReference

    fun getFoodData(hawkerName: String?) {//Get Food Data from Firebase
        dbref = FirebaseDatabase.getInstance().getReference("Foods/$hawkerName")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val foodArrayList = arrayListOf<NewFood>()
                if (snapshot.exists()) {
                    for (foodSnapshot in snapshot.children) {
                        val foodItem = foodSnapshot.getValue(NewFood::class.java)
                        foodItem?.let { foodArrayList.add(it) }
                    }
                    _foodList.value = foodArrayList
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
}

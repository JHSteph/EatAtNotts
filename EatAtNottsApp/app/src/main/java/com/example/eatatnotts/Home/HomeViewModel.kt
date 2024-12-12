package com.example.eatatnotts.Home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class HomeViewModel : ViewModel() {//Handles logic for Home Fragment

    private val dbref: DatabaseReference = FirebaseDatabase.getInstance().getReference("Activity")
    private val _newsList = MutableLiveData<List<News>>()
    val newsList: LiveData<List<News>> get() = _newsList
    private val _emptyNews = MutableLiveData<Boolean>()
    val emptyNews: LiveData<Boolean> get() = _emptyNews

    init {//Do this
        fetchNewsData()
    }

    private fun fetchNewsData() {//Fetch news from Firebase Real-Time Database
        _emptyNews.value=false
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newsArrayList = arrayListOf<News>()
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        _emptyNews.value=false
                        val news = userSnapshot.getValue(News::class.java)
                        Log.i("MYTAG", "${news}")
                        news?.let { newsArrayList.add(it) }
                    }
                }else{
                    _emptyNews.value=true
                }
                _newsList.value = newsArrayList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors here if needed
            }
        })
    }
}

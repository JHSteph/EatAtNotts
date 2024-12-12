// AddFoodViewModel.kt
package com.example.eatatnotts.AddFood

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eatatnotts.OrderToFood.NewFood
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class AddFoodViewModel : ViewModel() {//ViewModel to provide functions for AddFood Activity
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    private val _uploadResult = MutableLiveData<Boolean>()
    val uploadResult: LiveData<Boolean> = _uploadResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _photoName = MutableLiveData<String>()
    val photoName: LiveData<String> = _photoName

    var photoUri: Uri? = null

    fun uploadPhotoUri(uri: Uri) {//Upload photo url for FoodList to search for food photo
        photoUri = uri
        _photoName.value = uri.lastPathSegment
    }

    fun uploadFoodData(foodName: String, foodDescription: String) {//Uploads a food data(foodName, foodDescription(Price), and Uri of food Photo
        photoUri?.let {
            val fileName = UUID.randomUUID().toString()
            val ref = storageReference.child("photos/$fileName")
            ref.putFile(it)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        saveFoodData(foodName, foodDescription.toDouble(), uri.toString())
                    }.addOnFailureListener { e ->
                        _errorMessage.value = "Failed to get download URL: ${e.message}"
                    }
                }
                .addOnFailureListener { e ->
                    _errorMessage.value = "Failed to upload photo: ${e.message}"
                }
        } ?: saveFoodData(foodName, foodDescription.toDouble(), null)
    }

    private fun saveFoodData(foodName: String, foodDescription: Double, photoUrl: String?) {//Save food data to Firebase Real-Time Database
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val dbRef = database.child("Users")
        val query: Query = dbRef.orderByChild("uid").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("username").getValue(String::class.java)
                        val foodRef = database.child("Foods").child("$userName/$foodName")
                        val newFood = NewFood(foodName, foodDescription, photoUrl)
                        foodRef.setValue(newFood)
                            .addOnSuccessListener {
                                _uploadResult.value = true
                            }
                            .addOnFailureListener {
                                _uploadResult.value = false
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = "Error fetching user data: ${error.message}"
            }
        })
    }
}

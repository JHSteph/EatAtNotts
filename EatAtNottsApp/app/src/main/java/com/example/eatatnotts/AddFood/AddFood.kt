package com.example.eatatnotts.AddFood

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.eatatnotts.MainpageHawker
import com.example.eatatnotts.databinding.ActivityAddFoodBinding
import com.github.dhaval2404.imagepicker.ImagePicker

class AddFood : AppCompatActivity() {// Create view and handle button inputs
    private lateinit var binding: ActivityAddFoodBinding
    private val viewModel: AddFoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {// onCreate creates view
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.HawkertopAppBar)
        binding.HawkertopAppBar.setNavigationOnClickListener {
            navigateToMainPage()
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToMainPage()
            }
        })

        binding.btnSelectPhoto.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .start()
        }

        binding.btnAddFood.setOnClickListener {
            val foodName = binding.etFoodName.text.toString()
            val foodDescription = binding.etDescription.text.toString()
            if (!TextUtils.isEmpty(foodName) && !TextUtils.isEmpty(foodDescription) && viewModel.photoUri != null) {
                showConfirmationDialog(foodName, foodDescription)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.uploadResult.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Successfully Saved New Food!", Toast.LENGTH_SHORT).show()
                binding.etFoodName.text.clear()
                binding.etDescription.text.clear()
                binding.tvPhotoName.text = ""
            } else {
                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })

        viewModel.photoName.observe(this, Observer { photoName ->
            binding.tvPhotoName.text = photoName
        })
    }

    private fun showConfirmationDialog(foodName: String, foodDescription: String) {
        val AlertMessage = AlertDialog.Builder(this@AddFood)
        AlertMessage.setTitle("Order Confirmation")
        AlertMessage.setMessage("Are you sure you want to add this new food?")
        AlertMessage.setCancelable(false)
        AlertMessage.setPositiveButton("Yes") { _, _ ->
            viewModel.uploadFoodData(foodName, foodDescription)
        }
        AlertMessage.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        AlertMessage.create().show()
    }

    private val imagePickerResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            viewModel.uploadPhotoUri(result.data!!.data!!)
        } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            viewModel.uploadPhotoUri(data.data!!)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMainPage() {
        val intent = Intent(this@AddFood, MainpageHawker::class.java)
        startActivity(intent)
        finish()
    }
}
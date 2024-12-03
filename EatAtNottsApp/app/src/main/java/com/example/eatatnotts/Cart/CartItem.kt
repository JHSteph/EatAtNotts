package com.example.eatatnotts.Cart

data class CartItem (//CartItem to house data of food in Cart
    val hawkerName: String = "",
    val foodName: String = "",
    val price:String="",
    val quantity: Int=0,
    val photoUrl: String = "",
    val remarks: String="",
    var checkbox: Boolean= false
)
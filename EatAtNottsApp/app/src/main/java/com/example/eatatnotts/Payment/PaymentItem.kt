package com.example.eatatnotts.Payment

data class PaymentItem(//Data to house information of payment item
    val hawkerName: String = "",
    val foodName: String = "",
    val price:String="",
    val remarks:String="",
    val quantity: Int=0)
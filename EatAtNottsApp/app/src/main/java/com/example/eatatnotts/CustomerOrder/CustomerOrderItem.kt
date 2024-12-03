package com.example.eatatnotts.CustomerOrder

data class CustomerOrderItem(//Data to house information of customer order
    val receipt: String?=null,
    val customerEmail: String?=null,
    val foodName: String?=null,
    val quantity: Int=0,
    val status:String?=null,
    val method:String?=null,
    val remarks:String?=null)

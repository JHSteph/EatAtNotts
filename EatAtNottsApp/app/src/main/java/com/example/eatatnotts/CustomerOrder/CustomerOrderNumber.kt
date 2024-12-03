package com.example.eatatnotts.CustomerOrder

data class CustomerOrderNumber(//Data to house information of Customer Pick Up Order Number
    val date:String?=null,
    var time:String?=null,
    val customerEmail: String?=null,
    val receipt: String?=null,
    val status:String?=null    )

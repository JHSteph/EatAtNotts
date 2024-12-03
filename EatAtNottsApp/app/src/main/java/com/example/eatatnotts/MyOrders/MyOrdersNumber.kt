package com.example.eatatnotts.MyOrders

data class MyOrdersNumber(//Data to house information of My Pick Up Order Number
    var date:String?=null,
    var time:String?=null,
    var customerEmail:String?=null,
    val receipt: String?=null,
    var method:String?=null)

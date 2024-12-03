package com.example.eatatnotts.MyOrders

data class MyOrdersNumberDelivery(//Data to house information of My Delivery Order Number
    var date:String?=null,
    var time:String?=null,
    var customerEmail:String?=null,
    val receipt: String?=null,
    var method:String?=null,
    var location:String?=null)

package com.example.eatatnotts.Pending

data class PendingDeliveryItem(//Data to house information of pending delivery order details
    val receipt: String?=null,
    val customerEmail: String="",
    val orderList: String="",
    val status: String="",
    val method: String="",
    val quantity:Int=0,
    val location:String?=null,
    val date:String?=null,
    val time:String?=null,
    val rejectreason:String?=null)

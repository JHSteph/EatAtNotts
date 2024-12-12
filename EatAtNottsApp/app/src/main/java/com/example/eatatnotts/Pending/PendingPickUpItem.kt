package com.example.eatatnotts.Pending

data class PendingPickUpItem(//Data to house information of pending pick up order details
    val receipt: String?=null,
    val customerEmail: String="",
    val date:String="",
    val time:String="",
    val orderList: String="",
    val status: String="",
    val method: String="",
    val quantity:Int=0,
    val rejectreason:String?=null)

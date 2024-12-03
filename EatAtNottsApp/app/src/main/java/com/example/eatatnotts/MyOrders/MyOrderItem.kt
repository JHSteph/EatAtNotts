package com.example.eatatnotts.MyOrders

data class MyOrderItem(//Data to house information of My Order Item
    val orderNo: String?=null,
    val hawker: String="",
    val orderList: String="",
    val status: String="",
    val method: String="",
    val quantity:Int=0,
    val remarks:String="",
    val rejectreason:String?=null
)

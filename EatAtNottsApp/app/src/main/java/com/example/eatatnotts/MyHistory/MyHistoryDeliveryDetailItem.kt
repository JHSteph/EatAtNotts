package com.example.eatatnotts.MyHistory

data class MyHistoryDeliveryDetailItem(//Data to house information of My History Delivery Detail
    val hawkerName:String?=null,
    val method:String?=null,
    val foodName:String?=null,
    val receipt:String?=null,
    val quantity:Int=0,
    val rejectreason:String?=null,
    val remarks:String?=null,
    val status:String?=null,
)

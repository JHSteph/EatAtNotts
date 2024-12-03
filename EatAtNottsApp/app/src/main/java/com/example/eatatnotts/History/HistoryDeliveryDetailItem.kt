package com.example.eatatnotts.History

data class HistoryDeliveryDetailItem(//Data to house information of History Delivery Detail
    val customerEmail:String?=null,
    val foodName:String?=null,
    val method:String?=null,
    val quantity:Int=0,
    val receipt:String?=null,
    val remarks:String?=null,
    val status:String?=null)

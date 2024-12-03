package com.example.eatatnotts.History

data class HistoryPickUpDetailItem(//Data to house information of History Pick Up Detail
    val customerEmail:String?=null,
    val foodName:String?=null,
    val method:String?=null,
    val quantity:Int=0,
    val receipt:String?=null,
    val remarks:String?=null,
    val status:String?=null
)

package com.example.eatatnotts.Pending

data class PendingItem (//Data to house information of pending order number
        val receipt: String?=null,
        val customerEmail: String?=null,
        val foodName: String?=null,
        val quantity: Int=0,
        val status:String?=null,
        val method:String?=null,
        val remarks:String?=null
)
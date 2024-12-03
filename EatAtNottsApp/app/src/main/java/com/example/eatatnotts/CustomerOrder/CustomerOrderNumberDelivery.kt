package com.example.eatatnotts.CustomerOrder

data class CustomerOrderNumberDelivery(//Data to house information of Customer Delivery Order Number
    val date:String?=null,
                                       var time:String?=null,
                                       val customerEmail: String?=null,
                                       val receipt: String?=null,
                                       val status:String?=null,
                                        val location:String?=null)

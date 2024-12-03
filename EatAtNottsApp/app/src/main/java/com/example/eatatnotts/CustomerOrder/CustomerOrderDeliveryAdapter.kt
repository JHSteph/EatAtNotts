package com.example.eatatnotts.CustomerOrder

import com.example.eatatnotts.MyOrders.MyOrdersNumber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class CustomerOrderDeliveryAdapter(private val CustomerOrdersList:ArrayList<CustomerOrderNumberDelivery>, private val listener: OnItemClickListener): RecyclerView.Adapter<CustomerOrderDeliveryAdapter.MyViewHolder>() {//Adapter to show RecyclerView for Customer Delivery Order

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(customerorderitem: CustomerOrderNumberDelivery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses customer_order_delivery layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.customer_order_delivery, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get CustomerOrderList size
        return CustomerOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from CustomerOrderNumberDelivery into View of MyViewHolder
        val currentitem = CustomerOrdersList[position]
        holder.CustomerEmail.text=currentitem.customerEmail
        holder.OrderNo.text = (currentitem.receipt).toString()
        holder.Status.text=currentitem.status
        holder.location.text=currentitem.location
        holder.time.text=currentitem.time

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val CustomerEmail: TextView = itemView.findViewById(R.id.tvCustomerOrderDeliveryCustomerEmail)
        val OrderNo: TextView = itemView.findViewById(R.id.tvCustomerOrderDeliveryOrderNo)
        val Status: TextView = itemView.findViewById(R.id.tvCustomerOrderDeliveryStatus)
        val location:TextView=itemView.findViewById(R.id.tvCustomerOrderDeliveryLocation)
        val time:TextView=itemView.findViewById(R.id.tvCustomerOrderDeliveryOrderTime)
    }
}

package com.example.eatatnotts.CustomerOrder

import com.example.eatatnotts.MyOrders.MyOrdersNumber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class CustomerOrderPickUpAdapter(private var CustomerOrdersList:ArrayList<CustomerOrderNumber>, private val listener: OnItemClickListener): RecyclerView.Adapter<CustomerOrderPickUpAdapter.MyViewHolder>() {//CustomerOrderPickUpAdapter to Create a RecyclerView

    interface OnItemClickListener {//Detect which item is clicked
        fun onItemClick(customerorderitem: CustomerOrderNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses customer_order layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.customer_order, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get CustomerOrdersList size
        return CustomerOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from CustomerOrderNumberPickUp into View of MyViewHolder
        val currentitem = CustomerOrdersList[position]
        holder.CustomerEmail.text=currentitem.customerEmail
        holder.OrderNo.text = (currentitem.receipt).toString()
        holder.Status.text=currentitem.status
        holder.Time.text=currentitem.time

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    fun updateOrders(newOrders: ArrayList<CustomerOrderNumber>) {//Updates the list of customer order
        CustomerOrdersList = newOrders
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val CustomerEmail: TextView = itemView.findViewById(R.id.tvCustomerOrderCustomerEmail)
        val OrderNo: TextView = itemView.findViewById(R.id.tvCustomerOrderOrderNo)
        val Status: TextView = itemView.findViewById(R.id.tvCustomerOrderStatus)
        val Time: TextView = itemView.findViewById(R.id.tvCustomerOrderTime)
    }
}

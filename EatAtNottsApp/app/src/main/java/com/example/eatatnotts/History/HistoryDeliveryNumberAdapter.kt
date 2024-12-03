package com.example.eatatnotts.History

import com.example.eatatnotts.MyOrders.MyOrdersNumber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class HistoryDeliveryNumberAdapter(private val HistoryDeliveryOrderList:ArrayList<HistoryDeliveryNumberItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<HistoryDeliveryNumberAdapter.MyViewHolder>() {//Adapter to show RecyclerView for History Delivery Order Number

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(historydeliverynumber: HistoryDeliveryNumberItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses customer_order_delivery layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.customer_order_delivery, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get HistoryDeliveryOrderList size
        return HistoryDeliveryOrderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from HistoryDeliveryNumberItem into View of MyViewHolder
        val currentitem = HistoryDeliveryOrderList[position]
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

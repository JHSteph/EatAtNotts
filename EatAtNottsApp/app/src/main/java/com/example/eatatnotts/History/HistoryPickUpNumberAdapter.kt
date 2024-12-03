package com.example.eatatnotts.History

import com.example.eatatnotts.MyOrders.MyOrdersNumber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class HistoryPickUpNumberAdapter(private val HistoryPickUpNumberList:ArrayList<HistoryPickUpNumberItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<HistoryPickUpNumberAdapter.MyViewHolder>() {//Adapter to show RecyclerView for History Pick Up Order Number

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(historypickupnumber: HistoryPickUpNumberItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses customer_order layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.customer_order, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get HistoryPickUpNumberList size
        return HistoryPickUpNumberList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from HistoryPickUpNumberItem into View of MyViewHolder
        val currentitem = HistoryPickUpNumberList[position]
        holder.CustomerEmail.text=currentitem.customerEmail
        holder.OrderNo.text = (currentitem.receipt).toString()
        holder.Status.text=currentitem.status
        holder.Time.text=currentitem.time

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val CustomerEmail: TextView = itemView.findViewById(R.id.tvCustomerOrderCustomerEmail)
        val OrderNo: TextView = itemView.findViewById(R.id.tvCustomerOrderOrderNo)
        val Status: TextView = itemView.findViewById(R.id.tvCustomerOrderStatus)
        val Time: TextView = itemView.findViewById(R.id.tvCustomerOrderTime)
    }
}

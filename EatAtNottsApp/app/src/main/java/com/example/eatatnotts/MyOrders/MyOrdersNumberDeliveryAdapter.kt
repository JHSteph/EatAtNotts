package com.example.eatatnotts.MyOrders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class MyOrdersNumberDeliveryAdapter(private var MyOrdersList:ArrayList<MyOrdersNumberDelivery>, private val listener: OnItemClickListener): RecyclerView.Adapter<MyOrdersNumberDeliveryAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(myordersnumber: MyOrdersNumberDelivery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.my_orders_number_delivery, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return MyOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = MyOrdersList[position]
        holder.OrderNo.text = (currentitem.receipt).toString()
        holder.Method.text=currentitem.method
        holder.location.text=currentitem.location
        holder.Date.text=currentitem.date
        holder.Time.text=currentitem.time

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    fun updateOrders(newOrdersList: List<MyOrdersNumberDelivery>) {
        MyOrdersList = ArrayList(newOrdersList)
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val OrderNo: TextView = itemView.findViewById(R.id.tvOrderNo)
        val Method: TextView = itemView.findViewById(R.id.tvMethod)
        val location: TextView=itemView.findViewById(R.id.tvLocation)
        val Date: TextView=itemView.findViewById(R.id.tvDate)
        val Time: TextView=itemView.findViewById(R.id.tvTime)
    }
}

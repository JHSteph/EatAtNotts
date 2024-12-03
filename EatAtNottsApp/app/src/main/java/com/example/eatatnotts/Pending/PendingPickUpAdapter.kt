package com.example.eatatnotts.Pending

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.Pending.PendingPickUpItem
import com.example.eatatnotts.R

class PendingPickUpAdapter(private var MyOrdersList:ArrayList<PendingPickUpItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<PendingPickUpAdapter.MyViewHolder>() {//Adapter to show RecyclerView for Pending Pick Up Order

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(pendingPickUp: PendingPickUpItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses .pending_item layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.pending_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get MyOrdersList size
        return MyOrdersList.size
    }

    fun updateItems(newItems: ArrayList<PendingPickUpItem>) {//Updates Data
        MyOrdersList = newItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from PendingPickUpItem into View of MyViewHolder
        val currentitem = MyOrdersList[position]
        holder.OrderNo.text = (currentitem.receipt).toString()
        holder.Status.text=currentitem.status
        holder.customerEmail.text=currentitem.customerEmail
        holder.Date.text=currentitem.date
        holder.Time.text=currentitem.time

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val OrderNo: TextView = itemView.findViewById(R.id.tvPendingOrderNo)
        val customerEmail: TextView = itemView.findViewById(R.id.tvPendingCustomerEmail)
        val Status: TextView = itemView.findViewById(R.id.tvPendingStatus)
        val Date: TextView=itemView.findViewById(R.id.tvPendingDate)
        val Time: TextView=itemView.findViewById(R.id.tvPendingTime)
    }
}

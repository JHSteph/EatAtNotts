package com.example.eatatnotts.Pending

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.Pending.PendingDeliveryItem
import com.example.eatatnotts.R

class PendingDeliveryDetailsAdapter(private val MyOrdersList:ArrayList<PendingItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<PendingDeliveryDetailsAdapter.MyViewHolder>() {//Adapter to show RecyclerView for Pending Delivery Order Details

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(pendingitem: PendingItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses pending_item_details layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.pending_item_details, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get MyOrdersList size
        return MyOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from PendingItem into View of MyViewHolder
        val currentitem = MyOrdersList[position]
        holder.FoodName.text=currentitem.foodName
        holder.Quantity.text=(currentitem.quantity).toString()
        holder.Remarks.text=currentitem.remarks

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val FoodName: TextView = itemView.findViewById(R.id.tvPendingDetailFoodName)
        val Quantity: TextView = itemView.findViewById(R.id.tvPendingDetailQuantity)
        val Remarks: TextView = itemView.findViewById(R.id.tvPendingDetailRemarks)
    }
}

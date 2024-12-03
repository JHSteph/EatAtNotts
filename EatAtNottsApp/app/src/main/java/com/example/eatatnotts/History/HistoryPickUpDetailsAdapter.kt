package com.example.eatatnotts.History

import com.example.eatatnotts.MyOrders.MyOrdersNumber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class HistoryPickUpDetailsAdapter(private val CustomerOrdersList:ArrayList<HistoryPickUpDetailItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<HistoryPickUpDetailsAdapter.MyViewHolder>() {//Adapter to show RecyclerView for History Pick Up Order Details

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(historypickupdetail: HistoryPickUpDetailItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses pick_up_details layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.pick_up_details, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get MyOrdersList size
        return CustomerOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from HistoryPickUpDetailItem into View of MyViewHolder
        val currentitem = CustomerOrdersList[position]
        holder.FoodName.text=currentitem.foodName
        holder.Quantity.text = (currentitem.quantity).toString()
        holder.Remarks.text = (currentitem.remarks).toString()

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val FoodName: TextView = itemView.findViewById(R.id.tvPickUpDetailsFoodName)
        val Quantity: TextView = itemView.findViewById(R.id.tvPickUpDetailsQuantity)
        val Remarks: TextView = itemView.findViewById(R.id.tvPickUpDetailsRemarks)
    }
}

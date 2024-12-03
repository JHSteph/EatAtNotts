package com.example.eatatnotts.History

import com.example.eatatnotts.MyOrders.MyOrdersNumber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class HistoryDeliveryDetailsAdapter(private val CustomerOrdersList:ArrayList<HistoryDeliveryDetailItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<HistoryDeliveryDetailsAdapter.MyViewHolder>() {//Adapter to show RecyclerView for History Delivery Order Details

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(customerorderitem: HistoryDeliveryDetailItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses delivery_details layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.delivery_details, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get MyOrdersList size
        return CustomerOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from HistoryDeliveryDetailItem into View of MyViewHolder
        val currentitem = CustomerOrdersList[position]
        holder.FoodName.text=currentitem.foodName
        holder.Quantity.text = (currentitem.quantity).toString()
        holder.Remarks.text = (currentitem.remarks).toString()

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val FoodName: TextView = itemView.findViewById(R.id.tvDeliveryDetailsFoodName)
        val Quantity: TextView = itemView.findViewById(R.id.tvDeliveryDetailsQuantity)
        val Remarks: TextView = itemView.findViewById(R.id.tvDeliveryDetailsRemarks)
    }
}

package com.example.eatatnotts.CustomerOrder

import com.example.eatatnotts.MyOrders.MyOrdersNumber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class DeliveryDetailsAdapter(private val CustomerOrdersList:ArrayList<CustomerOrderItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<DeliveryDetailsAdapter.MyViewHolder>() {//DeliveryDetailsAdapter to Create a RecyclerView

    interface OnItemClickListener {//Detect which item is clicked
        fun onItemClick(customerorderitem: CustomerOrderItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses delivery_details layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.delivery_details, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get CustomerOrdersList size
        return CustomerOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from CustomerOrderItem into View of MyViewHolder
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
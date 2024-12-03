package com.example.eatatnotts.MyHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class MyHistoryDeliveryDatesAdapter(private var MyOrdersList:ArrayList<MyHistoryDateItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<MyHistoryDeliveryDatesAdapter.MyViewHolder>() {//Adapter to show RecyclerView for My History Delivery Order Dates

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(myordersdate: MyHistoryDateItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses my_history_date_item layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.my_history_date_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get MyOrdersList size
        return MyOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from MyHistoryDateItem into View of MyViewHolder
        val currentitem = MyOrdersList[position]
        holder.Date.text=currentitem.date

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    fun updateData(newList: List<MyHistoryDateItem>) {//Updates Data
        MyOrdersList = ArrayList(newList)
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val Date: TextView = itemView.findViewById(R.id.tvMyHistoryDate)
    }
}

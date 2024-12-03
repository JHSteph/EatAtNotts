package com.example.eatatnotts.History

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.History.HistoryDateItem
import com.example.eatatnotts.R

class HistoryPickUpDatesAdapter(private var MyOrdersList:ArrayList<HistoryDateItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<HistoryPickUpDatesAdapter.MyViewHolder>() {//Adapter to show RecyclerView for History Pick Up Order

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(myordersdate: HistoryDateItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses history_date_item layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.history_date_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get MyOrdersList size
        return MyOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from HistoryDateItem into View of MyViewHolder
        val currentitem = MyOrdersList[position]
        holder.Date.text=currentitem.date

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    fun updateData(newData: List<HistoryDateItem>) {//Updates Data
        MyOrdersList = ArrayList(newData)
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val Date: TextView = itemView.findViewById(R.id.tvHistoryDate)
    }
}

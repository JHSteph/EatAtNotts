package com.example.eatatnotts.MyHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class MyHistoryPickUpNumberAdapter(private val MyOrdersList:ArrayList<MyHistoryPickUpNumberItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<MyHistoryPickUpNumberAdapter.MyViewHolder>() {//Adapter to show RecyclerView for My History Pick Up Order Number

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(myordersnumber: MyHistoryPickUpNumberItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses my_orders_number layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.my_orders_number, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get MyOrdersList size
        return MyOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from MyHistoryPickUpNumberItem into View of MyViewHolder
        val currentitem = MyOrdersList[position]
        holder.OrderNo.text = (currentitem.receipt).toString()
        holder.Method.text=currentitem.method
        holder.Date.text=currentitem.date
        holder.Time.text=currentitem.time

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val OrderNo: TextView = itemView.findViewById(R.id.tvOrderNo)
        val Method: TextView = itemView.findViewById(R.id.tvMethod)
        val Date: TextView=itemView.findViewById(R.id.tvDate)
        val Time: TextView=itemView.findViewById(R.id.tvTime)
    }
}

package com.example.eatatnotts.MyOrders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class MyOrdersAdapter(private val MyOrdersList:ArrayList<MyOrderItem>, private val listener: OnItemClickListener): RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder>() {//Adapter to show RecyclerView for My Order

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(myorder: MyOrderItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses my_order_item layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.my_order_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get MyOrdersList size
        return MyOrdersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from MyOrderItem into View of MyViewHolder
        val currentitem = MyOrdersList[position]
        holder.HawkerName.text = currentitem.hawker
        holder.OrderList.text = currentitem.orderList
        holder.Amount.text=(currentitem.quantity).toString()
        holder.Status.text=currentitem.status
        holder.Remarks.text=currentitem.remarks
        holder.RejectReason.text=currentitem.rejectreason

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val HawkerName: TextView = itemView.findViewById(R.id.tvHawkerMyOrderDetail)
        val OrderList: TextView = itemView.findViewById(R.id.tvOrderListDetail)
        val Amount: TextView = itemView.findViewById(R.id.tvAmountDetail)
        val Status: TextView = itemView.findViewById(R.id.tvStatusDetail)
        val RejectReason: TextView = itemView.findViewById(R.id.tvRejectDetail)
        val Remarks: TextView = itemView.findViewById(R.id.tvRemarksDetail)
    }
}

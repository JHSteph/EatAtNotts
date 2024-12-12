package com.example.eatatnotts.Payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class PaymentAdapter(
    private var paymentList: List<PaymentItem>
) : RecyclerView.Adapter<PaymentAdapter.MyViewHolder>() {//Adapter to show RecyclerView for Payment Items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses payment_item layout and holds view for each item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.payment_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get paymentList size
        return paymentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from PaymentItem into View of MyViewHolder
        val currentItem = paymentList[position]
        holder.PaymentHawkerName.text = currentItem.hawkerName
        holder.PaymentFoodName.text = currentItem.foodName
        holder.PaymentPrice.text = currentItem.price
        holder.Amount.text = currentItem.quantity.toString()
        holder.Remarks.text = currentItem.remarks
    }

    fun updatePaymentList(newPaymentList: List<PaymentItem>) {//Update Data
        paymentList = newPaymentList
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val PaymentHawkerName: TextView = itemView.findViewById(R.id.tvPaymentHawkerName)
        val PaymentFoodName: TextView = itemView.findViewById(R.id.tvPaymentFoodName)
        val PaymentPrice: TextView = itemView.findViewById(R.id.tvPaymentPrice)
        val Amount: TextView = itemView.findViewById(R.id.tvPaymentAmount)
        val Remarks: TextView = itemView.findViewById(R.id.tvPaymentRemarks)
    }
}

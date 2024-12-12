package com.example.eatatnotts.Order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class HawkerAdapter(private val HawkerList:ArrayList<HawkersList>, private val listener: OnItemClickListener): RecyclerView.Adapter<HawkerAdapter.MyViewHolder>() {//Adapter to show RecyclerView for Order Page

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(hawker: HawkersList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses hawker_list layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.hawker_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get HawkerList size
        return HawkerList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from HawkersList into View of MyViewHolder
        val currentitem = HawkerList[position]
        holder.Hawker.text = currentitem.username
        holder.Locations.text = currentitem.location
        holder.HawkerDescriptions.text = currentitem.hawkerDescription

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentitem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val Hawker: TextView = itemView.findViewById(R.id.tvHawker)
        val Locations: TextView = itemView.findViewById(R.id.tvLocation)
        val HawkerDescriptions: TextView = itemView.findViewById(R.id.tvHawkerDescription)
    }
}

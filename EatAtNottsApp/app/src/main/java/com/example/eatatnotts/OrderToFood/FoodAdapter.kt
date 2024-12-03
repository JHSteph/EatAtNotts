package com.example.eatatnotts.OrderToFood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eatatnotts.R

class FoodAdapter(private val foodList: ArrayList<NewFood>, private val listener: OnItemClickListener): RecyclerView.Adapter<FoodAdapter.MyViewHolder>() {//Adapter to show RecyclerView for Food offered by hawkers

    interface OnItemClickListener {//To detect which item is clicked
        fun onItemClick(food: NewFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses food_list layout and holds view for each item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.food_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get foodList size
        return foodList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from NewFood into View of MyViewHolder
        val currentItem = foodList[position]
        holder.foodName.text = currentItem.foodName
        holder.foodPrice.text = "RM " + String.format("%.2f", currentItem.price)

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.photoUrl)
            .into(holder.foodImage)

        holder.itemView.setOnClickListener {
            listener.onItemClick(currentItem)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val foodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val foodPrice: TextView = itemView.findViewById(R.id.tvFoodPrice)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
    }
}
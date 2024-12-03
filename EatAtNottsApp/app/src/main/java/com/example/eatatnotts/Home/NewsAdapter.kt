package com.example.eatatnotts.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class NewsAdapter(private val newsList: List<News>) : RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {//Adapter to show RecyclerView for News in Home Fragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Uses news_item layout and holds view for each item
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.news_items, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Get newsList size
        return newsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from News into View of MyViewHolder
        val currentItem = newsList[position]
        holder.Authors.text = currentItem.Author
        holder.ActivityNames.text = currentItem.ActivityName
        holder.Descriptions.text = currentItem.Description
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Binds variables into element of layout file
        val Authors: TextView = itemView.findViewById(R.id.tvAuthor)
        val ActivityNames: TextView = itemView.findViewById(R.id.tvHawkerName)
        val Descriptions: TextView = itemView.findViewById(R.id.tvHawkerDescription)
    }
}

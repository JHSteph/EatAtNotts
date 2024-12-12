package com.example.eatatnotts.Cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eatatnotts.R

class CartAdapter(
    private var cartList: List<CartItem>,
    private val listener: OnItemClickListener,
    private val cartViewModel: CartViewModel
) : RecyclerView.Adapter<CartAdapter.MyViewHolder>() {//CartAdapter use to list out cart items in RecyclerView

    interface OnItemClickListener {//To detect which item of RecyclerView is clicked
        fun onItemClick(cart: CartItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {//Create RecyclerView based on layout file
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {//Fetches total number of items in the cartList
        return cartList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {//Binds data from cartList to views in MyViewHolder
        val currentItem = cartList[position]
        holder.bind(currentItem)
    }

    fun updateCartItems(newCartItems: List<CartItem>) {//Updates the list of cart items
        cartList = newCartItems
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {//Bind data from CartItem to views in MyViewHoldder
        private val cartHawkerName: TextView = itemView.findViewById(R.id.tvCartHawkerName)
        private val cartFoodName: TextView = itemView.findViewById(R.id.tvCartFoodName)
        private val cartPrice: TextView = itemView.findViewById(R.id.tvCartPrice)
        private val amount: TextView = itemView.findViewById(R.id.tvCartAmount)
        private val remarks: TextView = itemView.findViewById(R.id.tvCartRemarks)
        private val cartCheckbox: CheckBox = itemView.findViewById(R.id.CartCheckbox)

        fun bind(cartItem: CartItem) {
            cartHawkerName.text = cartItem.hawkerName
            cartFoodName.text = cartItem.foodName
            cartPrice.text = cartItem.price
            amount.text = cartItem.quantity.toString()
            remarks.text = cartItem.remarks
            cartCheckbox.setOnCheckedChangeListener(null) // Unset listener to avoid unwanted calls
            cartCheckbox.isChecked = cartItem.checkbox

            itemView.setOnClickListener {
                listener.onItemClick(cartItem)
            }

            cartCheckbox.setOnCheckedChangeListener { _, isChecked ->
                // Update the local item
                cartItem.checkbox = isChecked

                // Update ViewModel
                cartViewModel.updateCheckbox(cartItem)
            }
        }
    }
}
